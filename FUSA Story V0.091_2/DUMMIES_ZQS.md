# DUMMIES_ZQS.md

Diese Datei listet **alle** im Code gesetzten Dummy-Platzhalter für ZQS auf.
Format:
- DUMMY SPACE (NNN) – <Name> – <Ort> – <Warum fehlt das noch?>

## Aktive Dummy-Liste

- DUMMY SPACE (001) – Blueprint DB – `core/.../quest/zqs/runtime/ZqsSystem.java` – Blueprint-Auswahl (objective families, weights, repeat rules) ist im Konzept definiert, aber noch nicht als Datenbank/Format im Code umgesetzt.
- DUMMY SPACE (002) – Regelmodell – `core/.../quest/zqs/runtime/ZqsSystem.java` – Formalisierte Conditions (ALL_OF/ANY_OF/IF_THEN...) fehlen noch.
- DUMMY SPACE (003) – PlayerKnowledgeState – `core/.../quest/zqs/runtime/ZqsSystem.java` – Known-State (known_items/regions/harvestables/livings/pois/npcs) ist noch nicht implementiert.
- DUMMY SPACE (004) – HQ-Generierung – `core/.../quest/zqs/runtime/ZqsSystem.java` – HQTrigger/HQ-Regeln sind im Konzept vorbereitet, aber nicht gebaut.
- DUMMY SPACE (005) – QuestHistoryIndex – `core/.../quest/zqs/runtime/ZqsSystem.java` – Persistente History (seen/active/completed/failed/expired/declined) fehlt.
- DUMMY SPACE (006) – deterministische QuestID – `core/.../quest/zqs/runtime/ZqsSystem.java` – QuestID ist aktuell ein Dummy (seed+xor+params). Muss später nach Master-Regel deterministisch aus Kernparametern entstehen.
- DUMMY SPACE (007) – Logbuchnummern – `core/.../quest/zqs/runtime/PersistentQuestRecord.java` – Persistente logbook_entry_nr + Zählerlogik fehlt.
- DUMMY SPACE (008) – Currency Split – `core/.../quest/zqs/runtime/RewardBlock.java` – Aufteilung Kupfer/Silber/Gold + Regeln fehlen (aktuell alles Kupfer).
- DUMMY SPACE (009) – Reward Preview Einbettung – `core/.../quest/zqs/runtime/ZqsTextEngine.java` – Reward-Vorschau im Assignment wird aktuell angehängt; muss später sauber als Snippet-/Platzhalter-Referenz im Assignment-System integriert werden.
- DUMMY SPACE (010) – Reward Format – `core/.../quest/zqs/runtime/ZqsTextEngine.java` – Lokalisierte Darstellung von Rewards (Währung + Items) fehlt.
- DUMMY SPACE (011) – Regionsnamen – `core/.../quest/zqs/runtime/ZqsTextEngine.java` – `{target_region}` ist Dummy "(Region)"; Region-Katalog/Benennung fehlt.
- DUMMY SPACE (012) – NPC-Entity Namen – `core/.../quest/zqs/runtime/ZqsTextEngine.java` – `{target_entity}` Dummy "(Empfänger)"; NPC-Katalog/Benennung fehlt.
- DUMMY SPACE (013) – Escort-Parameter – `core/.../quest/zqs/runtime/ZqsTextEngine.java` – Escort-Felder sind Dummy.
- DUMMY SPACE (014) – ZQS Persistenz – `core/.../systems/WanderQuestGuySystem.java` + `GameScreen.java` – Save/Load des ZQS PlayerQuestDB/OfferBuffer fehlt; derzeit werden nur OfferIds gespeichert, die nach Load nicht zu Text/Details auflösbar sind.
- DUMMY SPACE (015) – Gesprächskontext-Berechnung – `core/.../systems/WanderQuestGuySystem.java` – echte Berechnung von `time_of_day`, `worldstress_zone`, Questcounts etc. aus Spielzustand fehlt (aktuell feste Defaults).
- DUMMY SPACE (016) – weitere Datenbindungen – `core/.../screens/GameScreen.java` – ZQS bindet aktuell nur `DataRegistry + PlayerProgress`; weitere Quellen (POIs/Regions/Runtime-Livings) fehlen.

- DUMMY SPACE (108) – Error Reporting Pipeline – `core/.../quest/zqs/runtime/ZqsDb.java` – DB-Loader wirft derzeit RuntimeException; saubere Protokollierung nach `ERRORS.md` fehlt.
- DUMMY SPACE (109) – DB Hot-Reload/Migration – `core/.../quest/zqs/runtime/ZqsSystem.java` – DB-Versionierung/Reload-Strategie fehlt.

- DUMMY SPACE (017) – no_valid_targets Handling – `core/.../quest/zqs/runtime/ZqsSystem.java` – wenn keine Targets für Blueprint existieren: Blockadegrund setzen + Textpfad `no_offer/blocked` sauber bedienen.
- DUMMY SPACE (018) – weitere TargetKinds – `core/.../quest/zqs/runtime/ZqsSystem.java` – harvestable/living/poi/npc/region Auswahl aus Runtime-Katalog fehlt.
- DUMMY SPACE (019) – weitere Reward-Formeln – `core/.../quest/zqs/runtime/ZqsSystem.java` – craft/find/escort Formeln gemäß `zqs_reward_logik_referenz_v1.md` fehlen.

- DUMMY SPACE (201) – catalogSnapshot Support – `core/.../quest/zqs/save/ZqsSaveBlock.java` – Snapshot-Felder nur nötig, falls RuntimeCatalog später dynamisch wird.
- DUMMY SPACE (202) – catalogSnapshot SaveIO – `core/.../quest/zqs/save/ZqsSaveIO.java` – Schreiben/Lesen des optionalen catalogSnapshot Blocks fehlt.

- DUMMY SPACE (301) – Snippet DB Vollständigkeit – `assets/data/zqs/text_snippets_de_DE_v1.json` – Der Assembler wirft Fehler, wenn z.B. `reward.middle` oder `farewell.middle` für benötigte Filter fehlen (kein Fallback erlaubt). Snippet-DB muss entsprechend vollständig befüllt werden.

- DUMMY SPACE (401) – Reward Items Struktur – `core/.../quest/zqs/runtime/RewardBlock.java` – rewardItems sind noch als String-Liste; muss auf strukturierte Item-Liste umgestellt werden.
- DUMMY SPACE (402) – TargetBlock valueCopper – `core/.../quest/zqs/generator/RewardCalculator.java` – TargetBlock trägt aktuell keinen Wert; Wert wird aus Tag `valueCopper:<n>` gezogen.
- DUMMY SPACE (403) – RewardProfiles Integration – `core/.../quest/zqs/generator/RewardCalculator.java` – reward_text_mode und Distribution (item|currency|mixed) muss aus `reward_profiles_v1.json` kommen.

- DUMMY SPACE (501) – Declined-Semantik – `core/.../quest/zqs/history/QuestHistoryIndex.java` – Klären, ob `declined_quest_ids` nur akzeptierte Quests meint (Master sagt: nicht angenommene Offers nicht persistieren → dürfen nicht in History). Aktuell ist Methode markDeclined entsprechend kommentiert.

- DUMMY SPACE (601) – QuestLog Status-Achse – `core/.../quest/QuestLog.java` – Legacy QuestLog hat nur ACCEPTED/COMPLETED; für ZQS wurden FAILED/EXPIRED ergänzt, aber das Mapping zu `finalStatus` muss später sauber festgezurrt werden (QuestLog bleibt View/Migration, nicht kanonisch).

- DUMMY SPACE (602) – ZQS→QuestLog Status-Mapping – `core/.../data/SaveManager.java` – Mapping finalStatus (aktiv/abgabebereit/erledigt/fehlgeschlagen/abgelaufen) nach QuestLog.Status ist aktuell heuristisch.

- DUMMY SPACE (701) – Runtime Import aus Save – `core/.../quest/zqs/runtime/ZqsRuntime.java` – Import playerQuestDb/logbook/history/knowledge in Runtime-Modelle noch nicht umgesetzt.
- DUMMY SPACE (702) – Accept Persistenz – `core/.../quest/zqs/runtime/ZqsRuntime.java` – acceptOffer schreibt noch nicht in ZqsSaveBlock.playerQuestDb/logbook/questHistoryIndex.
- DUMMY SPACE (703) – Accepted QuestDef View – `core/.../quest/zqs/dock/WanderQuestGuyDock.java` – QuestLog-Eintrag bei Accept wird noch als Platzhalter-QuestDef erzeugt; muss aus Record/Logbook Titel+accepted_text kommen.

## Hinweis (Fallback-Verbot)

In diesem Projekt gilt: **keine Fallback-Systeme** außerhalb der ZQS-Dokumentkonstellation.
Wenn DB-Dateien fehlen oder leer sind, ist das ein **harte** Setup-/Datenfrage und muss als Fehler sichtbar sein (statt still weiterzulaufen).

## Struktur-Dummies (DB/Ordner)

- DUMMY SPACE (101) – ContentCatalogAsset – `core/.../quest/zqs/catalog/README.md` – Asset-Katalog (alles im Spiel definierte) ist als separate Ebene noch nicht modelliert.
- DUMMY SPACE (102) – PlayerKnowledgeState – `core/.../quest/zqs/knowledge/README.md` – Known-State Datenmodell + Persistenz fehlen.
- DUMMY SPACE (103) – BlueprintLoader/Registry – `core/.../quest/zqs/blueprint/README.md` – Loader für `assets/data/zqs/blueprints_v1.json` fehlt.
- DUMMY SPACE (104) – Generatorpipeline – `core/.../quest/zqs/generator/README.md` – vollständige Pipeline (Eligibility/RepeatRules/etc.) fehlt.
- DUMMY SPACE (105) – HistoryIndex – `core/.../quest/zqs/history/README.md` – Datenmodell + Update-Regeln fehlen.
- DUMMY SPACE (106) – Textpool Loader – `core/.../quest/zqs/text/README.md` – JSON-Snippet-Pool lesen + filtern fehlt.
- DUMMY SPACE (107) – ZQS SaveBlock – `core/.../quest/zqs/save/README.md` – SaveManager Integration fehlt.
