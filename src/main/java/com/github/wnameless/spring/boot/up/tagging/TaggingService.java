package com.github.wnameless.spring.boot.up.tagging;

public interface TaggingService<T extends LabelTag<I, ID>, I extends TagTemplate, ID> {

  TagTemplateRepository<I, ID> getUserEntityTagListItemRepository();

  LabelTagRepositiry<T, I, ID> getUserEntityTagRepository();

}
