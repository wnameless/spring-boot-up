package com.github.wnameless.spring.boot.up.attachment;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import com.github.wnameless.spring.boot.up.web.RestfulItemProvider;
import jakarta.servlet.http.HttpServletResponse;

public interface AttachmentSnapshotController<AA extends AttachmentSnapshotProvider<A, ID>, S extends AttachmentService<A, ID>, A extends Attachment<ID>, ID>
    extends AttachmentSnapshotControllerBase<S, A, ID>, RestfulItemProvider<AA> {

  default AA getAttachmentSnapshotProvider(ID id) {
    return getRestfulItem();
  }

  @GetMapping("/{id}/attachments")
  default ModelAndView retrieveAttachments(ModelAndView mav, @PathVariable ID id,
      String ajaxTargetId) {
    return retrieveAttachmentsAction(mav, getAttachmentSnapshotProvider(id), ajaxTargetId);
  }

  @GetMapping("/{id}/attachments/edit")
  default ModelAndView editAttachments(ModelAndView mav, @PathVariable ID id, String ajaxTargetId) {
    return editAttachmentsAction(mav, getAttachmentSnapshotProvider(id), id.toString(),
        ajaxTargetId);
  }

  @GetMapping("/{id}/attachments/note")
  default ModelAndView noteAttachments(ModelAndView mav, @PathVariable ID id, String ajaxTargetId) {
    return noteAttachmentsAction(mav, getAttachmentSnapshotProvider(id), id.toString(),
        ajaxTargetId);
  }

  @GetMapping("/{id}/attachments/upload")
  default ModelAndView uploadFragment(ModelAndView mav, @PathVariable ID id, String ajaxTargetId) {
    return uploadFragmentAction(mav, getAttachmentSnapshotProvider(id), id.toString(),
        ajaxTargetId);
  }

  @PostMapping("/{id}/attachments")
  default ModelAndView uploadAttachments(ModelAndView mav, @PathVariable ID id,
      @RequestBody Map<String, Object> jsfFiles, String ajaxTargetId) {
    return uploadAttachmentsAction(mav, getAttachmentSnapshotProvider(id), jsfFiles, ajaxTargetId);
  }

  @GetMapping(path = "/{id}/attachments/{attachmentId}")
  default void downloadAttachment(HttpServletResponse response, @PathVariable ID id,
      @PathVariable ID attachmentId) {
    downloadAttachmentAction(response, getAttachmentSnapshotProvider(id), attachmentId);
  }

  @PutMapping(path = "/{id}/attachments")
  default ModelAndView modifyAttachments(ModelAndView mav, @PathVariable ID id,
      @RequestBody Map<String, Object> jsfFiles, @RequestParam String ajaxTargetId) {
    return modifyAttachmentsAction(mav, getAttachmentSnapshotProvider(id), jsfFiles, ajaxTargetId);
  }

}
