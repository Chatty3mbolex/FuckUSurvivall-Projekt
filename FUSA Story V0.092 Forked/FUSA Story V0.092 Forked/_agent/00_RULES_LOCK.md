# 00_RULES_LOCK.md

## Project root (ONLY place to operate)
`C:\Users\kuehn\Desktop\Chatty Projekt\FuckUSurvivall Projekt\FUSA Story V0.092 Forked\FUSA Story V0.092 Forked`

## Authority
- Plan: `MASTER_PLAN_AGENT_FUSA_GAMESCREEN_DECOMPOSITION.md`
- Workflow: `C:\Users\kuehn\Desktop\Chatty Projekt\workflow.md`

## Hard constraints (NON-NEGOTIABLE)
- OFFLINE ONLY
- NO COMPILE / NO BUILD / NO RUN / NO DEBUG
- NO COMMIT / NO GIT
- NO WEB / NO external APIs
- DO NOT touch multiplayer: `client/network/**` + `GameClientListener.java`
- No human checkpoint messages; checkpoints go to `_agent/*` logs.

## Timer rule
- Minimum 6s cooldown between reasoning/major blocks; keep operations deterministic.

## Execution order (must follow)
Phase 5.2 -> 5.3 -> 5.4 -> 5.5 -> 5.6 -> 5.7 -> 5.8 -> 6.1 -> 6.2 -> 6.3 -> 7.1 -> 7.2
