package com.github.wnameless.spring.boot.up.attachment;

import com.github.wnameless.spring.boot.up.SpringBootUp;

public final class AttachmentI18nHelper {

  public static String getAttachmentTitle() {
    return SpringBootUp.getMessage("sbu.attachment.panel.attachment", null, "Attachment");
  }

  public static String getFileName() {
    return SpringBootUp.getMessage("sbu.attachment.panel.fileName", null, "File Name");
  }

  public static String getNote() {
    return SpringBootUp.getMessage("sbu.attachment.panel.note", null, "Note");
  }

}
