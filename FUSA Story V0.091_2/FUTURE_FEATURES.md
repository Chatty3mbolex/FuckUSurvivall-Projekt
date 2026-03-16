# Future Features / Settings (Notes)

- **Gegner‑Balancing (Orks etc.)**
  - Spawnrate/Cap pro Chunk-Radius (früh/mid/late game)
  - Aggro‑Radius/FOV + Reaktionszeiten (Sneak/Run Einfluss)
  - Damage/HP/Armor Scaling nach Tag/Nacht und Biome
  - Loot-/XP‑Tuning (Drop-Raten, Coin-Mengen, Seltenheit)
  - AI-Verhalten: Chase‑Abbruch, Gruppenverhalten, Fluchtchance (Intimidation)
  - Spezialgegner/Varianten (Elite/Range/DoT) + Spawnbedingungen

- **Animal‑Balancing (Deer/Future Tiere)**
  - Populations-Caps pro Biome + Respawn-Timer
  - Verhalten: Flucht-FOV, Panic-Radius, Wander-Geschwindigkeit, Herding
  - Drops (Meat/Hide), Jagd-Skill-Interaktionen
  - Rare Spawns / Biome-exklusive Tiere
  - “Resource nodes” Tiere (z.B. Fischplätze) Regrowth/Respawn

- **Merchant‑Balancing**
  - Händler-Dichte + Spawn-Logik (fixed vs wandering)
  - Offer-Refresh-Logik (Realtime vs Ingame-Tage, Cooldowns)
  - Preis-Skalen: Trading-Skill Einfluss, Markup/Markdown, Biome/Region modifiers
  - Warenpools je Händler-Typ (Basic/Service/Luxury) + Progression Unlocks
  - Anti-Exploit Regeln (Buy/Sell Loops, Price Floors/Caps)

- **World‑Design Changes (Macro)**
  - Biome-Verteilung/Anteile (Kontinente, Inseln, Übergänge)
  - “Points of Interest” (Minen, Ruinen, Camps) Dichte + Mindestabstände
  - Road‑Netz: Klassen (service/main), Dichte, Verbindung zu POIs, Sichtbarkeit
  - Spawn-Safety-Zone um Startpunkt (keine Lava/Wasser/Orks am Spawn)
  - Chunk-Streaming/Preload Settings (Radius, Warmup, Budget)

- **Biome‑Designs (Micro)**
  - Ground/Edge Materialregeln pro Biome (Overrides, EdgeWidth)
  - Shoreline/Bands (Strandbreite, Material-Mix, Flussufer)
  - Biome-spezifische “Decco”-Sets (Steine, Grasbüschel, Schnee-Details)
  - Hazard-Zonen (Kälte/Hitze/DoT) + visuelles Feedback

- **Flora**
  - Spawn-Regeln je Biome/Zone (CORE/EDGE/ROAD/WATERLINE/MASK/CUSTOM)
  - Dichte über Vegetation-Field + Random Jitter (Tree/Bush)
  - Harvest-Balancing: Yield, Respawn, Tool-Gates (Axt/Skill)
  - Kollision/Blocking-Regeln (walkable vs blocked, Placement restrictions)

- **Fauna (über Animals hinaus)**
  - Neue Arten + Biome-Exklusivität
  - Tageszeit-Einflüsse (nachtaktive Tiere, Gefahr nachts)
  - Interaktionen: Predator/Prey, Flucht-/Angriffsverhalten
  - Drops/Crafting-Materialien + Progression

- **Noise / Worldgen Settings**
  - Frequenzen/Octaves pro Field (height/heat/moist/veg/rock/path)
  - Water smoothing + waterDistMaxR
  - EdgeBlend Cap + per-biome edgeWidthTiles
  - Roads: enabled + (zukünftig) Targets/Nodes/A*-Budgets in Config (statt Hardcode)
  - Deco Scatter Dichte + Seed/Variation Controls
