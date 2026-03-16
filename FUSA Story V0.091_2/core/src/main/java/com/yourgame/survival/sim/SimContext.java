package com.yourgame.survival.sim;

import com.yourgame.survival.data.Inventory;
import com.yourgame.survival.data.PlayerProgress;
import com.yourgame.survival.data.PriceBook;
import com.yourgame.survival.data.SurvivalNeeds;
import com.yourgame.survival.data.Wallet;
import com.yourgame.survival.entity.Entities;
import com.yourgame.survival.world.World;

/** Block 8: bundles simulation references for deterministic command application. */
public final class SimContext {
  public final World world;
  public final Entities entities;
  public final Inventory inv;
  public final Wallet wallet;
  public final PlayerProgress progress;
  public final SurvivalNeeds needs;
  public final PriceBook priceBook;

  public SimContext(World world, Entities entities, Inventory inv, Wallet wallet, PlayerProgress progress, SurvivalNeeds needs, PriceBook priceBook) {
    this.world = world;
    this.entities = entities;
    this.inv = inv;
    this.wallet = wallet;
    this.progress = progress;
    this.needs = needs;
    this.priceBook = priceBook;
  }
}
