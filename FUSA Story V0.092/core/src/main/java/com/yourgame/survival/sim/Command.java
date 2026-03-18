package com.yourgame.survival.sim;

/** Block 8: GC-light command payload (primitive fields only). */
public final class Command {
  public CommandType type;
  public float f0;
  public float f1;
  public int i0;
  public int i1;

  public Command() {
    this.type = CommandType.MOVE;
  }

  public void set(CommandType type, float f0, float f1, int i0, int i1) {
    this.type = type;
    this.f0 = f0;
    this.f1 = f1;
    this.i0 = i0;
    this.i1 = i1;
  }
}
