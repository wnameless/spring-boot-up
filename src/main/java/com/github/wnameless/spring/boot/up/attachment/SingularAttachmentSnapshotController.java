package com.github.wnameless.spring.boot.up.attachment;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import com.github.wnameless.spring.boot.up.web.RestfulItem;
import jakarta.servlet.http.HttpServletResponse;

public interface SingularAttachmentSnapshotController<AA extends AttachmentSnapshotProvider<A, ID> & RestfulItem<ID>, S extends AttachmentService<A, ID>, A extends Attachment<ID>, ID>
    extends AttachmentSnapshotControllerBase<AA, S, A, ID> {

  AA getAttachmentSnapshotProvider();

  @GetMapping("/attachments")
  default ModelAndView retrieveAttachments(ModelAndView mav, @RequestParam String ajaxTargetId) {
    return retrieveAttachmentsAction(mav, getAttachmentSnapshotProvider(), ajaxTargetId);
  }

  @GetMapping("/attachments/edit")
  default ModelAndView editAttachments(ModelAndView mav, @RequestParam String ajaxTargetId) {
    return editAttachmentsAction(mav, getAttachmentSnapshotProvider(), "", ajaxTargetId);
  }

  @GetMapping("/attachments/note")
  default ModelAndView noteAttachments(ModelAndView mav, @RequestParam String ajaxTargetId) {
    return noteAttachmentsAction(mav, getAttachmentSnapshotProvider(), "", ajaxTargetId);
  }

  @GetMapping("/attachments/upload")
  default ModelAndView uploadFragment(ModelAndView mav, @RequestParam String ajaxTargetId) {
    return uploadFragmentAction(mav, getAttachmentSnapshotProvider(), "", ajaxTargetId);
  }

  @PostMapping("/attachments")
  default ModelAndView uploadAttachments(ModelAndView mav,
      @RequestBody Map<String, Object> jsfFiles, @RequestParam String ajaxTargetId) {
    return uploadAttachmentsAction(mav, getAttachmentSnapshotProvider(), jsfFiles, ajaxTargetId);
  }

  @GetMapping(path = "/attachments/{attachmentId}")
  default void downloadAttachment(HttpServletResponse response, @PathVariable ID attachmentId) {
    downloadAttachmentAction(response, getAttachmentSnapshotProvider(), attachmentId);
  }

  @PutMapping(path = "/attachments")
  default ModelAndView modifyAttachments(ModelAndView mav,
      @RequestBody Map<String, Object> jsfFiles, @RequestParam String ajaxTargetId) {
    return modifyAttachmentsAction(mav, getAttachmentSnapshotProvider(), jsfFiles, ajaxTargetId);
  }

}
