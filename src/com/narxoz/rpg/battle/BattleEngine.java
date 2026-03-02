package com.narxoz.rpg.battle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class BattleEngine {
    private static BattleEngine instance;
    private Random random = new Random(1L);

    private BattleEngine() {
    }

    public static BattleEngine getInstance() {
        if (instance == null) {
            instance = new BattleEngine();
        }
        return instance;
    }

    public BattleEngine setRandomSeed(long seed) {
        this.random = new Random(seed);
        return this;
    }

    public void reset() {
        this.random=new Random(1L);
    }

    public EncounterResult runEncounter(List<Combatant> teamA, List<Combatant> teamB) {
        if (teamA==null || teamB==null) {
            throw new IllegalArgumentException("Teams must not be null");
        }
        if (teamA.isEmpty() || teamB.isEmpty()) {
            throw new IllegalArgumentException("Teams must not be empty");
        }

        List<Combatant> a=new ArrayList<>(teamA);
        List<Combatant> b=new ArrayList<>(teamB);

        a.removeIf(c -> c==null || !c.isAlive());
        b.removeIf(c -> c==null || !c.isAlive());

        EncounterResult result=new EncounterResult();

        if (a.isEmpty() || b.isEmpty()) {
            result.setRounds(0);
            result.setWinner(a.isEmpty() ? "Team B" : "Team A");
            result.addLog("Encounter ended immediately: one team had no living combatants.");
            return result;
        }

        int rounds=0;
        result.addLog("Battle started!");
        result.addLog("Team A: " + names(a));
        result.addLog("Team B: " + names(b));

        while (!a.isEmpty() && !b.isEmpty()) {
            rounds++;
            result.addLog("");
            result.addLog("--- Round " + rounds + " ---");

            attackPhase("A", a, "B", b, result);
            if (b.isEmpty()) break;

            attackPhase("B", b, "A", a, result);
        }

        result.setRounds(rounds);
        result.setWinner(a.isEmpty() ? "Team B" : "Team A");
        result.addLog("");
        result.addLog("Battle ended. Winner: " + result.getWinner());
        return result;
    }

    private void attackPhase(
            String attackerTeamName,
            List<Combatant> attackers,
            String defenderTeamName,
            List<Combatant> defenders,
            EncounterResult result
    ) {
        for (Combatant attacker : new ArrayList<>(attackers)) {
            if (defenders.isEmpty()) return;
            if (attacker==null || !attacker.isAlive()) continue;

            Combatant target=defenders.get(0);

            int damage = attacker.getAttackPower();

            target.takeDamage(damage);
            result.addLog("Team " + attackerTeamName + ": " + attacker.getName()
                    + " hits " + target.getName() + " for " + damage);

            defenders.removeIf(c -> c==null || !c.isAlive());

            if (defenders.isEmpty()) {
                result.addLog("Team " +defenderTeamName+ " has no living combatants left.");
                return;
            }
        }
    }

    private String names(List<Combatant> team) {
        StringBuilder sb=new StringBuilder();
        for (int i=0; i < team.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(team.get(i).getName());
        }
        return sb.toString();
    }
}
