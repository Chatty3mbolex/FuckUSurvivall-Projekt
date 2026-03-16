package com.yourgame.survival.data;

/** Skill definitions (fixed order) for the in-game Skill-Menü (P). */
public final class SkillDefs {
  private SkillDefs() {}

  public static final String[] ID = {
      // Ressourcen
      "Mining","Woodcutting","Herbalism","Hunting","Fishing","Foraging",
      "Smelting","Tanning","Alchemy","Cooking",

      // Kampf
      "CombatMelee","CombatRanged","WeaponCraft","DualWield","ShieldMastery",
      "ArmorCraft","Dodging","Parrying","Toughness",
      "Stealth","Tracking","Looting",

      // Soziales
      "Trading","Barter","Intimidation","Persuasion","Leadership","Negotiation","Reputation",

      // Person
      "Health","Stamina","Sprinting","HungerControl","SleepControl",
      "CarryCapacity","Climbing","Boating","FlyingEfficiency",
      "ColdResistance","HeatResistance",

      // Andere
      "Building","Crafting","Engineering","ToolDurability",
      "MagicAffinity","SpiritSight","Blessing","Navigation","Scavenging"
  };

  public static final String[] NAME_DE = {
      // Ressourcen
      "Bergbau","Holzfällen","Kräuterkunde","Jagd","Angeln","Sammeln",
      "Schmelzen","Gerben","Alchemie","Kochen",

      // Kampf
      "Nahkampf","Fernkampf","Waffenschmied","Doppelklingen","Schildkampf",
      "Rüstungsschmied","Ausweichen","Parieren","Zähigkeit",
      "Schleichen","Spurenlesen","Plündern",

      // Soziales
      "Handel","Feilschen","Einschüchtern","Überreden","Führung","Verhandeln","Ruf",

      // Person
      "Gesundheit","Ausdauer","Sprinten","Hunger-Kontrolle","Schlaf-Kontrolle",
      "Tragkraft","Klettern","Bootfahren","Flug-Effizienz",
      "Kälteresistenz","Hitzresistenz",

      // Andere
      "Bauen","Handwerk","Ingenieurwesen","Werkzeug-Haltbarkeit",
      "Magie-Affinität","Geistersicht","Segen","Navigation","Plündersinn"
  };

  /** Asset-manifest IDs (docs taxonomy) used to pick suitable icons. */
  public static final String[] ICON_ASSET_ID = {
      "free_cc0_melee_weapons__items_resources_food__pickaxe_1",
      "ravenmoreiconpack022014__items_resources_food__axe",
      "kenney_pirate-kit__items_resources_food__grass-plant",
      "ravenmoreiconpack022014__items_resources_food__bow",
      "kenney_pirate-kit__items_resources_food__boat-row-small",
      "root__items_resources_food__bag_with_flour",
      "kenney_hexagon-kit__items_resources_food__building-smelter",
      "ravenmoreiconpack022014__items_resources_food__armor",
      "ravenmoreiconpack022014__items_resources_food__potiongreen",
      "tiny_swords_free_pack__items_resources_food__meat_resource",

      "ravenmoreiconpack022014__items_resources_food__sword",
      "ravenmoreiconpack022014__items_resources_food__bow",
      "ravenmoreiconpack022014__items_resources_food__hammer",
      "ravenmoreiconpack022014__items_resources_food__dagger",
      "ravenmoreiconpack022014__items_resources_food__shield",
      "ravenmoreiconpack022014__items_resources_food__helmet",
      "kenney_ui-pack-adventure__items_resources_food__minimap_arrow_a",
      "ravenmoreiconpack022014__items_resources_food__shieldsmall",
      "ravenmoreiconpack022014__items_resources_food__heart",
      "craftpix-net-180537-free-swordsman-1-3-level-pixel-top-down-sprite-character__items_resources_food__shadow_single",
      "lpc_base_assets__items_resources_food__eyeball",
      "kenney_pirate-kit__items_resources_food__chest",

      "ravenmoreiconpack022014__items_resources_food__coin",
      "root__items_resources_food__bag_with_flour",
      "kenney_ui-pack-adventure__items_resources_food__minimap_icon_exclamation_red",
      "ravenmoreiconpack022014__items_resources_food__envelope",
      "kenney_pirate-kit__items_resources_food__flag-high",
      "ravenmoreiconpack022014__items_resources_food__scroll",
      "kenney_ui-pack-adventure__items_resources_food__minimap_icon_star_yellow",

      "ravenmoreiconpack022014__items_resources_food__heart",
      "kenney_ui-pack-adventure__items_resources_food__minimap_icon_jewel_yellow",
      "kenney_ui-pack-adventure__items_resources_food__minimap_icon_jewel_yellow",
      "tiny_swords_free_pack__items_resources_food__meat_resource",
      "ravenmoreiconpack022014__items_resources_food__tome",
      "ravenmoreiconpack022014__items_resources_food__backpack",
      "kenney_toon-characters-1__items_resources_food__character_maleadventurer_rope",
      "kenney_pirate-kit__items_resources_food__boat-row-large",
      "kenney_pirate-kit__items_resources_food__flag-pennant",
      "craftpix-net-385863-free-top-down-trees-pixel-art__items_resources_food__snow_tree1",
      "tiny_swords_free_pack__items_resources_food__fire_01",

      "kenney_hexagon-kit__items_resources_food__building-house",
      "ravenmoreiconpack022014__items_resources_food__tools",
      "kenney_hexagon-kit__items_resources_food__building-mill",
      "ravenmoreiconpack022014__items_resources_food__upg_hammer",
      "ravenmoreiconpack022014__items_resources_food__wand",
      "lpc_base_assets__items_resources_food__ghost",
      "kenney_ui-pack-adventure__items_resources_food__minimap_icon_star_white",
      "kenney_ui-pack-adventure__items_resources_food__minimap_compass_toon_n",
      "ravenmoreiconpack022014__items_resources_food__tools",
  };

  /**
   * Icon file paths relative to the libGDX assets/ root.
   * (Derived from assets-manifest.json sourcePath)
   */
  public static final String[] ICON_PATH = {
      "usable assets/free_cc0_melee_weapons/Free CC0 Melee Weapons/sprites/pickaxe 1.png",
      "usable assets/RavenmoreIconPack.02.2014/64/axe.png",
      "usable assets/kenney_pirate-kit/Previews/grass-plant.png",
      "usable assets/RavenmoreIconPack.02.2014/64/bow.png",
      "usable assets/kenney_pirate-kit/Previews/boat-row-small.png",
      "usable assets/bag_with_flour.png",
      "usable assets/kenney_hexagon-kit/Previews/building-smelter.png",
      "usable assets/RavenmoreIconPack.02.2014/64/armor.png",
      "usable assets/RavenmoreIconPack.02.2014/64/potionGreen.png",
      "usable assets/Tiny Swords (Free Pack)/Tiny Swords (Free Pack)/Terrain/Resources/Meat/Meat Resource/Meat Resource.png",

      "usable assets/RavenmoreIconPack.02.2014/64/sword.png",
      "usable assets/RavenmoreIconPack.02.2014/64/bow.png",
      "usable assets/RavenmoreIconPack.02.2014/64/hammer.png",
      "usable assets/RavenmoreIconPack.02.2014/64/dagger.png",
      "usable assets/RavenmoreIconPack.02.2014/64/shield.png",
      "usable assets/RavenmoreIconPack.02.2014/64/helmet.png",
      "usable assets/kenney_ui-pack-adventure/PNG/Double/minimap_arrow_a.png",
      "usable assets/RavenmoreIconPack.02.2014/64/shieldSmall.png",
      "usable assets/RavenmoreIconPack.02.2014/64/heart.png",
      "usable assets/craftpix-net-180537-free-swordsman-1-3-level-pixel-top-down-sprite-character/Tiled_files/Swordsman3/shadow_single.png",
      "usable assets/lpc_base_assets/LPC Base Assets/sprites/monsters/eyeball.png",
      "usable assets/kenney_pirate-kit/Previews/chest.png",

      "usable assets/RavenmoreIconPack.02.2014/64/coin.png",
      "usable assets/bag_with_flour.png",
      "usable assets/kenney_ui-pack-adventure/PNG/Double/minimap_icon_exclamation_red.png",
      "usable assets/RavenmoreIconPack.02.2014/64/envelope.png",
      "usable assets/kenney_pirate-kit/Previews/flag-high.png",
      "usable assets/RavenmoreIconPack.02.2014/64/scroll.png",
      "usable assets/kenney_ui-pack-adventure/PNG/Double/minimap_icon_star_yellow.png",

      "usable assets/RavenmoreIconPack.02.2014/64/heart.png",
      "usable assets/kenney_ui-pack-adventure/PNG/Double/minimap_icon_jewel_yellow.png",
      "usable assets/kenney_ui-pack-adventure/PNG/Double/minimap_icon_jewel_yellow.png",
      "usable assets/Tiny Swords (Free Pack)/Tiny Swords (Free Pack)/Terrain/Resources/Meat/Meat Resource/Meat Resource.png",
      "usable assets/RavenmoreIconPack.02.2014/64/tome.png",
      "usable assets/RavenmoreIconPack.02.2014/64/backpack.png",
      "usable assets/kenney_toon-characters-1/Male adventurer/PNG/Poses/character_maleAdventurer_rope.png",
      "usable assets/kenney_pirate-kit/Previews/boat-row-large.png",
      "usable assets/kenney_pirate-kit/Previews/flag-pennant.png",
      "usable assets/craftpix-net-385863-free-top-down-trees-pixel-art/PNG/Assets_separately/Trees_texture_shadow_dark/Snow_tree1.png",
      "usable assets/Tiny Swords (Free Pack)/Tiny Swords (Free Pack)/Particle FX/Fire_01.png",

      "usable assets/kenney_hexagon-kit/Previews/building-house.png",
      "usable assets/RavenmoreIconPack.02.2014/64/tools.png",
      "usable assets/kenney_hexagon-kit/Previews/building-mill.png",
      "usable assets/RavenmoreIconPack.02.2014/64/upg_hammer.png",
      "usable assets/RavenmoreIconPack.02.2014/64/wand.png",
      "usable assets/lpc_base_assets/LPC Base Assets/sprites/monsters/ghost.png",
      "usable assets/kenney_ui-pack-adventure/PNG/Double/minimap_icon_star_white.png",
      "usable assets/kenney_ui-pack-adventure/PNG/Double/minimap_compass_toon_n.png",
      "usable assets/RavenmoreIconPack.02.2014/64/tools.png",
  };

  public static final int COUNT = ID.length;

  static {
    // Guardrail: keep arrays aligned if future edits add/remove skills.
    if (ICON_PATH.length != COUNT || ICON_ASSET_ID.length != COUNT || NAME_DE.length != COUNT) {
      throw new RuntimeException("SkillDefs arrays are not aligned (ID/NAME_DE/ICON_PATH/ICON_ASSET_ID)");
    }
  }

  public static int indexOf(String id) {
    if (id == null) return -1;
    for (int i=0;i<ID.length;i++) if (id.equals(ID[i])) return i;
    return -1;
  }
}
