# ZQS Data (DB-ähnliche Dateien)

Dieses Verzeichnis enthält **statische Datenbanken** (DB-ähnliche JSON-Dateien) für das ZQS.

Regeln:
- Diese Dateien sind **keine** Savegames.
- Sie enthalten **Templates/Blueprints/Kataloge/Snippets**, die als Grundlage für die Generierung dienen.
- Persistente Spielerzustände (QuestDB/History/Knowledge) gehören **nicht** hierher, sondern in Save/Slot-Daten.

Reihenfolge (konzepttreu):
1) DB-Struktur (diese Dateien)
2) Befüller (Loader/Registry)
3) Leser (Generator/Textengine)

Dateien in diesem Ordner:
- `catalog_runtime_v1.json` – freigegebene generatorfähige Ziele (Runtime-Katalog)
- `blueprints_v1.json` – Quest-Blueprints (Vorlagen)
- `text_snippets_de_DE_v1.json` – Snippet-Pools (de_DE)
- `reward_profiles_v1.json` – Rewardprofile/Regeln (Parameter, keine Formeln im Text)

