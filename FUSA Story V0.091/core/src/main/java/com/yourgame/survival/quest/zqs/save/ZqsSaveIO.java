package com.yourgame.survival.quest.zqs.save;

import com.badlogic.gdx.utils.JsonValue;

/**
 * Save/Load for the root JSON block key: "zqs".
 *
 * Must match SAVE_ZQS.md.
 */
public final class ZqsSaveIO {
  private ZqsSaveIO() {}

  /** Appends ONLY the object value for root key "zqs" (no surrounding key). */
  public static void appendZqsObject(StringBuilder sb, ZqsSaveBlock zqs) {
    if (sb == null) return;
    if (zqs == null) zqs = new ZqsSaveBlock();

    sb.append('{');

    sb.append('"').append("zqsVersion").append('"').append(':').append(zqs.zqsVersion).append(',');

    // knowledge
    sb.append('"').append("knowledge").append('"').append(':').append('{');
    appendIntArray(sb, "known_items", zqs.knowledge.knownItems);
    sb.append(',');
    appendStringArray(sb, "known_regions", zqs.knowledge.knownRegions);
    sb.append(',');
    appendStringArray(sb, "known_harvestables", zqs.knowledge.knownHarvestables);
    sb.append(',');
    appendStringArray(sb, "known_livings", zqs.knowledge.knownLivings);
    sb.append(',');
    appendStringArray(sb, "known_pois", zqs.knowledge.knownPois);
    sb.append(',');
    appendStringArray(sb, "known_npcs", zqs.knowledge.knownNpcs);
    sb.append('}').append(',');

    // playerQuestDb
    sb.append('"').append("playerQuestDb").append('"').append(':').append('{');
    sb.append('"').append("records").append('"').append(':').append('[');
    for (int i = 0; i < zqs.playerQuestDb.records.size(); i++) {
      if (i > 0) sb.append(',');
      appendRecord(sb, zqs.playerQuestDb.records.get(i));
    }
    sb.append(']').append('}').append(',');

    // questHistoryIndex
    sb.append('"').append("questHistoryIndex").append('"').append(':').append('{');
    appendStringArray(sb, "seen_quest_ids", zqs.questHistoryIndex.seenQuestIds);
    sb.append(',');
    appendStringArray(sb, "active_quest_ids", zqs.questHistoryIndex.activeQuestIds);
    sb.append(',');
    appendStringArray(sb, "completed_quest_ids", zqs.questHistoryIndex.completedQuestIds);
    sb.append(',');
    appendStringArray(sb, "expired_quest_ids", zqs.questHistoryIndex.expiredQuestIds);
    sb.append(',');
    appendStringArray(sb, "declined_quest_ids", zqs.questHistoryIndex.declinedQuestIds);
    sb.append('}').append(',');

    // logbook
    sb.append('"').append("logbook").append('"').append(':').append('{');
    sb.append('"').append("next_logbook_entry_nr").append('"').append(':').append(zqs.logbook.nextLogbookEntryNr).append(',');
    sb.append('"').append("entries").append('"').append(':').append('[');
    for (int i = 0; i < zqs.logbook.entries.size(); i++) {
      if (i > 0) sb.append(',');
      appendLogbookEntry(sb, zqs.logbook.entries.get(i));
    }
    sb.append(']').append('}').append(',');

    // counters
    sb.append('"').append("counters").append('"').append(':').append('{');
    sb.append('"').append("quest_nr_counter").append('"').append(':').append(zqs.counters.questNrCounter);
    sb.append('}').append(',');

    // storyState
    sb.append('"').append("storyState").append('"').append(':').append('{');
    sb.append('"').append("player_sl_id").append('"').append(':').append(zqs.storyState.playerSlId);
    sb.append('}').append(',');

    // optional rngState
    sb.append('"').append("rngState").append('"').append(':').append('{');
    sb.append('"').append("generator_rng").append('"').append(':').append(zqs.rngState.generatorRng);
    sb.append('}').append(',');

    // optional blueprintState
    sb.append('"').append("blueprintState").append('"').append(':').append('{');
    sb.append('"').append("cooldowns").append('"').append(':').append('[');
    for (int i = 0; i < zqs.blueprintState.cooldowns.size(); i++) {
      if (i > 0) sb.append(',');
      ZqsSaveBlock.Cooldown cd = zqs.blueprintState.cooldowns.get(i);
      if (cd == null) cd = new ZqsSaveBlock.Cooldown();
      sb.append('{');
      sb.append('"').append("key").append('"').append(':').append('"').append(escapeJson(cd.key)).append('"').append(',');
      sb.append('"').append("until_runtime_sec").append('"').append(':').append(cd.untilRuntimeSec);
      sb.append('}');
    }
    sb.append(']').append('}');

    // optional catalogSnapshot
    sb.append(',');
    sb.append('"').append("catalogSnapshot").append('"').append(':').append('{');
    kv(sb, "runtime_catalog_id", zqs.catalogSnapshot.runtimeCatalogId);
    sb.append(',');
    kv(sb, "hash", zqs.catalogSnapshot.hash);
    sb.append('}');

    // nqTimerState
    sb.append(',');
    sb.append('"').append("nqTimerState").append('"').append(':').append('{');
    sb.append('"').append("next_nq_due_runtime_sec").append('"').append(':').append(zqs.nqTimerState.nextNqDueRuntimeSec).append(',');
    sb.append('"').append("last_nq_generated_at_runtime_sec").append('"').append(':').append(zqs.nqTimerState.lastNqGeneratedAtRuntimeSec).append(',');
    sb.append('"').append("last_rolled_refresh_min_sec").append('"').append(':').append(zqs.nqTimerState.lastRolledRefreshMinSec).append(',');
    sb.append('"').append("last_rolled_refresh_max_sec").append('"').append(':').append(zqs.nqTimerState.lastRolledRefreshMaxSec);
    sb.append('}');

    sb.append('}');
  }

  public static void readInto(JsonValue zqsJson, ZqsSaveBlock out) {
    if (out == null) return;
    out.setDefaults();
    if (zqsJson == null) return; // missing block -> defaults

    out.zqsVersion = zqsJson.getInt("zqsVersion", 1);

    // knowledge
    JsonValue k = zqsJson.get("knowledge");
    if (k != null) {
      readIntArray(k.get("known_items"), out.knowledge.knownItems);
      readStringArray(k.get("known_regions"), out.knowledge.knownRegions);
      readStringArray(k.get("known_harvestables"), out.knowledge.knownHarvestables);
      readStringArray(k.get("known_livings"), out.knowledge.knownLivings);
      readStringArray(k.get("known_pois"), out.knowledge.knownPois);
      readStringArray(k.get("known_npcs"), out.knowledge.knownNpcs);
    }

    // playerQuestDb
    JsonValue pq = zqsJson.get("playerQuestDb");
    JsonValue recs = (pq != null) ? pq.get("records") : null;
    if (recs != null) {
      for (JsonValue r = recs.child; r != null; r = r.next) {
        ZqsSaveBlock.PersistentQuestRecordSave pr = new ZqsSaveBlock.PersistentQuestRecordSave();
        pr.questId = r.getString("quest_id", "");
        pr.questFamily = r.getString("quest_family", "NQ");
        pr.questType = r.getString("quest_type", "");
        pr.questSubtype = r.getString("quest_subtype", "");
        pr.blueprintId = r.getString("blueprint_id", "");

        JsonValue tgt = r.get("target");
        if (tgt != null) {
          pr.target.targetType = tgt.getString("target_type", "");
          pr.target.targetId = tgt.getString("target_id", "");
          pr.target.targetName = tgt.getString("target_name", "");
          pr.target.targetAmount = tgt.getInt("target_amount", 0);
          pr.target.targetValueCopper = tgt.getInt("target_value_copper", 0);
        }

        pr.expectedTimeSec = r.getInt("expected_time_sec", 0);

        JsonValue rw = r.get("reward");
        if (rw != null) {
          pr.reward.rewardTotalCopper = rw.getInt("reward_total_copper", 0);
          pr.reward.rewardTextMode = rw.getString("reward_text_mode", "currency");
          pr.reward.rewardCurrencyCopper = rw.getInt("reward_currency_copper", 0);
          pr.reward.rewardCurrencySilver = rw.getInt("reward_currency_silver", 0);
          pr.reward.rewardCurrencyGold = rw.getInt("reward_currency_gold", 0);
          pr.reward.rewardItems.clear();
          JsonValue it = rw.get("reward_items");
          if (it != null) {
            for (JsonValue pair = it.child; pair != null; pair = pair.next) {
              ZqsSaveBlock.RewardItem ri = new ZqsSaveBlock.RewardItem();
              ri.itemId = pair.getInt("itemId", -1);
              ri.amount = pair.getInt("amount", 0);
              pr.reward.rewardItems.add(ri);
            }
          }
        }

        JsonValue tx = r.get("text");
        if (tx != null) {
          pr.text.textProfileId = tx.getString("text_profile_id", "");
          pr.text.acceptedText = tx.getString("accepted_text", "");
          JsonValue gti = tx.get("generated_text_ids");
          pr.text.generatedTextIds.clear();
          if (gti != null) {
            JsonValue gg = gti.get("greeting");
            JsonValue aa = gti.get("assignment");
            JsonValue rr = gti.get("reward");
            JsonValue ff = gti.get("farewell");
            readStringArray(gg, pr.text.generatedTextIds.greeting);
            readStringArray(aa, pr.text.generatedTextIds.assignment);
            readStringArray(rr, pr.text.generatedTextIds.reward);
            readStringArray(ff, pr.text.generatedTextIds.farewell);
          }
        }

        JsonValue src = r.get("source");
        if (src != null) {
          pr.source.sourceNpcId = src.getString("source_npc_id", "");
          pr.source.giverNpcId = src.getString("giver_npc_id", "");
        }

        JsonValue st = r.get("status");
        if (st != null) {
          pr.status.finalStatus = st.getString("final_status", "aktiv");
        }

        JsonValue ts = r.get("timestamps");
        if (ts != null) {
          pr.timestamps.generatedAt = ts.getLong("generated_at", 0L);
          pr.timestamps.offeredAt = ts.getLong("offered_at", 0L);
          pr.timestamps.acceptedAt = ts.getLong("accepted_at", 0L);
          pr.timestamps.completedAt = ts.getLong("completed_at", 0L);
          pr.timestamps.failedAt = ts.getLong("failed_at", 0L);
        }

        pr.logbookEntryNr = r.getInt("logbook_entry_nr", 0);

        JsonValue loc = r.get("locations");
        if (loc != null) {
          pr.locations.acceptedLocation = loc.getString("accepted_location", "");
          pr.locations.completedLocation = loc.getString("completed_location", "");
        }

        out.playerQuestDb.records.add(pr);
      }
    }

    // history
    JsonValue hi = zqsJson.get("questHistoryIndex");
    if (hi != null) {
      readStringArray(hi.get("seen_quest_ids"), out.questHistoryIndex.seenQuestIds);
      readStringArray(hi.get("active_quest_ids"), out.questHistoryIndex.activeQuestIds);
      readStringArray(hi.get("completed_quest_ids"), out.questHistoryIndex.completedQuestIds);
      readStringArray(hi.get("expired_quest_ids"), out.questHistoryIndex.expiredQuestIds);
      readStringArray(hi.get("declined_quest_ids"), out.questHistoryIndex.declinedQuestIds);
    }

    // logbook
    JsonValue lb = zqsJson.get("logbook");
    if (lb != null) {
      out.logbook.nextLogbookEntryNr = lb.getInt("next_logbook_entry_nr", 1);
      out.logbook.entries.clear();
      JsonValue es = lb.get("entries");
      if (es != null) {
        for (JsonValue e = es.child; e != null; e = e.next) {
          ZqsSaveBlock.LogbookEntry le = new ZqsSaveBlock.LogbookEntry();
          le.logbookEntryNr = e.getInt("logbook_entry_nr", 0);
          le.questId = e.getString("quest_id", "");
          le.questFamily = e.getString("quest_family", "NQ");
          le.title = e.getString("title", "");
          le.acceptedText = e.getString("accepted_text", "");
          le.giverNpcId = e.getString("giver_npc_id", "");
          le.acceptedLocation = e.getString("accepted_location", "");
          le.acceptedAt = e.getLong("accepted_at", 0L);
          le.finalStatus = e.getString("final_status", "aktiv");
          le.completedAt = e.getLong("completed_at", 0L);
          out.logbook.entries.add(le);
        }
      }
    }

    // counters
    JsonValue c = zqsJson.get("counters");
    if (c != null) {
      out.counters.questNrCounter = c.getInt("quest_nr_counter", 1);
    }

    // storyState
    JsonValue ss = zqsJson.get("storyState");
    if (ss != null) {
      out.storyState.playerSlId = ss.getInt("player_sl_id", 1);
    }

    // rngState
    JsonValue rs = zqsJson.get("rngState");
    if (rs != null) {
      out.rngState.generatorRng = rs.getLong("generator_rng", 0L);
    }

    // blueprintState
    JsonValue bs = zqsJson.get("blueprintState");
    if (bs != null) {
      out.blueprintState.cooldowns.clear();
      JsonValue cds = bs.get("cooldowns");
      if (cds != null) {
        for (JsonValue cd = cds.child; cd != null; cd = cd.next) {
          ZqsSaveBlock.Cooldown c0 = new ZqsSaveBlock.Cooldown();
          c0.key = cd.getString("key", "");
          c0.untilRuntimeSec = cd.getLong("until_runtime_sec", 0L);
          out.blueprintState.cooldowns.add(c0);
        }
      }
    }

    // optional catalogSnapshot
    JsonValue cs = zqsJson.get("catalogSnapshot");
    if (cs != null) {
      out.catalogSnapshot.runtimeCatalogId = cs.getString("runtime_catalog_id", "catalog_runtime_v1");
      out.catalogSnapshot.hash = cs.getString("hash", "");
    }

    // nqTimerState
    JsonValue nt = zqsJson.get("nqTimerState");
    if (nt != null) {
      out.nqTimerState.nextNqDueRuntimeSec = nt.getLong("next_nq_due_runtime_sec", 0L);
      out.nqTimerState.lastNqGeneratedAtRuntimeSec = nt.getLong("last_nq_generated_at_runtime_sec", 0L);
      out.nqTimerState.lastRolledRefreshMinSec = nt.getInt("last_rolled_refresh_min_sec", 0);
      out.nqTimerState.lastRolledRefreshMaxSec = nt.getInt("last_rolled_refresh_max_sec", 0);
    }
  }

  // ------------------------ write helpers ------------------------

  private static void appendRecord(StringBuilder sb, ZqsSaveBlock.PersistentQuestRecordSave pr) {
    if (pr == null) pr = new ZqsSaveBlock.PersistentQuestRecordSave();
    sb.append('{');
    kv(sb, "quest_id", pr.questId); sb.append(',');
    kv(sb, "quest_family", pr.questFamily); sb.append(',');
    kv(sb, "quest_type", pr.questType); sb.append(',');
    kv(sb, "quest_subtype", pr.questSubtype); sb.append(',');
    kv(sb, "blueprint_id", pr.blueprintId); sb.append(',');

    sb.append('"').append("target").append('"').append(':').append('{');
    kv(sb, "target_type", pr.target.targetType); sb.append(',');
    kv(sb, "target_id", pr.target.targetId); sb.append(',');
    kv(sb, "target_name", pr.target.targetName); sb.append(',');
    sb.append('"').append("target_amount").append('"').append(':').append(pr.target.targetAmount).append(',');
    sb.append('"').append("target_value_copper").append('"').append(':').append(pr.target.targetValueCopper);
    sb.append('}').append(',');

    sb.append('"').append("expected_time_sec").append('"').append(':').append(pr.expectedTimeSec).append(',');

    sb.append('"').append("reward").append('"').append(':').append('{');
    sb.append('"').append("reward_total_copper").append('"').append(':').append(pr.reward.rewardTotalCopper).append(',');
    kv(sb, "reward_text_mode", pr.reward.rewardTextMode); sb.append(',');
    sb.append('"').append("reward_currency_copper").append('"').append(':').append(pr.reward.rewardCurrencyCopper).append(',');
    sb.append('"').append("reward_currency_silver").append('"').append(':').append(pr.reward.rewardCurrencySilver).append(',');
    sb.append('"').append("reward_currency_gold").append('"').append(':').append(pr.reward.rewardCurrencyGold).append(',');
    sb.append('"').append("reward_items").append('"').append(':').append('[');
    for (int i = 0; i < pr.reward.rewardItems.size(); i++) {
      if (i > 0) sb.append(',');
      ZqsSaveBlock.RewardItem ri = pr.reward.rewardItems.get(i);
      if (ri == null) ri = new ZqsSaveBlock.RewardItem();
      sb.append('{');
      sb.append('"').append("itemId").append('"').append(':').append(ri.itemId).append(',');
      sb.append('"').append("amount").append('"').append(':').append(ri.amount);
      sb.append('}');
    }
    sb.append(']');
    sb.append('}').append(',');

    sb.append('"').append("text").append('"').append(':').append('{');
    kv(sb, "text_profile_id", pr.text.textProfileId); sb.append(',');
    sb.append('"').append("generated_text_ids").append('"').append(':').append('{');
    appendStringArray(sb, "greeting", pr.text.generatedTextIds.greeting); sb.append(',');
    appendStringArray(sb, "assignment", pr.text.generatedTextIds.assignment); sb.append(',');
    appendStringArray(sb, "reward", pr.text.generatedTextIds.reward); sb.append(',');
    appendStringArray(sb, "farewell", pr.text.generatedTextIds.farewell);
    sb.append('}').append(',');
    kv(sb, "accepted_text", pr.text.acceptedText);
    sb.append('}').append(',');

    sb.append('"').append("source").append('"').append(':').append('{');
    kv(sb, "source_npc_id", pr.source.sourceNpcId); sb.append(',');
    kv(sb, "giver_npc_id", pr.source.giverNpcId);
    sb.append('}').append(',');

    sb.append('"').append("status").append('"').append(':').append('{');
    kv(sb, "final_status", pr.status.finalStatus);
    sb.append('}').append(',');

    sb.append('"').append("timestamps").append('"').append(':').append('{');
    sb.append('"').append("generated_at").append('"').append(':').append(pr.timestamps.generatedAt).append(',');
    sb.append('"').append("offered_at").append('"').append(':').append(pr.timestamps.offeredAt).append(',');
    sb.append('"').append("accepted_at").append('"').append(':').append(pr.timestamps.acceptedAt).append(',');
    sb.append('"').append("completed_at").append('"').append(':').append(pr.timestamps.completedAt).append(',');
    sb.append('"').append("failed_at").append('"').append(':').append(pr.timestamps.failedAt);
    sb.append('}').append(',');

    sb.append('"').append("logbook_entry_nr").append('"').append(':').append(pr.logbookEntryNr).append(',');

    sb.append('"').append("locations").append('"').append(':').append('{');
    kv(sb, "accepted_location", pr.locations.acceptedLocation); sb.append(',');
    kv(sb, "completed_location", pr.locations.completedLocation);
    sb.append('}');

    sb.append('}');
  }

  private static void appendLogbookEntry(StringBuilder sb, ZqsSaveBlock.LogbookEntry e) {
    if (e == null) e = new ZqsSaveBlock.LogbookEntry();
    sb.append('{');
    sb.append('"').append("logbook_entry_nr").append('"').append(':').append(e.logbookEntryNr).append(',');
    kv(sb, "quest_id", e.questId); sb.append(',');
    kv(sb, "quest_family", e.questFamily); sb.append(',');
    kv(sb, "title", e.title); sb.append(',');
    kv(sb, "accepted_text", e.acceptedText); sb.append(',');
    kv(sb, "giver_npc_id", e.giverNpcId); sb.append(',');
    kv(sb, "accepted_location", e.acceptedLocation); sb.append(',');
    sb.append('"').append("accepted_at").append('"').append(':').append(e.acceptedAt).append(',');
    kv(sb, "final_status", e.finalStatus); sb.append(',');
    sb.append('"').append("completed_at").append('"').append(':').append(e.completedAt);
    sb.append('}');
  }

  private static void appendIntArray(StringBuilder sb, String key, java.util.ArrayList<Integer> values) {
    sb.append('"').append(key).append('"').append(':').append('[');
    if (values != null) {
      for (int i = 0; i < values.size(); i++) {
        if (i > 0) sb.append(',');
        sb.append(values.get(i));
      }
    }
    sb.append(']');
  }

  private static void appendStringArray(StringBuilder sb, String key, java.util.ArrayList<String> values) {
    sb.append('"').append(key).append('"').append(':').append('[');
    if (values != null) {
      for (int i = 0; i < values.size(); i++) {
        if (i > 0) sb.append(',');
        sb.append('"').append(escapeJson(values.get(i))).append('"');
      }
    }
    sb.append(']');
  }

  private static void kv(StringBuilder sb, String key, String value) {
    sb.append('"').append(key).append('"').append(':').append('"').append(escapeJson(value)).append('"');
  }

  private static String escapeJson(String s) {
    if (s == null) return "";
    String out = s;
    out = out.replace("\\", "\\\\");
    out = out.replace("\"", "\\\"");
    out = out.replace("\r", " ");
    out = out.replace("\n", " ");
    return out;
  }

  // ------------------------ read helpers ------------------------

  private static void readIntArray(JsonValue arr, java.util.ArrayList<Integer> out) {
    out.clear();
    if (arr == null) return;
    for (JsonValue v = arr.child; v != null; v = v.next) {
      try { out.add(v.asInt()); } catch (Throwable ignored) {}
    }
  }

  private static void readStringArray(JsonValue arr, java.util.ArrayList<String> out) {
    out.clear();
    if (arr == null) return;
    for (JsonValue v = arr.child; v != null; v = v.next) {
      try { out.add(v.asString()); } catch (Throwable ignored) {}
    }
  }
}
