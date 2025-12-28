package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.Program;
import com.battle.heroes.army.programs.GeneratePreset;
import java.util.*;

public class GeneratePresetImpl implements GeneratePreset {

    private static final int ARMY_ROWS = 21;
    private static final int ARMY_COLUMNS = 3;
    private static final int MAX_COPIES = 11;

    private static class DpState {
        int attack;
        int health;
        int[] counts;

        DpState(int attack, int health, int unitTypes) {
            this.attack = attack;
            this.health = health;
            this.counts = new int[unitTypes];
        }

        DpState copy() {
            DpState copy = new DpState(this.attack, this.health, this.counts.length);
            System.arraycopy(this.counts, 0, copy.counts, 0, this.counts.length);
            return copy;
        }
    }

    @Override
    public Army generate(List<Unit> unitList, int maxPoints) {

        if (unitList == null || unitList.isEmpty() || maxPoints <= 0) {

            return new Army(Collections.emptyList());
        }

        int unitTypes = unitList.size();

        DpState[] dpState = new DpState[maxPoints + 1];
        dpState[0] = new DpState(0, 0, unitTypes);

        for (int type = 0; type < unitTypes; type++) {

            Unit unit = unitList.get(type);
            int cost = unit.getCost();
            int attack = unit.getBaseAttack();
            int health = unit.getHealth();

            if (cost <= 0 || cost > maxPoints) continue;

            int binCopies = Math.min(MAX_COPIES, maxPoints / cost);
            int binPower = 1;

            while (binCopies > 0) {

                binPower = Math.min(binPower, binCopies);

                int binCost = binPower * cost;
                int binAttack = binPower * attack;
                int binHealth = binPower * health;

                for (int w = maxPoints - binCost; w >= 0; w--) {

                    if (dpState[w] != null) {

                        int newCost = w + binCost;
                        int newAttack = dpState[w].attack + binAttack;
                        int newHealth = dpState[w].health + binHealth;

                        if (dpState[newCost] == null || newAttack > dpState[newCost].attack ||
                                (newAttack == dpState[newCost].attack && newHealth > dpState[newCost].health)) {

                            DpState newState = dpState[w].copy();
                            newState.attack = newAttack;
                            newState.health = newHealth;
                            newState.counts[type] += binPower;
                            dpState[newCost] = newState;
                        }
                    }
                }

                binCopies -= binPower;
                binPower *= 2;
            }
        }

        DpState bestDpState = null;
        int bestCost = 0;

        for (int w = 0; w <= maxPoints; w++) {

            if (dpState[w] != null) {

                if (bestDpState == null ||
                        dpState[w].attack > bestDpState.attack ||
                        (dpState[w].attack == bestDpState.attack && dpState[w].health > bestDpState.health)) {

                    bestDpState = dpState[w];
                    bestCost = w;
                }
            }
        }

        List<Unit> armyUnits = createArmyUnits(unitList, bestDpState);

        arrangeUnits(armyUnits);

        Army army = new Army(armyUnits);
        army.setPoints(bestCost);
        return army;
    }

    private List<Unit> createArmyUnits(List<Unit> unitList, DpState dpState) {

        List<Unit> armyUnits = new ArrayList<>();
        Map<String, Integer> nameCounters = new HashMap<>();

        for (int type = 0; type < unitList.size(); type++) {

            Unit typePreset = unitList.get(type);
            int count = dpState.counts[type];

            if (count <= 0) continue;

            String typeName = typePreset.getUnitType();
            int nameCounter = nameCounters.getOrDefault(typeName, 1);

            for (int i = 0; i < count; i++) {

                String newName = typeName + " " + nameCounter;
                nameCounter++;

                Unit newUnit = new Unit(
                        newName,
                        typePreset.getUnitType(),
                        typePreset.getHealth(),
                        typePreset.getBaseAttack(),
                        typePreset.getCost(),
                        typePreset.getAttackType(),
                        new HashMap<>(typePreset.getAttackBonuses()),
                        new HashMap<>(typePreset.getDefenceBonuses()),
                        0, 0
                );

                newUnit.setAlive(true);
                Program program = typePreset.getProgram();
                if (program != null) {
                    newUnit.setProgram(program);
                }

                armyUnits.add(newUnit);
            }

            nameCounters.put(typeName, nameCounter);
        }

        return armyUnits;
    }

    private void arrangeUnits(List<Unit> units) {

        if (units.isEmpty()) return;
        List<int[]> positions = new ArrayList<>();

        for (int x = 0; x < ARMY_COLUMNS; x++) {
            for (int y = 0; y < ARMY_ROWS; y++) {
                positions.add(new int[]{x, y});
            }
        }

        Collections.shuffle(positions);

        for (int i = 0; i < units.size(); i++) {

            int[] pos = positions.get(i);
            Unit unit = units.get(i);
            unit.setxCoordinate(pos[0]);
            unit.setyCoordinate(pos[1]);
        }
    }
}
