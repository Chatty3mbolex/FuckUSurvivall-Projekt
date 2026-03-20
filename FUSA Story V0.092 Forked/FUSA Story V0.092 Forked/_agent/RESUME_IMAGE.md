# RESUME_IMAGE — FUSA GameScreen Decomposition (exact resume)

**Purpose:** This file is the *single prompt payload* to restore the agent to the exact project state/mental model it had right after stopping.

## 0) Hard rules (must be loaded as system constraints)
- Work ONLY here:
  `C:\Users\kuehn\Desktop\Chatty Projekt\FuckUSurvivall Projekt\FUSA Story V0.092 Forked\FUSA Story V0.092 Forked`
- Authority:
  - Plan: `C:\Users\kuehn\Desktop\Chatty Projekt\FuckUSurvivall Projekt\MASTER_PLAN_AGENT_FUSA_GAMESCREEN_DECOMPOSITION.md`
  - Workflow: `C:\Users\kuehn\Desktop\Chatty Projekt\workflow.md`
- Mandatory process:
  - Write memory after EVERY step (what/why/next)
  - No human checkpoint messages except when user explicitly requests (normal mode: checkpoints only into memory/_agent)
  - 6s cooldown rule
  - Scripts must be reviewed before execution
- Forbidden:
  - compile/build/run/debug/commit
  - chase side trails / bug hunts / “spuren”
  - touch multiplayer (`client/network/**`, `GameClientListener.java`)

## 1) Current step + status
- CURRENT_PHASE: PHASE 5
- CURRENT_STEP: **5.8 Final GameScreen cleanup**
- STATUS: **IN PROGRESS**

## 2) What has been completed already (high signal)
### Strategy used so far
- All extractions were implemented first as **INNER CLASSES** of `GameScreen` to avoid widening visibility and