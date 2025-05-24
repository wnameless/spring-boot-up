package com.github.wnameless.spring.boot.up.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class AuditableDocumentTest {

  @Test
  public void testAuditableDocument() {
    var doc1 = new BaseAuditableDocument();
    doc1.setF1("v1");
    doc1.setF2(123);
    var doc2 = new BaseAuditableDocument();
    doc2.setF1("v2");
    doc2.setF2(456);

    assertEquals(doc1, doc2);
    doc1.setId("123");
    assertNotEquals(doc1, doc2);
    doc2.setId("123");
    assertEquals(doc1, doc2);
  }

}
