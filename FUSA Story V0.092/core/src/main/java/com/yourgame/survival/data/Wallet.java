package com.yourgame.survival.data;

/** Wallet stores money in COPPER (integer). Silver/Gold are derived by conversion rates. */
public final class Wallet {
  public long copper = 0L;

  public void addCopper(long amount) {
    if (amount <= 0) return;
    copper = safeAdd(copper, amount);
  }

  public boolean spendCopper(long amount) {
    if (amount <= 0) return true;
    if (copper < amount) return false;
    copper -= amount;
    return true;
  }

  private static long safeAdd(long a, long b) {
    long r = a + b;
    if (((a ^ r) & (b ^ r)) < 0) {
      return Long.MAX_VALUE;
    }
    return r;
  }
}
