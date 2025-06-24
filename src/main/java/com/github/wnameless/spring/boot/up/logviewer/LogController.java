package com.github.wnameless.spring.boot.up.logviewer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier; // Import BooleanSupplier
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier; // Needed for @Qualifier
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException; // Import for throwing
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import jakarta.servlet.ServletContext; // Import ServletContext

@Controller
public class LogController {

  private static final Logger logger = LoggerFactory.getLogger(LogController.class);

  @Value("${logging.file.name:logs/application.log}")
  private String logFilePath;

  private final Set<SseEmitter> emitters = new CopyOnWriteArraySet<>();
  private long lastKnownFilePointer = 0;
  private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

  private final BooleanSupplier logViewerAccessSupplier;

  // Inject ServletContext to get the application's context path
  private final ServletContext servletContext;

  public LogController(
      @Autowired @Qualifier("logViewerAccessSupplier") BooleanSupplier logViewerAccessSupplier,
      @Autowired ServletContext servletContext) { // Inject ServletContext
    this.logViewerAccessSupplier = logViewerAccessSupplier;
    this.servletContext = servletContext; // Assign to field
    scheduler.scheduleAtFixedRate(this::tailLogFile, 0, 1, TimeUnit.SECONDS);
    logger.info("Log tailing scheduler started.");
  }

  /**
   * Serves the Thymeleaf HTML page for the log viewer. Access is first checked here using the
   * injected BooleanSupplier. The application's context path is added to the model for use in the
   * HTML.
   * 
   * @param model The model to pass attributes to the view.
   * @return The name of the Thymeleaf template.
   * @throws AccessDeniedException if the logViewerAccessSupplier condition is not met.
   */
  @GetMapping("/log-viewer")
  public String logViewer(Model model) {
    // Check the access condition before serving the view
    if (!logViewerAccessSupplier.getAsBoolean()) {
      logger.warn("Access denied for /log-viewer based on custom condition.");
      throw new AccessDeniedException("Access to log viewer is denied.");
    }
    // Add the context path to the model so it can be used in Thymeleaf to build URLs
    model.addAttribute("contextPath", servletContext.getContextPath());
    return "sbu/log-viewer/log-viewer";
  }

  /**
   * Establishes a Server-Sent Events (SSE) connection to stream log updates in real-time. Access is
   * first checked here using the injected BooleanSupplier.
   *
   * @return An SseEmitter instance for sending events to the client.
   * @throws AccessDeniedException if the logViewerAccessSupplier condition is not met.
   */
  @GetMapping(path = "/api/logs/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter streamLogs() {
    // Check the access condition before starting the SSE stream
    if (!logViewerAccessSupplier.getAsBoolean()) {
      logger.warn("Access denied for /api/logs/stream based on custom condition.");
      throw new AccessDeniedException("Access to log stream is denied.");
    }

    SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
    this.emitters.add(emitter);

    logger.info("New SSE emitter connected. Total emitters: {}", emitters.size());

    try {
      sendInitialLogContent(emitter);
    } catch (IOException e) {
      logger.error("Error sending initial log content to emitter: {}", e.getMessage());
      emitter.completeWithError(e);
      this.emitters.remove(emitter);
    }

    emitter.onCompletion(() -> {
      emitters.remove(emitter);
      logger.info("SSE emitter completed. Total emitters: {}", emitters.size());
    });
    emitter.onTimeout(() -> {
      emitters.remove(emitter);
      logger.warn("SSE emitter timed out. Total emitters: {}", emitters.size());
    });
    emitter.onError(e -> {
      emitters.remove(emitter);
      logger.error("SSE emitter error: {}. Total emitters: {}", e.getMessage(), emitters.size());
    });

    return emitter;
  }

  // --- Private helper methods (unchanged from previous version) ---
  private void sendInitialLogContent(SseEmitter emitter) throws IOException {
    File logFile = new File(logFilePath);
    if (logFile.exists() && logFile.isFile()) {
      StringBuilder fullLog = new StringBuilder();
      try (RandomAccessFile reader = new RandomAccessFile(logFile, "r")) {
        String line;
        while ((line = reader.readLine()) != null) {
          fullLog.append(line).append("\n");
        }
        lastKnownFilePointer = reader.getFilePointer();
      }
      emitter.send(SseEmitter.event().name("initial-log").data(fullLog.toString()));
      logger.info("Sent initial log content to a new emitter.");
    } else {
      emitter.send(SseEmitter.event().name("initial-log")
          .data("Log file not found at: " + logFilePath + "\n"));
      logger.warn("Log file not found at: {}", logFilePath);
    }
  }

  private void tailLogFile() {
    File logFile = new File(logFilePath);
    if (!logFile.exists() || !logFile.isFile()) {
      lastKnownFilePointer = 0;
      return;
    }

    try (RandomAccessFile reader = new RandomAccessFile(logFile, "r")) {
      long currentFileLength = reader.length();

      if (currentFileLength < lastKnownFilePointer) {
        logger.warn("Log file truncated. Resetting read pointer.");
        lastKnownFilePointer = 0;
      }

      reader.seek(lastKnownFilePointer);

      String line;
      while ((line = reader.readLine()) != null) {
        for (SseEmitter emitter : emitters) {
          try {
            emitter.send(SseEmitter.event().name("new-log-entry").data(line + "\n"));
          } catch (IOException e) {
            logger.warn("Failed to send log line to an emitter, removing it: {}", e.getMessage());
          }
        }
      }
      lastKnownFilePointer = reader.getFilePointer();
    } catch (IOException e) {
      logger.error("Error reading log file: {}", e.getMessage());
    }
  }

}
