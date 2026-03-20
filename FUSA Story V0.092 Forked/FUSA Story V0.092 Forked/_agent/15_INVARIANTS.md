# 15_INVARIANTS.md

1. Save-Format 100% kompatibel (keine Key-/Reihenfolge-/Serializer-Änderung)
2. Tick-/Update-Reihenfolge unverändert
3. Input-Verhalten logisch gleich
4. WorldMap-Travel-Reihenfolge unverändert
5. UI-State-Mutationen funktionsgleich
6. Renderer rendert, steuert aber nicht
7. Day/Night MUSIC nicht in Renderer
8. Keine Wrapper-/Zwischencontroller-Mimikry
9. Keine Seitensprünge / kein Scope Creep
10. Nur statische Verifikation, kein Compile/Run
