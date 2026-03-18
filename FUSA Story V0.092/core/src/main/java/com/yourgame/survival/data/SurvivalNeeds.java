package com.yourgame.survival.data;

public final class SurvivalNeeds {
  public float hp = 100f;
  public float hpMax = 100f;

  public float stamina = 100f;
  public float staminaMax = 100f;

  public float mana = 100f;
  public float manaMax = 100f;

  public float hunger = 100f;
  public float hungerMax = 100f;

  public float sleep = 100f;
  public float sleepMax = 100f;

  // Multipliers (1.0 = default). Set by the game based on skills.
  public float hungerDrainMul = 1.0f;
  public float sleepDrainMul = 1.0f;

  public void tick(float dt) {
    // drains per second (minimal tuning)
    hunger -= 0.6f * dt * hungerDrainMul;
    // Sleep drain slowed down by 75% (requested)
    sleep -= (0.35f * 0.25f) * dt * sleepDrainMul;

    // stamina regen
    stamina += 3.0f * dt;
    if (stamina > staminaMax) stamina = staminaMax;

    // mana regen (minimal, placeholder)
    mana += 4.0f * dt;
    if (mana > manaMax) mana = manaMax;

    if (hunger < 0f) hunger = 0f;
    if (sleep < 0f) sleep = 0f;

    // If starving or exhausted: hp drain
    if (hunger <= 0f) hp -= 1.2f * dt;
    if (sleep <= 0f) hp -= 0.8f * dt;

    if (hp < 0f) hp = 0f;
  }

  public void eat(float amount) {
    hunger += amount;
    if (hunger > hungerMax) hunger = hungerMax;
  }

  public void sleepFull() {
    sleep = sleepMax;
    stamina = staminaMax;
    mana = manaMax;
    hp = Math.min(hpMax, hp + 25f);
  }
}
