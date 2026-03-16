# PLAN — Modul 02: `quest/zqs/knowledge/*` (PlayerKnowledgeState)

> Ziel: Dummy-Replacement für Struktur-Dummy **(102)** + harte Known-State Regeln aus Master.
>
> Autorität:
> - `flow 4/zqs_master_alignment_v_1_1.md` (Block 2)
> - `MODEL_ZQS.md` (02.02)
> - Persistenz: `SAVE_ZQS.md` (knowledge)

> Harte Regeln:
> - Known-State ist **persistenter Save-Block** und wird **nie** rekonstruiert.
> - Generator darf Ziele mit `knownRequired=true` nur wählen, wenn Known-State das Ziel enthält.

---

## 1) Dateien/Packages (neu)

Pfad (neu):
- `core/src/main/java/com/yourgame/survival/quest/zqs/knowledge/`

Neue Klassen:
1) `PlayerKnowledgeState.java` (Runtime View / Wrapper)
2) `KnowledgeQuery.java` (Helper)

> Persistenz bleibt weiterhin in `ZqsSaveBlock.Knowledge`.

---

## 2) Copy/Paste Code

### 2.1 `PlayerKnowledgeState.java`

```java
package com.yourgame.survival.quest.zqs.knowledge;

import com.yourgame.survival.quest.zqs.catalog.CatalogKind;
import com.yourgame.survival.quest.zqs.save.ZqsSaveBlock;

import java.util.HashSet;

/**
 * Runtime wrapper over persisted save knowledge.
 * Canonical storage remains in ZqsSaveBlock.knowledge.
 */
public final class PlayerKnowledgeState {

  private final HashSet<Integer> knownItems = new HashSet<>();
  private final HashSet<String> knownRegions = new HashSet<>();
  private final HashSet<String> knownHarvestables = new HashSet<>();
  private final HashSet<String> knownLivings = new HashSet<>();
  private final HashSet<String> knownPois = new HashSet<>();
  private final HashSet<String> knownNpcs = new HashSet<>();

  public void clear() {
    knownItems.clear();
    knownRegions.clear();
    knownHarvestables.clear();
    knownLivings.clear();
    knownPois.clear();
    knownNpcs.clear();
  }

  public void importFromSave(ZqsSaveBlock save) {
    clear();
    if (save == null || save.knowledge == null) return;

    if (save.knowledge.knownItems != null) knownItems.addAll(save.knowledge.knownItems);
    if (save.knowledge.knownRegions != null) knownRegions.addAll(save.knowledge.knownRegions);
    if (save.knowledge.knownHarvestables != null) knownHarvestables.addAll(save.knowledge.knownHarvestables);
    if (save.knowledge.knownLivings != null) knownLivings.addAll(save.knowledge.knownLivings);
    if (save.knowledge.knownPois != null) knownPois.addAll(save.knowledge.knownPois);
    if (save.knowledge.knownNpcs != null) knownNpcs.addAll(save.knowledge.knownNpcs);
  }

  /**
   * Key-based check used by generator after it selected a CatalogEntry.
   * For non-items, key is string id.
   */
  public boolean isKnown(CatalogKind kind, String key) {
    if (kind == null) return false;
    if (key == null || key.isEmpty()) return false;

    return switch (kind) {
      case ITEM, RESOURCE -> {
        try { yield knownItems.contains(Integer.parseInt(key)); }
        catch (Throwable t) { yield false; }
      }
      case REGION -> knownRegions.contains(key);
      case HARVESTABLE -> knownHarvestables.contains(key);
      case LIVING -> knownLivings.contains(key);
      case POI -> knownPois.contains(key);
      case NPC -> knownNpcs.contains(key);
    };
  }
}
```

### 2.2 `KnowledgeQuery.java`

```java
package com.yourgame.survival.quest.zqs.knowledge;

import com.yourgame.survival.quest.zqs.catalog.CatalogEntryRt;

public final class KnowledgeQuery {
  private KnowledgeQuery() {}

  public static boolean passesKnownGate(PlayerKnowledgeState k, CatalogEntryRt e) {
    if (e == null) return false;
    if (!e.knownRequired) return true;
    if (k == null) return false;
    return k.isKnown(e.kind, e.key());
  }
}
```

---

## 3) Einbau-Anweisungen (später)

- In `ZqsRuntime.bind(...)` (DUMMY 701) wird zusätzlich `PlayerKnowledgeState.importFromSave(save)` gemacht.
- Generatorpipeline (Modul 04) nutzt `KnowledgeQuery.passesKnownGate(...)` in Filterkette Schritt 4.

---

## 4) Dummy-Abdeckung

- (102) PlayerKnowledgeState: vollständig.
- Verhindert Fallback: KnownRequired ohne Known -> Ziel wird **verworfen** (nicht „trotzdem“).

