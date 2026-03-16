package com.yourgame.survival.editor;

public final class EditorStampMasks {
  private EditorStampMasks() {}

  public static String nodeMaskName(String nodeType) {
    String t = (nodeType == null || nodeType.isBlank()) ? "TREE" : nodeType.trim().toUpperCase();
    return "STAMP_NODE_" + t;
  }

  public static String encounterMaskName(String entityType) {
    String t = (entityType == null || entityType.isBlank()) ? "ORK_GRUNT" : entityType.trim().toUpperCase();
    return "STAMP_ENCOUNTER_" + t;
  }
}
