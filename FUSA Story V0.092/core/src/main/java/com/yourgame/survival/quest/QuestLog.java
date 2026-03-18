package com.yourgame.survival.quest;

import com.badlogic.gdx.utils.Array;

/** Minimal quest log (Task 9 placeholder). */
public final class QuestLog {

  public enum Status {
    ACCEPTED,
    COMPLETED,

    // ZQS requires additional terminal states; kept as optional to avoid refactoring legacy logic.
    FAILED,
    EXPIRED
  }

  public static Status fromFinalStatus(String finalStatus) {
    if (finalStatus == null) return Status.ACCEPTED;
    String s = finalStatus.trim().toLowerCase();
    if (s.equals("erledigt") || s.equals("completed")) return Status.COMPLETED;
    if (s.equals("fehlgeschlagen") || s.equals("failed")) return Status.FAILED;
    if (s.equals("abgelaufen") || s.equals("expired")) return Status.EXPIRED;
    return Status.ACCEPTED;
  }

  public static final class Entry {
    public final QuestDef def;
    public Status status;
    public long acceptedAtRuntimeSec;

    // ZQS view-only fields (do NOT become a second canonical storage).
    // Canonical data lives in zqs.playerQuestDb + zqs.logbook.
    public int logbookEntryNr;
    public String finalStatus; // e.g. "aktiv|abgabebereit|erledigt|fehlgeschlagen"

    public Entry(QuestDef def, Status status, long acceptedAtRuntimeSec) {
      this.def = def;
      this.status = status;
      this.acceptedAtRuntimeSec = acceptedAtRuntimeSec;
      this.logbookEntryNr = 0;
      this.finalStatus = "";
    }

    public Entry(QuestDef def, Status status, long acceptedAtRuntimeSec, int logbookEntryNr, String finalStatus) {
      this.def = def;
      this.status = status;
      this.acceptedAtRuntimeSec = acceptedAtRuntimeSec;
      this.logbookEntryNr = logbookEntryNr;
      this.finalStatus = (finalStatus == null) ? "" : finalStatus;
    }
  }

  // stable order for UI
  public final Array<Entry> entries = new Array<>();

  public boolean has(String id) {
    if (id == null || id.isEmpty()) return false;
    for (int i = 0; i < entries.size; i++) {
      Entry e = entries.get(i);
      if (e != null && e.def != null && id.equals(e.def.id)) return true;
    }
    return false;
  }

  /** @return true if it was newly accepted (not already present). */
  public boolean accept(QuestDef def, long runtimeSec) {
    if (def == null || def.id == null || def.id.isEmpty()) return false;
    if (has(def.id)) return false;
    entries.add(new Entry(def, Status.ACCEPTED, runtimeSec));
    return true;
  }

  /** ZQS convenience: attach logbook entry number + finalStatus for UI display only. */
  public boolean acceptZqs(QuestDef def, long runtimeSec, int logbookEntryNr, String finalStatus) {
    if (def == null || def.id == null || def.id.isEmpty()) return false;
    if (has(def.id)) return false;
    entries.add(new Entry(def, Status.ACCEPTED, runtimeSec, logbookEntryNr, finalStatus));
    return true;
  }

  public int size() {
    return entries.size;
  }

  public void clear() {
    entries.clear();
  }

  public Entry find(String questId) {
    if (questId == null || questId.isEmpty()) return null;
    for (int i = 0; i < entries.size; i++) {
      Entry e = entries.get(i);
      if (e != null && e.def != null && questId.equals(e.def.id)) return e;
    }
    return null;
  }

  /** View-only sync from canonical ZQS state (do not create a second persistence). */
  public void updateZqsState(String questId, String finalStatus) {
    Entry e = find(questId);
    if (e == null) return;
    e.finalStatus = (finalStatus == null) ? "" : finalStatus;
    e.status = QuestLog.fromFinalStatus(finalStatus);
  }
}
