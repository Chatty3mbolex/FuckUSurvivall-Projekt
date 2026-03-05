package com.yourgame.survival.sim;

/** Block 8: ring buffer command queue (no collections). */
public final class CommandQueue {
  private final Command[] buf;
  private int head = 0;
  private int tail = 0;
  private int size = 0;

  public CommandQueue(int capacity) {
    if (capacity <= 0) capacity = 64;
    this.buf = new Command[capacity];
    for (int i = 0; i < capacity; i++) this.buf[i] = new Command();
  }

  public void clear() {
    head = 0;
    tail = 0;
    size = 0;
  }

  public int size() {
    return size;
  }

  public int capacity() {
    return buf.length;
  }

  /** Returns false if full. */
  public boolean push(CommandType type, float f0, float f1, int i0, int i1) {
    if (size >= buf.length) return false;
    buf[tail].set(type, f0, f1, i0, i1);
    tail++;
    if (tail >= buf.length) tail = 0;
    size++;
    return true;
  }

  /** Returns null if empty. */
  public Command pop() {
    if (size <= 0) return null;
    Command c = buf[head];
    head++;
    if (head >= buf.length) head = 0;
    size--;
    return c;
  }
}
