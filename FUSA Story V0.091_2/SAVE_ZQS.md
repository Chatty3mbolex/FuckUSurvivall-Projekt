# SAVE_ZQS.md — ZQS Persistenzmodell (PHASE_03)

> Zweck: **exakte Save-/Load-Struktur** für ZQS, sodass Implementierung ohne Nachfragen möglich ist.
> Basis: `MODEL_ZQS.md` + Flow-4 Master Alignment + Workflowplan.
>
> Harte Regeln (Master):
> - `OfferBuffer` ist **nicht persistent**.
> - Nicht angenommene NQ werden verworfen und **nicht** gespeichert.
> - Persistiert werden nur: Knowledge, angenommene Quests (DB/Log), HistoryIndex, Zähler, RNG-State (falls benötigt).
> - Migration: wenn Block fehlt → **saubere Defaults**, keine stillen Datenverluste.

---

## 03.01 — Neuer Save-Block für ZQS

Im Root-Save (Slot JSON) wird ein Block eingeführt:

- Key: `"zqs"`
- Versionierung innerhalb des Blocks:
  - `"zqsVersion": <int>` (startet bei 1)

Beispiel:

```json
{
  "saveVersion": 6,
  "player": { ... },
  "questSys": { ... },
  "questLog": { ... },
  "zqs": {
    "zqsVersion": 1,
    "...": "..."
  }
}
```

---

## 03.02 — Unterblöcke des ZQS-Save

### Pflicht-Unterblöcke

1) `knowledge`
2) `playerQuestDb`
3) `questHistoryIndex`
4) `logbook`
5) `counters`

### Optionale Unterblöcke (nur wenn technisch nötig)

6) `rngState`
7) `blueprintState`
8) `catalogSnapshot`

### Explizite Ausschlüsse

- `offerBuffer` / aktive Angebote am NPC: **nicht persistent**.
- `assignment_na` / N/A-Ausgaben: **nicht persistent**.

---

## 03.03 — Pflichtfelder pro Unterblock (exakt)

### A) `knowledge` (PlayerKnowledgeState)

```json
"knowledge": {
  "known_items": [0, 1, 2],
  "known_regions": ["REGION_HOME"],
  "known_harvestables": ["NODE_TREE"],
  "known_livings": ["animal_deer"],
  "known_pois": ["POI_CHEST_HIDDEN"],
  "known_npcs": ["WANDER_QUEST_GUY"]
}
```

Pflichtregeln:
- Alle Felder müssen existieren (auch wenn leer → `[]`).
- Typen sind stabil: items = int-Liste; alles andere = string-Liste.

Defaults (bei fehlendem Block):
- alle Listen leer.

---

### B) `playerQuestDb` (Persistente Quest Records)

Ziel: vollständige Questdetails persistieren (angenommene Quests).

Struktur:

```json
"playerQuestDb": {
  "records": [
    {
      "quest_id": "NQ...",
      "quest_family": "NQ",
      "quest_type": "sammeln",
      "quest_subtype": "sammeln.item",

      "blueprint_id": "bp_collect_resource_basic",

      "target": {
        "target_type": "item",
        "target_id": "0",
        "target_name": "Wood",
        "target_amount": 10,
        "target_value_copper": 1
      },

      "expected_time_sec": 1032,

      "reward": {
        "reward_total_copper": 12345,
        "reward_text_mode": "currency",
        "reward_currency_copper": 12345,
        "reward_currency_silver": 0,
        "reward_currency_gold": 0,
        "reward_items": []
      },

      "text": {
        "text_profile_id": "tp_nq_default",
        "generated_text_ids": {
          "greeting": ["greeting.main.day.001", "greeting.middle.ruhig.001", "greeting.end.offer.001"],
          "assignment": ["assignment.main.generic.001", "assignment.middle.sammeln.item.001", "assignment.end.generic.001"],
          "reward": ["reward.main.claimable.001"],
          "farewell": ["farewell.end.generic.001"]
        },
        "accepted_text": "<fertiger sichtbarer Text bei Annahme>"
      },

      "source": {
        "source_npc_id": "WANDER_QUEST_GUY",
        "giver_npc_id": "WANDER_QUEST_GUY"
      },

      "status": {
        "final_status": "aktiv"
      },

      "timestamps": {
        "generated_at": 100,
        "offered_at": 120,
        "accepted_at": 140,
        "completed_at": 0,
        "failed_at": 0
      },

      "logbook_entry_nr": 12,

      "locations": {
        "accepted_location": "",
        "completed_location": ""
      }
    }
  ]
}
```

Pflichtregeln:
- `records` existiert immer (auch wenn leer).
- Persistiert werden nur **angenommene** Quests.
- Ein Record enthält **genug**, um UI/Questlog/Rewardtexte ohne DB-Neuberechnung anzuzeigen.

Defaults:
- `records: []`

---

### C) `questHistoryIndex`

```json
"questHistoryIndex": {
  "seen_quest_ids": [],
  "active_quest_ids": [],
  "completed_quest_ids": [],
  "expired_quest_ids": [],
  "declined_quest_ids": []
}
```

Pflichtregeln:
- Alle Listen existieren.
- Nicht angenommene NQ erscheinen hier **nicht**.

Defaults:
- alle leer.

---

### D) `logbook`

Ziel: persistente Logbuchnummern + UI-seitige Sortierung.

```json
"logbook": {
  "next_logbook_entry_nr": 1,
  "entries": [
    {
      "logbook_entry_nr": 12,
      "quest_id": "NQ...",
      "quest_family": "NQ",
      "title": "<Anzeige-Titel>",
      "accepted_text": "<Text bei Annahme>",
      "giver_npc_id": "WANDER_QUEST_GUY",
      "accepted_location": "",
      "accepted_at": 140,
      "final_status": "aktiv",
      "completed_at": 0
    }
  ]
}
```

Pflichtregeln:
- `next_logbook_entry_nr` muss existieren.
- `entries` existiert immer.
- `logbook_entry_nr` ist stabil und wird **nur** bei Accept vergeben.

Defaults:
- `next_logbook_entry_nr = 1`
- `entries = []`

---

### E) `counters`

Ziel: systemische Zähler, die nicht aus anderen Listen rekonstruiert werden sollen.

```json
"counters": {
  "quest_nr_counter": 1
}
```

Pflichtregeln:
- `quest_nr_counter` existiert.

Defaults:
- `quest_nr_counter = 1`

---

### F) Optional: `rngState`

Nur wenn deterministische Generierung/Replay nötig ist.

```json
"rngState": {
  "generator_rng": 0
}
```

Defaults:
- `generator_rng = 0`

---

### G) Optional: `blueprintState`

Nur falls RepeatRules/Eligibility zusätzlichen persistierten Zustand braucht.

```json
"blueprintState": {
  "cooldowns": [
    {"key": "bp_collect_resource_basic", "until_runtime_sec": 0}
  ]
}
```

Defaults:
- `cooldowns: []`

---

### H) Optional: `catalogSnapshot`

Standard: **nicht nötig**, weil Kataloge aus Assets kommen.
Nur falls Runtime-Freigaben dynamisch sind.

```json
"catalogSnapshot": {
  "runtimeCatalogId": "catalog_runtime_v1",
  "hash": "<optional>"
}
```

Defaults:
- Block fehlt → ok.

---

## 03.04 — Migration/Defaults-Regeln

Wenn `zqs` Block fehlt:
- ZQS wird mit Defaults initialisiert:
  - knowledge leer
  - DB leer
  - history leer
  - logbook leer + `next_logbook_entry_nr=1`
  - counters default

Wenn einzelne Unterblöcke fehlen:
- Unterblock mit Defaults erzeugen.

Wenn einzelne Felder fehlen:
- Feld auf Default setzen.

Harte Regel:
- **Keine stillen Datenverluste**: wenn ein Feld unbekannt ist, wird es beim nächsten Save nach Möglichkeit mitgeschrieben.

---

## Kontrolle PHASE_03 (Definition of Done)

PHASE_03 ist fertig, wenn:
- ein Agent daraus Save/Load schreiben kann, ohne nachzufragen,
- und klar ist, welche Teile nicht persistent sein dürfen (OfferBuffer).
