package com.yourgame.survival.quest;

/** Quest definition (minimal for Task 9). */
public final class QuestDef {

  public enum Kind {
    MAIN,
    SIDE
  }

  public final String id;
  public final Kind kind;
  public final String title;
  public final String desc;

  public QuestDef(String id, Kind kind, String title, String desc) {
    this.id = (id == null) ? "" : id;
    this.kind = (kind == null) ? Kind.SIDE : kind;
    this.title = (title == null) ? "" : title;
    this.desc = (desc == null) ? "" : desc;
  }

  // --- Minimal built-in quest pool (placeholder content; real logic later) ---

  public static QuestDef qHomeWelcome() {
    return new QuestDef(
        "home_welcome",
        Kind.MAIN,
        "Willkommen daheim",
        "Finde die Burg und schau dich um. (Ja, das ist Absicht: wir brauchen erstmal ein Quest-System-Gerüst.)"
    );
  }

  public static QuestDef qGatherLogs() {
    return new QuestDef(
        "gather_logs_10",
        Kind.SIDE,
        "Holz beschaffen",
        "Sammle 10 Holzscheite. (Noch ohne echte Fortschritts-Checks; kommt später.)"
    );
  }

  public static QuestDef qMeetTheMerchant() {
    return new QuestDef(
        "meet_merchant",
        Kind.SIDE,
        "Der Handel ruft",
        "Sprich mit einem Händler. (Noch ohne echte Trigger; kommt später.)"
    );
  }

  /** Resolve a quest by id (minimal registry for Task 11 persistence). */
  public static QuestDef byId(String id) {
    if (id == null) return null;
    return switch (id) {
      case "home_welcome" -> qHomeWelcome();
      case "gather_logs_10" -> qGatherLogs();
      case "meet_merchant" -> qMeetTheMerchant();
      default -> new QuestDef(id, Kind.SIDE, id, "(unbekannte Quest-ID; Save kompatibel gehalten)");
    };
  }
}
