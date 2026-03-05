package com.yourgame.survival.data;

public final class PlayerProgress {
  public int level = 1;
  public int xp = 0;
  public int xpToNext = 25;
  public int skillPoints = 0;

  // Skill levels fixed order (see SkillDefs)
  public final int[] skillLv = new int[SkillDefs.COUNT];

  public PlayerProgress() {
    // Test/default: start with level 1 in every skill.
    // (Save/load can overwrite these values.)
    for (int i = 0; i < skillLv.length; i++) skillLv[i] = 1;
  }

  public void addXp(int amount) {
    xp += Math.max(0, amount);
    while (xp >= xpToNext) {
      xp -= xpToNext;
      level++;
      skillPoints += 3;
      xpToNext = (int) Math.floor(25 + (level - 1) * 12 + (level - 1) * (level - 1) * 1.5);
      if (xpToNext < 25) xpToNext = 25;
    }
  }
}
