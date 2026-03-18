# quest/zqs/save

ZQS Persistenz (Save/Load Block) – getrennt vom OfferBuffer.

Soll (Flow 4 / Master):
- OfferBuffer ist **nicht persistent**
- PlayerQuestDB ist persistent (angenommene Quests)
- QuestHistoryIndex ist persistent
- logbook counters sind persistent

DUMMY SPACE (107) – SaveBlock JSON Schema + SaveManager Integration fehlt.

