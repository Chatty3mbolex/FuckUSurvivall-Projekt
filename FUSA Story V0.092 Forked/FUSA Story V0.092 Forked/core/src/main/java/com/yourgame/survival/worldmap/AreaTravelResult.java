package com.yourgame.survival.worldmap;

/**
 * Result of a travel attempt on the WorldMap.
 *
 * We use an explicit result object instead of booleans so the caller can
 * decide what to show to the player (toast, sound, UI hint, etc.).
 */
public final class AreaTravelResult {
  public enum Code {
    /** Travel succeeded (either entered known area or discovered a new one). */
    OK,
    /** There is no exit in that direction. */
    NO_EXIT,
    /** A new area could not be placed due to against-checks or missing templates. */
    INVALID_PLACEMENT,
    /** Loader failed to load the template into the runtime world. */
    LOAD_FAILED
  }

  public final Code code;
  public final String templateId;

  public AreaTravelResult(Code code, String templateId) {
    this.code = code;
    this.templateId = (templateId != null) ? templateId : "";
  }

  public static AreaTravelResult ok(String templateId) { return new AreaTravelResult(Code.OK, templateId); }
  public static AreaTravelResult noExit() { return new AreaTravelResult(Code.NO_EXIT, ""); }
  public static AreaTravelResult invalid() { return new AreaTravelResult(Code.INVALID_PLACEMENT, ""); }
  public static AreaTravelResult loadFailed(String templateId) { return new AreaTravelResult(Code.LOAD_FAILED, templateId); }
}
