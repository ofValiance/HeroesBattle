package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.PrintBattleLog;
import com.battle.heroes.army.programs.SimulateBattle;
import java.util.*;

public class SimulateBattleImpl implements SimulateBattle {
    private PrintBattleLog printBattleLog;

    @Override
    public void simulate(Army playerArmy, Army computerArmy) throws InterruptedException {

        Map<Unit, Army> unitToArmy = new HashMap<>();

        for (Unit unit : playerArmy.getUnits()) {
            unitToArmy.put(unit, playerArmy);
        }
        for (Unit unit : computerArmy.getUnits()) {
            unitToArmy.put(unit, computerArmy);
        }

        while (true) {

            List<Unit> aliveUnits = new ArrayList<>();

            for (Unit unit : playerArmy.getUnits()) {
                if (unit.isAlive()) {
                    aliveUnits.add(unit);
                }
            }
            for (Unit unit : computerArmy.getUnits()) {
                if (unit.isAlive()) {
                    aliveUnits.add(unit);
                }
            }

            boolean playerHasAlive = false;
            boolean computerHasAlive = false;

            for (Unit unit : aliveUnits) {
                if (unitToArmy.get(unit) == playerArmy) {
                    playerHasAlive = true;
                } else {
                    computerHasAlive = true;
                }
                if (playerHasAlive && computerHasAlive) {
                    break;
                }
            }

            if (!playerHasAlive || !computerHasAlive) {
                break;
            }

            aliveUnits.sort((u1, u2) -> Integer.compare(u2.getBaseAttack(), u1.getBaseAttack()));

            for (Unit attacker : aliveUnits) {

                if (!attacker.isAlive()) {
                    continue;
                }

                Army attackerArmy = unitToArmy.get(attacker);
                Army enemyArmy = (attackerArmy == playerArmy) ? computerArmy : playerArmy;

                Unit target = attacker.getProgram().attack();
                if (target == null) {
                    continue;
                }

                printBattleLog.printBattleLog(attacker, target);
            }
        }
    }
}