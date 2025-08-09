package com.github.wnameless.spring.boot.up.formdiff;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.github.wnameless.spring.boot.up.attachment.Attachment;
import com.github.wnameless.spring.boot.up.attachment.BasicAttachmentSnapshot;
import com.github.wnameless.spring.boot.up.jsf.RestfulAttachmentJsonSchemaForm;

class SimpleAttachmentDiffTest {

  @Test
  @SuppressWarnings({"unchecked", "rawtypes"})
  void testBasicAttachmentDiff() {
    // Create forms
    RestfulAttachmentJsonSchemaForm beforeForm = new RestfulAttachmentJsonSchemaForm();
    RestfulAttachmentJsonSchemaForm afterForm = new RestfulAttachmentJsonSchemaForm();

    // Set empty form data
    beforeForm.setFormData(new LinkedHashMap<>());
    afterForm.setFormData(new LinkedHashMap<>());

    // Create attachment snapshots
    BasicAttachmentSnapshot beforeSnapshot = new BasicAttachmentSnapshot();
    BasicAttachmentSnapshot afterSnapshot = new BasicAttachmentSnapshot();

    // Create simple attachment
    SimpleAttachment attachment1 = new SimpleAttachment();
    attachment1.id = "1";
    attachment1.group = "docs";
    attachment1.name = "test.pdf";
    attachment1.uri = URI.create("/old/test.pdf");

    SimpleAttachment attachment2 = new SimpleAttachment();
    attachment2.id = "1";
    attachment2.group = "docs";
    attachment2.name = "test.pdf";
    attachment2.uri = URI.create("/new/test.pdf"); // URI changed

    List beforeAttachments = new ArrayList();
    beforeAttachments.add(attachment1);
    beforeSnapshot.setAttachments(beforeAttachments);

    List afterAttachments = new ArrayList();
    afterAttachments.add(attachment2);
    afterSnapshot.setAttachments(afterAttachments);

    beforeForm.setAttachmentSnapshot(beforeSnapshot);
    afterForm.setAttachmentSnapshot(afterSnapshot);

    // Create and calculate diff
    RestfulAttachmentJsonSchemaFormDiff diff =
        new RestfulAttachmentJsonSchemaFormDiff(beforeForm, afterForm);
    diff.calculateDiff();

    // Get results
    RestfulAttachmentJsonSchemaForm beforeDiff = diff.getBeforeFormDiff();
    RestfulAttachmentJsonSchemaForm afterDiff = diff.getAfterFormDiff();

    // Verify
    List<Attachment> beforeDiffAttachments = beforeDiff.getAttachmentSnapshot().getAttachments();
    List<Attachment> afterDiffAttachments = afterDiff.getAttachmentSnapshot().getAttachments();

    assertEquals(1, beforeDiffAttachments.size());
    assertEquals(1, afterDiffAttachments.size());

    // Both should have bg-info since URI changed
    assertEquals("bg-info", beforeDiffAttachments.get(0).getUiClassNames());
    assertEquals("bg-info", afterDiffAttachments.get(0).getUiClassNames());
  }

  static class SimpleAttachment implements Attachment<String> {
    String id;
    String group;
    String name;
    URI uri;
    String note;
    LocalDateTime createdAt = LocalDateTime.now();
    String uiClassNames;

    @Override
    public String getId() {
      return id;
    }

    @Override
    public void setId(String id) {
      this.id = id;
    }

    @Override
    public String getGroup() {
      return group;
    }

    @Override
    public void setGroup(String group) {
      this.group = group;
    }

    @Override
    public String getName() {
      return name;
    }

    @Override
    public void setName(String name) {
      this.name = name;
    }

    @Override
    public URI getUri() {
      return uri;
    }

    @Override
    public void setUri(URI uri) {
      this.uri = uri;
    }

    @Override
    public String getNote() {
      return note;
    }

    @Override
    public void setNote(String note) {
      this.note = note;
    }

    @Override
    public LocalDateTime getCreatedAt() {
      return createdAt;
    }

    @Override
    public void setCreatedAt(LocalDateTime createdAt) {
      this.createdAt = createdAt;
    }

    @Override
    public String getUiClassNames() {
      return uiClassNames;
    }

    @Override
    public void setUiClassNames(String classNames) {
      this.uiClassNames = classNames;
    }
  }
}
