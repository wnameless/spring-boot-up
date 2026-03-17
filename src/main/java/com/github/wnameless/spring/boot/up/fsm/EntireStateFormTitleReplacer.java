package com.github.wnameless.spring.boot.up.fsm;

public interface EntireStateFormTitleReplacer {

  String getTitle(PhaseProvider<?, ?, ?, ?> pp, String formType, String formBranch);

}
