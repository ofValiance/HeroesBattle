package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.SuitableForAttackUnitsFinder;
import java.util.*;


public class SuitableForAttackUnitsFinderImpl implements SuitableForAttackUnitsFinder {

    private static final int ARMY_ROWS = 21;
    private static final int ARMY_COLUMNS = 3;

    @Override
    public List<Unit> getSuitableUnits(List<List<Unit>> unitsByRow, boolean isLeftArmyTarget) {

        List<Unit> suitableUnits = new ArrayList<>();

        for (int col = 0; col < ARMY_COLUMNS; col++) {

            List<Unit> columnUnits = unitsByRow.get(col);
            if (columnUnits == null) {
                continue;
            }

            for (int row = 0; row < ARMY_ROWS; row++) {

                if (row >= columnUnits.size()) {
                    break;
                }

                Unit currentUnit = columnUnits.get(row);
                if (currentUnit == null || !currentUnit.isAlive()) {
                    continue;
                }

                if (isLeftArmyTarget) {

                    if (col == 0) {

                        suitableUnits.add(currentUnit);

                    } else {

                        List<Unit> prevCol = unitsByRow.get(col - 1);
                        Unit unitBehind = null;

                        if (prevCol != null && row < prevCol.size()) {
                            unitBehind = prevCol.get(row);
                        }

                        if (unitBehind == null || !unitBehind.isAlive()) {
                            suitableUnits.add(currentUnit);
                        }
                    }
                } else {

                    if (col == 2) {

                        suitableUnits.add(currentUnit);

                    } else {

                        List<Unit> nextColumn = unitsByRow.get(col + 1);
                        Unit unitBehind = null;

                        if (nextColumn != null && row < nextColumn.size()) {
                            unitBehind = nextColumn.get(row);
                        }

                        if (unitBehind == null || !unitBehind.isAlive()) {
                            suitableUnits.add(currentUnit);
                        }
                    }
                }
            }
        }

        return suitableUnits;
    }
}
