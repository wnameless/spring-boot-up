package com.github.wnameless.spring.boot.up.jsf;

import java.util.LinkedHashMap;
import java.util.Map;
import com.github.wnameless.spring.boot.up.attachment.Attachment;
import com.github.wnameless.spring.boot.up.attachment.AttachmentChecklist;
import com.github.wnameless.spring.boot.up.attachment.BasicAttachmentSnapshot;
import com.github.wnameless.spring.boot.up.attachment.StatelessAttachmentSnapshotProvider;
import com.github.wnameless.spring.boot.up.web.RestfulItem;
import lombok.Data;

@Data
public class RestfulAttachmentJsonSchemaForm<A extends Attachment<ID>, ID>
    implements JsonSchemaForm, RestfulItem<ID>, StatelessAttachmentSnapshotProvider<A, ID> {

  private ID id;

  private Map<String, Object> formData = new LinkedHashMap<>();
  private Map<String, Object> schema = new LinkedHashMap<>();
  private Map<String, Object> uiSchema = new LinkedHashMap<>();

  private String basePath;
  private String indexPath;
  private String backPathname;

  private BasicAttachmentSnapshot<A, ID> attachmentSnapshot = new BasicAttachmentSnapshot<>();
  private AttachmentChecklist attachmentChecklist = new AttachmentChecklist();

  public RestfulAttachmentJsonSchemaForm() {}

  public RestfulAttachmentJsonSchemaForm(String basePath, ID id) {
    this.basePath = basePath;
    this.id = id;
    indexPath = basePath;
  }

  public RestfulAttachmentJsonSchemaForm(String basePath, ID id, String indexPath) {
    this.basePath = basePath;
    this.id = id;
    this.indexPath = indexPath;
  }

  @Override
  public Map<String, Object> getFormData() {
    return formData;
  }

  @Override
  public void setFormData(Map<String, Object> formData) {
    this.formData = formData;
  }

  @Override
  public Map<String, Object> getSchema() {
    return schema;
  }

  @Override
  public void setSchema(Map<String, Object> schema) {
    this.schema = schema;
  }

  @Override
  public Map<String, Object> getUiSchema() {
    return uiSchema;
  }

  @Override
  public void setUiSchema(Map<String, Object> uiSchema) {
    this.uiSchema = uiSchema;
  }

}
