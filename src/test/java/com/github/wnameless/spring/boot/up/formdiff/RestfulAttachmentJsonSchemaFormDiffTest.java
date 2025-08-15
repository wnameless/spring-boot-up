package com.github.wnameless.spring.boot.up.formdiff;

import static org.junit.jupiter.api.Assertions.*;
import java.net.URI;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.github.wnameless.spring.boot.up.attachment.Attachment;
import com.github.wnameless.spring.boot.up.attachment.BasicAttachmentSnapshot;
import com.github.wnameless.spring.boot.up.jsf.RestfulAttachmentJsonSchemaForm;

class RestfulAttachmentJsonSchemaFormDiffTest {

  private RestfulAttachmentJsonSchemaFormDiff<String> diff;
  private RestfulAttachmentJsonSchemaForm<?, String> beforeForm;
  private RestfulAttachmentJsonSchemaForm<?, String> afterForm;

  @BeforeEach
  void setUp() {
    beforeForm = new RestfulAttachmentJsonSchemaForm<>();
    afterForm = new RestfulAttachmentJsonSchemaForm<>();
  }

  @Test
  void testFieldDeleted() {
    // Setup: field exists in before but not in after
    Map<String, Object> beforeData = new LinkedHashMap<>();
    beforeData.put("name", "John");
    beforeData.put("age", 30);
    beforeForm.setFormData(beforeData);

    Map<String, Object> afterData = new LinkedHashMap<>();
    afterData.put("name", "John");
    // age is deleted
    afterForm.setFormData(afterData);

    // Create diff
    diff = new RestfulAttachmentJsonSchemaFormDiff<>(beforeForm, afterForm);
    diff.calculateDiff();

    // Verify
    RestfulAttachmentJsonSchemaForm<?, String> beforeDiff = diff.getBeforeFormDiff();
    // RestfulAttachmentJsonSchemaForm<?, String> afterDiff = diff.getAfterFormDiff();

    // Check that age field has danger color in beforeForm
    @SuppressWarnings("unchecked")
    Map<String, Object> ageUiSchema = (Map<String, Object>) beforeDiff.getUiSchema().get("age");
    assertNotNull(ageUiSchema);
    assertEquals("bg-danger", ageUiSchema.get("ui:classNames"));

    // Check that name field has no color (no change)
    assertNull(beforeDiff.getUiSchema().get("name"));
  }

  @Test
  void testFieldAdded() {
    // Setup: field doesn't exist in before but exists in after
    Map<String, Object> beforeData = new LinkedHashMap<>();
    beforeData.put("name", "John");
    beforeForm.setFormData(beforeData);

    Map<String, Object> afterData = new LinkedHashMap<>();
    afterData.put("name", "John");
    afterData.put("email", "john@example.com");
    afterForm.setFormData(afterData);

    // Create diff
    diff = new RestfulAttachmentJsonSchemaFormDiff<>(beforeForm, afterForm);
    diff.calculateDiff();

    // Verify
    RestfulAttachmentJsonSchemaForm<?, String> afterDiff = diff.getAfterFormDiff();

    // Check that email field has success color in afterForm
    @SuppressWarnings("unchecked")
    Map<String, Object> emailUiSchema = (Map<String, Object>) afterDiff.getUiSchema().get("email");
    assertNotNull(emailUiSchema);
    assertEquals("bg-success", emailUiSchema.get("ui:classNames"));
  }

  @Test
  void testFieldChanged() {
    // Setup: field value changed
    Map<String, Object> beforeData = new LinkedHashMap<>();
    beforeData.put("name", "John");
    beforeData.put("age", 30);
    beforeForm.setFormData(beforeData);

    Map<String, Object> afterData = new LinkedHashMap<>();
    afterData.put("name", "Jane");
    afterData.put("age", 30);
    afterForm.setFormData(afterData);

    // Create diff
    diff = new RestfulAttachmentJsonSchemaFormDiff<>(beforeForm, afterForm);
    diff.calculateDiff();

    // Verify
    RestfulAttachmentJsonSchemaForm<?, String> beforeDiff = diff.getBeforeFormDiff();
    RestfulAttachmentJsonSchemaForm<?, String> afterDiff = diff.getAfterFormDiff();

    // Check that name field has info color in both forms
    @SuppressWarnings("unchecked")
    Map<String, Object> beforeNameUiSchema =
        (Map<String, Object>) beforeDiff.getUiSchema().get("name");
    assertNotNull(beforeNameUiSchema);
    assertEquals("bg-info", beforeNameUiSchema.get("ui:classNames"));

    @SuppressWarnings("unchecked")
    Map<String, Object> afterNameUiSchema =
        (Map<String, Object>) afterDiff.getUiSchema().get("name");
    assertNotNull(afterNameUiSchema);
    assertEquals("bg-info", afterNameUiSchema.get("ui:classNames"));

    // Age should have no color (no change)
    assertNull(beforeDiff.getUiSchema().get("age"));
    assertNull(afterDiff.getUiSchema().get("age"));
  }

  @Test
  void testFieldTypeChanged() {
    // Setup: field type changed from number to string
    Map<String, Object> beforeData = new LinkedHashMap<>();
    beforeData.put("id", 123);
    beforeForm.setFormData(beforeData);

    Map<String, Object> afterData = new LinkedHashMap<>();
    afterData.put("id", "ABC123");
    afterForm.setFormData(afterData);

    // Create diff
    diff = new RestfulAttachmentJsonSchemaFormDiff<>(beforeForm, afterForm);
    diff.calculateDiff();

    // Verify
    RestfulAttachmentJsonSchemaForm<?, String> beforeDiff = diff.getBeforeFormDiff();
    RestfulAttachmentJsonSchemaForm<?, String> afterDiff = diff.getAfterFormDiff();

    // Check that id field has warning color in both forms
    @SuppressWarnings("unchecked")
    Map<String, Object> beforeIdUiSchema = (Map<String, Object>) beforeDiff.getUiSchema().get("id");
    assertNotNull(beforeIdUiSchema);
    assertEquals("bg-warning", beforeIdUiSchema.get("ui:classNames"));

    @SuppressWarnings("unchecked")
    Map<String, Object> afterIdUiSchema = (Map<String, Object>) afterDiff.getUiSchema().get("id");
    assertNotNull(afterIdUiSchema);
    assertEquals("bg-warning", afterIdUiSchema.get("ui:classNames"));
  }

  @Test
  void testMergeExistingClassNames() {
    // Setup: uiSchema already has classes
    Map<String, Object> beforeData = new LinkedHashMap<>();
    beforeData.put("name", "John");
    beforeForm.setFormData(beforeData);

    Map<String, Object> beforeUiSchema = new LinkedHashMap<>();
    Map<String, Object> nameUiSchema = new HashMap<>();
    nameUiSchema.put("ui:classNames", "custom-class required-field");
    beforeUiSchema.put("name", nameUiSchema);
    beforeForm.setUiSchema(beforeUiSchema);

    Map<String, Object> afterData = new LinkedHashMap<>();
    afterData.put("name", "Jane");
    afterForm.setFormData(afterData);
    afterForm.setUiSchema(new LinkedHashMap<>());

    // Create diff
    diff = new RestfulAttachmentJsonSchemaFormDiff<>(beforeForm, afterForm);
    diff.calculateDiff();

    // Verify
    RestfulAttachmentJsonSchemaForm<?, String> beforeDiff = diff.getBeforeFormDiff();

    // Check that existing classes are preserved and new class is added
    @SuppressWarnings("unchecked")
    Map<String, Object> nameUiSchemaResult =
        (Map<String, Object>) beforeDiff.getUiSchema().get("name");
    assertNotNull(nameUiSchemaResult);
    String classes = (String) nameUiSchemaResult.get("ui:classNames");
    assertNotNull(classes);
    assertTrue(classes.contains("custom-class"));
    assertTrue(classes.contains("required-field"));
    assertTrue(classes.contains("bg-info"));
  }

  @Test
  void testNestedFields() {
    // Setup: nested object fields
    Map<String, Object> beforeData = new LinkedHashMap<>();
    Map<String, Object> address = new LinkedHashMap<>();
    address.put("street", "123 Main St");
    address.put("city", "Boston");
    beforeData.put("address", address);
    beforeForm.setFormData(beforeData);

    Map<String, Object> afterData = new LinkedHashMap<>();
    Map<String, Object> newAddress = new LinkedHashMap<>();
    newAddress.put("street", "456 Oak Ave");
    newAddress.put("city", "Boston");
    newAddress.put("zip", "02101");
    afterData.put("address", newAddress);
    afterForm.setFormData(afterData);

    // Create diff
    diff = new RestfulAttachmentJsonSchemaFormDiff<>(beforeForm, afterForm);
    diff.calculateDiff();

    // Verify
    RestfulAttachmentJsonSchemaForm<?, String> beforeDiff = diff.getBeforeFormDiff();
    RestfulAttachmentJsonSchemaForm<?, String> afterDiff = diff.getAfterFormDiff();

    // Check nested street field has info color (changed)
    @SuppressWarnings("unchecked")
    Map<String, Object> addressSchema =
        (Map<String, Object>) beforeDiff.getUiSchema().get("address");
    assertNotNull(addressSchema);
    @SuppressWarnings("unchecked")
    Map<String, Object> streetSchema = (Map<String, Object>) addressSchema.get("street");
    assertNotNull(streetSchema);
    assertEquals("bg-info", streetSchema.get("ui:classNames"));

    // City should NOT have any color (unchanged)
    @SuppressWarnings("unchecked")
    Map<String, Object> citySchema = (Map<String, Object>) addressSchema.get("city");
    assertNull(citySchema); // No uiSchema created for unchanged field

    // The address object itself should NOT be highlighted
    assertNull(addressSchema.get("ui:classNames"));

    // Check zip field has success color in afterForm (added)
    @SuppressWarnings("unchecked")
    Map<String, Object> afterAddressSchema =
        (Map<String, Object>) afterDiff.getUiSchema().get("address");
    assertNotNull(afterAddressSchema);
    @SuppressWarnings("unchecked")
    Map<String, Object> zipSchema = (Map<String, Object>) afterAddressSchema.get("zip");
    assertNotNull(zipSchema);
    assertEquals("bg-success", zipSchema.get("ui:classNames"));

    // The address object itself should NOT be highlighted in afterForm either
    assertNull(afterAddressSchema.get("ui:classNames"));
  }

  @Test
  void testArrayFields() {
    // Setup: array fields with some changes
    Map<String, Object> beforeData = new LinkedHashMap<>();
    List<String> tags = new ArrayList<>();
    tags.add("java");
    tags.add("spring");
    tags.add("mongodb");
    beforeData.put("tags", tags);

    List<Map<String, Object>> items = new ArrayList<>();
    Map<String, Object> item1 = new LinkedHashMap<>();
    item1.put("id", 1);
    item1.put("name", "Item One");
    items.add(item1);

    Map<String, Object> item2 = new LinkedHashMap<>();
    item2.put("id", 2);
    item2.put("name", "Item Two");
    items.add(item2);

    beforeData.put("items", items);
    beforeForm.setFormData(beforeData);

    Map<String, Object> afterData = new LinkedHashMap<>();
    List<String> newTags = new ArrayList<>();
    newTags.add("java");
    newTags.add("spring-boot"); // Changed
    newTags.add("mongodb");
    newTags.add("docker"); // Added
    afterData.put("tags", newTags);

    List<Map<String, Object>> newItems = new ArrayList<>();
    Map<String, Object> newItem1 = new LinkedHashMap<>();
    newItem1.put("id", 1);
    newItem1.put("name", "Item One Updated"); // Changed
    newItems.add(newItem1);

    Map<String, Object> newItem2 = new LinkedHashMap<>();
    newItem2.put("id", 2);
    newItem2.put("name", "Item Two"); // Unchanged
    newItems.add(newItem2);

    Map<String, Object> newItem3 = new LinkedHashMap<>();
    newItem3.put("id", 3);
    newItem3.put("name", "Item Three"); // Added
    newItems.add(newItem3);

    afterData.put("items", newItems);
    afterForm.setFormData(afterData);

    // Create diff
    diff = new RestfulAttachmentJsonSchemaFormDiff<>(beforeForm, afterForm);
    diff.calculateDiff();

    // Verify
    RestfulAttachmentJsonSchemaForm<?, String> beforeDiff = diff.getBeforeFormDiff();
    RestfulAttachmentJsonSchemaForm<?, String> afterDiff = diff.getAfterFormDiff();

    // Check tags array - only changed items should be highlighted
    @SuppressWarnings("unchecked")
    Map<String, Object> tagsSchema = (Map<String, Object>) beforeDiff.getUiSchema().get("tags");
    assertNotNull(tagsSchema);

    // tags[1] changed from "spring" to "spring-boot"
    @SuppressWarnings("unchecked")
    Map<String, Object> tag1Schema = (Map<String, Object>) tagsSchema.get("items[1]");
    assertNotNull(tag1Schema);
    assertEquals("bg-info", tag1Schema.get("ui:classNames"));

    // tags[0] and tags[2] unchanged - should not be highlighted
    assertNull(tagsSchema.get("items[0]"));
    assertNull(tagsSchema.get("items[2]"));

    // In afterForm, tags[3] is added
    @SuppressWarnings("unchecked")
    Map<String, Object> afterTagsSchema = (Map<String, Object>) afterDiff.getUiSchema().get("tags");
    assertNotNull(afterTagsSchema);
    @SuppressWarnings("unchecked")
    Map<String, Object> tag3Schema = (Map<String, Object>) afterTagsSchema.get("items[3]");
    assertNotNull(tag3Schema);
    assertEquals("bg-success", tag3Schema.get("ui:classNames"));

    // Check items array - only changed properties should be highlighted
    @SuppressWarnings("unchecked")
    Map<String, Object> itemsSchema = (Map<String, Object>) beforeDiff.getUiSchema().get("items");
    assertNotNull(itemsSchema);

    // items[0].name changed
    @SuppressWarnings("unchecked")
    Map<String, Object> item0Schema = (Map<String, Object>) itemsSchema.get("items[0]");
    assertNotNull(item0Schema);
    @SuppressWarnings("unchecked")
    Map<String, Object> item0NameSchema = (Map<String, Object>) item0Schema.get("name");
    assertNotNull(item0NameSchema);
    assertEquals("bg-info", item0NameSchema.get("ui:classNames"));

    // items[0].id unchanged - should not be highlighted
    assertNull(item0Schema.get("id"));

    // items[1] completely unchanged - should not have any highlighting
    assertNull(itemsSchema.get("items[1]"));

    // In afterForm, items[2] is completely new
    @SuppressWarnings("unchecked")
    Map<String, Object> afterItemsSchema =
        (Map<String, Object>) afterDiff.getUiSchema().get("items");
    assertNotNull(afterItemsSchema);
    @SuppressWarnings("unchecked")
    Map<String, Object> item2AfterSchema = (Map<String, Object>) afterItemsSchema.get("items[2]");
    assertNotNull(item2AfterSchema);
    @SuppressWarnings("unchecked")
    Map<String, Object> item2IdSchema = (Map<String, Object>) item2AfterSchema.get("id");
    assertNotNull(item2IdSchema);
    assertEquals("bg-success", item2IdSchema.get("ui:classNames"));
    @SuppressWarnings("unchecked")
    Map<String, Object> item2NameSchema = (Map<String, Object>) item2AfterSchema.get("name");
    assertNotNull(item2NameSchema);
    assertEquals("bg-success", item2NameSchema.get("ui:classNames"));
  }

  @Test
  void testCompleteObjectAdditionAndDeletion() {
    // Setup: complete object added/deleted
    Map<String, Object> beforeData = new LinkedHashMap<>();
    Map<String, Object> config = new LinkedHashMap<>();
    config.put("timeout", 30);
    config.put("retries", 3);
    beforeData.put("config", config);
    beforeData.put("name", "Test");
    beforeForm.setFormData(beforeData);

    Map<String, Object> afterData = new LinkedHashMap<>();
    afterData.put("name", "Test");
    Map<String, Object> settings = new LinkedHashMap<>();
    settings.put("theme", "dark");
    settings.put("language", "en");
    afterData.put("settings", settings);
    afterForm.setFormData(afterData);

    // Create diff
    diff = new RestfulAttachmentJsonSchemaFormDiff<>(beforeForm, afterForm);
    diff.calculateDiff();

    // Verify
    RestfulAttachmentJsonSchemaForm<?, String> beforeDiff = diff.getBeforeFormDiff();
    RestfulAttachmentJsonSchemaForm<?, String> afterDiff = diff.getAfterFormDiff();

    // config object completely deleted - all its properties should be marked as deleted
    @SuppressWarnings("unchecked")
    Map<String, Object> configSchema = (Map<String, Object>) beforeDiff.getUiSchema().get("config");
    assertNotNull(configSchema);
    @SuppressWarnings("unchecked")
    Map<String, Object> timeoutSchema = (Map<String, Object>) configSchema.get("timeout");
    assertNotNull(timeoutSchema);
    assertEquals("bg-danger", timeoutSchema.get("ui:classNames"));
    @SuppressWarnings("unchecked")
    Map<String, Object> retriesSchema = (Map<String, Object>) configSchema.get("retries");
    assertNotNull(retriesSchema);
    assertEquals("bg-danger", retriesSchema.get("ui:classNames"));

    // settings object completely added - all its properties should be marked as added
    @SuppressWarnings("unchecked")
    Map<String, Object> settingsSchema =
        (Map<String, Object>) afterDiff.getUiSchema().get("settings");
    assertNotNull(settingsSchema);
    @SuppressWarnings("unchecked")
    Map<String, Object> themeSchema = (Map<String, Object>) settingsSchema.get("theme");
    assertNotNull(themeSchema);
    assertEquals("bg-success", themeSchema.get("ui:classNames"));
    @SuppressWarnings("unchecked")
    Map<String, Object> languageSchema = (Map<String, Object>) settingsSchema.get("language");
    assertNotNull(languageSchema);
    assertEquals("bg-success", languageSchema.get("ui:classNames"));
  }

  @Test
  void testLazyCalculation() {
    // Setup
    Map<String, Object> beforeData = new LinkedHashMap<>();
    beforeData.put("name", "John");
    beforeForm.setFormData(beforeData);

    Map<String, Object> afterData = new LinkedHashMap<>();
    afterData.put("name", "Jane");
    afterForm.setFormData(afterData);

    // Create diff but don't calculate yet
    diff = new RestfulAttachmentJsonSchemaFormDiff<>(beforeForm, afterForm);

    assertFalse(diff.isDiffCalculated());

    // Getting beforeFormDiff should trigger calculation
    diff.getBeforeFormDiff();
    assertTrue(diff.isDiffCalculated());
  }

  @Test
  @SuppressWarnings({"unchecked", "rawtypes"})
  void testAttachmentFileUpdate() {
    // Setup: same attachment but URI changed (file updated)
    BasicAttachmentSnapshot beforeSnapshot = new BasicAttachmentSnapshot();
    List beforeAttachments = new ArrayList();
    beforeAttachments.add(createAttachment("1", "documents", "report.pdf",
        URI.create("/files/old/report.pdf"), "Monthly report"));
    beforeSnapshot.setAttachments(beforeAttachments);
    beforeForm.setAttachmentSnapshot(beforeSnapshot);
    beforeForm.setFormData(new LinkedHashMap<>());

    BasicAttachmentSnapshot afterSnapshot = new BasicAttachmentSnapshot();
    List afterAttachments = new ArrayList();
    afterAttachments.add(createAttachment("1", "documents", "report.pdf",
        URI.create("/files/new/report.pdf"), "Monthly report"));
    afterSnapshot.setAttachments(afterAttachments);
    afterForm.setAttachmentSnapshot(afterSnapshot);
    afterForm.setFormData(new LinkedHashMap<>());

    // Create diff
    diff = new RestfulAttachmentJsonSchemaFormDiff<>(beforeForm, afterForm);
    diff.calculateDiff();

    // Verify both forms show the attachment with info color (file updated)
    RestfulAttachmentJsonSchemaForm<?, String> beforeDiff = diff.getBeforeFormDiff();
    RestfulAttachmentJsonSchemaForm<?, String> afterDiff = diff.getAfterFormDiff();

    Attachment<?> beforeAttachment = beforeDiff.getAttachmentSnapshot().getAttachments().get(0);
    Attachment<?> afterAttachment = afterDiff.getAttachmentSnapshot().getAttachments().get(0);

    assertEquals("bg-info", beforeAttachment.getUiClassNames());
    assertEquals("bg-info", afterAttachment.getUiClassNames());
  }

  @Test
  @SuppressWarnings({"unchecked", "rawtypes"})
  void testAttachmentAddition() {
    // Setup: new attachment added in afterForm
    BasicAttachmentSnapshot beforeSnapshot = new BasicAttachmentSnapshot();
    List beforeAttachments = new ArrayList();
    beforeAttachments.add(createAttachment("1", "documents", "report.pdf",
        URI.create("/files/report.pdf"), "Monthly report"));
    beforeSnapshot.setAttachments(beforeAttachments);
    beforeForm.setAttachmentSnapshot(beforeSnapshot);
    beforeForm.setFormData(new LinkedHashMap<>());

    BasicAttachmentSnapshot afterSnapshot = new BasicAttachmentSnapshot();
    List afterAttachments = new ArrayList();
    afterAttachments.add(createAttachment("1", "documents", "report.pdf",
        URI.create("/files/report.pdf"), "Monthly report"));
    afterAttachments.add(createAttachment("2", "images", "diagram.png",
        URI.create("/files/diagram.png"), "System diagram"));
    afterSnapshot.setAttachments(afterAttachments);
    afterForm.setAttachmentSnapshot(afterSnapshot);
    afterForm.setFormData(new LinkedHashMap<>());

    // Create diff
    diff = new RestfulAttachmentJsonSchemaFormDiff<>(beforeForm, afterForm);
    diff.calculateDiff();

    // Verify
    RestfulAttachmentJsonSchemaForm<?, String> beforeDiff = diff.getBeforeFormDiff();
    RestfulAttachmentJsonSchemaForm<?, String> afterDiff = diff.getAfterFormDiff();

    // First attachment should have no changes
    assertNull(beforeDiff.getAttachmentSnapshot().getAttachments().get(0).getUiClassNames());
    assertNull(afterDiff.getAttachmentSnapshot().getAttachments().get(0).getUiClassNames());

    // Second attachment should be marked as added (success) only in afterForm
    assertEquals("bg-success",
        afterDiff.getAttachmentSnapshot().getAttachments().get(1).getUiClassNames());
  }

  @Test
  @SuppressWarnings({"unchecked", "rawtypes"})
  void testAttachmentDeletion() {
    // Setup: attachment removed in afterForm
    BasicAttachmentSnapshot beforeSnapshot = new BasicAttachmentSnapshot();
    List beforeAttachments = new ArrayList();
    beforeAttachments.add(createAttachment("1", "documents", "report.pdf",
        URI.create("/files/report.pdf"), "Monthly report"));
    beforeAttachments.add(createAttachment("2", "images", "diagram.png",
        URI.create("/files/diagram.png"), "System diagram"));
    beforeSnapshot.setAttachments(beforeAttachments);
    beforeForm.setAttachmentSnapshot(beforeSnapshot);
    beforeForm.setFormData(new LinkedHashMap<>());

    BasicAttachmentSnapshot afterSnapshot = new BasicAttachmentSnapshot();
    List afterAttachments = new ArrayList();
    afterAttachments.add(createAttachment("1", "documents", "report.pdf",
        URI.create("/files/report.pdf"), "Monthly report"));
    afterSnapshot.setAttachments(afterAttachments);
    afterForm.setAttachmentSnapshot(afterSnapshot);
    afterForm.setFormData(new LinkedHashMap<>());

    // Create diff
    diff = new RestfulAttachmentJsonSchemaFormDiff<>(beforeForm, afterForm);
    diff.calculateDiff();

    // Verify
    RestfulAttachmentJsonSchemaForm<?, String> beforeDiff = diff.getBeforeFormDiff();
    RestfulAttachmentJsonSchemaForm<?, String> afterDiff = diff.getAfterFormDiff();

    // First attachment should have no changes
    assertNull(beforeDiff.getAttachmentSnapshot().getAttachments().get(0).getUiClassNames());
    assertNull(afterDiff.getAttachmentSnapshot().getAttachments().get(0).getUiClassNames());

    // Second attachment should be marked as deleted (danger) only in beforeForm
    assertEquals("bg-danger",
        beforeDiff.getAttachmentSnapshot().getAttachments().get(1).getUiClassNames());
  }

  @Test
  @SuppressWarnings({"unchecked", "rawtypes"})
  void testAttachmentNoteChange() {
    // Setup: same attachment but note changed
    BasicAttachmentSnapshot beforeSnapshot = new BasicAttachmentSnapshot();
    List beforeAttachments = new ArrayList();
    beforeAttachments.add(createAttachment("1", "documents", "report.pdf",
        URI.create("/files/report.pdf"), "Monthly report"));
    beforeSnapshot.setAttachments(beforeAttachments);
    beforeForm.setAttachmentSnapshot(beforeSnapshot);
    beforeForm.setFormData(new LinkedHashMap<>());

    BasicAttachmentSnapshot afterSnapshot = new BasicAttachmentSnapshot();
    List afterAttachments = new ArrayList();
    afterAttachments.add(createAttachment("1", "documents", "report.pdf",
        URI.create("/files/report.pdf"), "Quarterly report")); // Note changed
    afterSnapshot.setAttachments(afterAttachments);
    afterForm.setAttachmentSnapshot(afterSnapshot);
    afterForm.setFormData(new LinkedHashMap<>());

    // Create diff
    diff = new RestfulAttachmentJsonSchemaFormDiff<>(beforeForm, afterForm);
    diff.calculateDiff();

    // Verify both forms show the attachment with info color (note changed)
    RestfulAttachmentJsonSchemaForm<?, String> beforeDiff = diff.getBeforeFormDiff();
    RestfulAttachmentJsonSchemaForm<?, String> afterDiff = diff.getAfterFormDiff();

    Attachment<?> beforeAttachment = beforeDiff.getAttachmentSnapshot().getAttachments().get(0);
    Attachment<?> afterAttachment = afterDiff.getAttachmentSnapshot().getAttachments().get(0);

    assertEquals("bg-info", beforeAttachment.getUiClassNames());
    assertEquals("bg-info", afterAttachment.getUiClassNames());
  }

  @Test
  @SuppressWarnings({"unchecked", "rawtypes"})
  void testAttachmentGroupChange() {
    // Setup: same attachment but group changed
    BasicAttachmentSnapshot beforeSnapshot = new BasicAttachmentSnapshot();
    List beforeAttachments = new ArrayList();
    beforeAttachments.add(createAttachment("1", "documents", "report.pdf",
        URI.create("/files/report.pdf"), "Monthly report"));
    beforeSnapshot.setAttachments(beforeAttachments);
    beforeForm.setAttachmentSnapshot(beforeSnapshot);
    beforeForm.setFormData(new LinkedHashMap<>());

    BasicAttachmentSnapshot afterSnapshot = new BasicAttachmentSnapshot();
    List afterAttachments = new ArrayList();
    afterAttachments.add(createAttachment("1", "archive", "report.pdf", // Group changed
        URI.create("/files/report.pdf"), "Monthly report"));
    afterSnapshot.setAttachments(afterAttachments);
    afterForm.setAttachmentSnapshot(afterSnapshot);
    afterForm.setFormData(new LinkedHashMap<>());

    // Create diff
    diff = new RestfulAttachmentJsonSchemaFormDiff<>(beforeForm, afterForm);
    diff.calculateDiff();

    // Verify both forms show the attachment with warning color (structural change)
    RestfulAttachmentJsonSchemaForm<?, String> beforeDiff = diff.getBeforeFormDiff();
    RestfulAttachmentJsonSchemaForm<?, String> afterDiff = diff.getAfterFormDiff();

    Attachment<?> beforeAttachment = beforeDiff.getAttachmentSnapshot().getAttachments().get(0);
    Attachment<?> afterAttachment = afterDiff.getAttachmentSnapshot().getAttachments().get(0);

    assertEquals("bg-warning", beforeAttachment.getUiClassNames());
    assertEquals("bg-warning", afterAttachment.getUiClassNames());
  }

  @Test
  @SuppressWarnings({"unchecked", "rawtypes"})
  void testMultipleAttachmentChanges() {
    // Setup: multiple attachments with mixed changes
    BasicAttachmentSnapshot beforeSnapshot = new BasicAttachmentSnapshot();
    List beforeAttachments = new ArrayList();
    beforeAttachments.add(createAttachment("1", "documents", "report.pdf",
        URI.create("/files/report.pdf"), "Monthly report"));
    beforeAttachments.add(createAttachment("2", "images", "diagram.png",
        URI.create("/files/old/diagram.png"), "System diagram"));
    beforeAttachments.add(createAttachment("3", "documents", "spec.doc",
        URI.create("/files/spec.doc"), "Specification"));
    beforeSnapshot.setAttachments(beforeAttachments);
    beforeForm.setAttachmentSnapshot(beforeSnapshot);
    beforeForm.setFormData(new LinkedHashMap<>());

    BasicAttachmentSnapshot afterSnapshot = new BasicAttachmentSnapshot();
    List afterAttachments = new ArrayList();
    afterAttachments.add(createAttachment("1", "documents", "report.pdf",
        URI.create("/files/report.pdf"), "Monthly report")); // Unchanged
    afterAttachments.add(createAttachment("2", "images", "diagram.png",
        URI.create("/files/new/diagram.png"), "System diagram")); // URI changed
    // Attachment 3 deleted
    afterAttachments.add(createAttachment("4", "presentations", "slides.ppt",
        URI.create("/files/slides.ppt"), "Presentation")); // New attachment
    afterSnapshot.setAttachments(afterAttachments);
    afterForm.setAttachmentSnapshot(afterSnapshot);
    afterForm.setFormData(new LinkedHashMap<>());

    // Create diff
    diff = new RestfulAttachmentJsonSchemaFormDiff<>(beforeForm, afterForm);
    diff.calculateDiff();

    // Verify
    RestfulAttachmentJsonSchemaForm<?, String> beforeDiff = diff.getBeforeFormDiff();
    RestfulAttachmentJsonSchemaForm<?, String> afterDiff = diff.getAfterFormDiff();

    // Attachment 1: unchanged
    assertNull(beforeDiff.getAttachmentSnapshot().getAttachments().get(0).getUiClassNames());
    assertNull(afterDiff.getAttachmentSnapshot().getAttachments().get(0).getUiClassNames());

    // Attachment 2: URI changed (info)
    assertEquals("bg-info",
        beforeDiff.getAttachmentSnapshot().getAttachments().get(1).getUiClassNames());
    assertEquals("bg-info",
        afterDiff.getAttachmentSnapshot().getAttachments().get(1).getUiClassNames());

    // Attachment 3: deleted (danger) - only in beforeForm
    assertEquals("bg-danger",
        beforeDiff.getAttachmentSnapshot().getAttachments().get(2).getUiClassNames());

    // Attachment 4: added (success) - only in afterForm
    assertEquals("bg-success",
        afterDiff.getAttachmentSnapshot().getAttachments().get(2).getUiClassNames());
  }

  // Helper method to create test attachments
  private Attachment<String> createAttachment(String id, String group, String name, URI uri,
      String note) {
    return new Attachment<String>() {
      private String attachmentId = id;
      private String attachmentGroup = group;
      private String attachmentName = name;
      private URI attachmentUri = uri;
      private String attachmentNote = note;
      private LocalDateTime createdAt = LocalDateTime.now(Clock.systemUTC());
      private String uiClassNames;

      @Override
      public String getId() {
        return attachmentId;
      }

      @Override
      public void setId(String id) {
        this.attachmentId = id;
      }

      @Override
      public String getGroup() {
        return attachmentGroup;
      }

      @Override
      public void setGroup(String group) {
        this.attachmentGroup = group;
      }

      @Override
      public String getName() {
        return attachmentName;
      }

      @Override
      public void setName(String name) {
        this.attachmentName = name;
      }

      @Override
      public URI getUri() {
        return attachmentUri;
      }

      @Override
      public void setUri(URI uri) {
        this.attachmentUri = uri;
      }

      @Override
      public String getNote() {
        return attachmentNote;
      }

      @Override
      public void setNote(String note) {
        this.attachmentNote = note;
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
    };
  }
}
