package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.Edge;
import com.battle.heroes.army.programs.UnitTargetPathFinder;
import com.battle.heroes.army.programs.EdgeDistance;

import java.util.*;

public class UnitTargetPathFinderImpl implements UnitTargetPathFinder {

    private static final int FIELD_WIDTH = 27;
    private static final int FIELD_HEIGHT = 21;

    private static final int[][] DIRECTIONS = {
            {1, 0}, {0, 1}, {-1, 0}, {0, -1},
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
    };

    @Override
    public List<Edge> getTargetPath(Unit attackUnit, Unit targetUnit, List<Unit> existingUnitList) {

        int startX = attackUnit.getxCoordinate();
        int startY = attackUnit.getyCoordinate();
        int targetX = targetUnit.getxCoordinate();
        int targetY = targetUnit.getyCoordinate();

        boolean[][] obstacles = findObstacles(existingUnitList, attackUnit, targetUnit);

        Edge targetLocation = findNearestReachableLocation(startX, startY, targetX, targetY, obstacles);

        if (targetLocation == null) {
            return Collections.emptyList();
        }

        return dijkstra(startX, startY, targetLocation.getX(), targetLocation.getY(), obstacles);
    }

    private boolean[][] findObstacles(List<Unit> units, Unit attackUnit, Unit targetUnit) {

        boolean[][] obstacles = new boolean[FIELD_WIDTH][FIELD_HEIGHT];

        for (Unit unit : units) {

            if (unit.isAlive()) {

                int x = unit.getxCoordinate();
                int y = unit.getyCoordinate();

                if (x >= 0 && x < FIELD_WIDTH && y >= 0 && y < FIELD_HEIGHT) {

                    if (!unit.equals(attackUnit) && !unit.equals(targetUnit)) {

                        obstacles[x][y] = true;
                    }
                }
            }
        }

        return obstacles;
    }

    private Edge findNearestReachableLocation(int startX, int startY, int targetX, int targetY, boolean[][] obstacles) {

        List<Edge> locations = new ArrayList<>();

        for (int[] dir : DIRECTIONS) {

            int nx = targetX + dir[0];
            int ny = targetY + dir[1];

            if (isValid(nx, ny) && !obstacles[nx][ny]) {
                locations.add(new Edge(nx, ny));
            }
        }

        if (locations.isEmpty()) {
            return null;
        }

        Edge nrLocation = null;
        int minDistance = Integer.MAX_VALUE;

        for (Edge location : locations) {

            int dist = Math.max(Math.abs(startX - location.getX()), Math.abs(startY - location.getY()));
            if (dist < minDistance) {
                minDistance = dist;
                nrLocation = location;
            }
        }

        return nrLocation;
    }

    private List<Edge> dijkstra(int startX, int startY, int endX, int endY, boolean[][] obstacles) {

        PriorityQueue<EdgeDistance> q = new PriorityQueue<>(Comparator.comparingInt(EdgeDistance::getDistance));

        int[][] distance = new int[FIELD_WIDTH][FIELD_HEIGHT];
        Edge[][] prevLoc = new Edge[FIELD_WIDTH][FIELD_HEIGHT];

        for (int i = 0; i < FIELD_WIDTH; i++) {

            Arrays.fill(distance[i], Integer.MAX_VALUE);
        }

        distance[startX][startY] = 0;
        q.offer(new EdgeDistance(startX, startY, 0));

        while (!q.isEmpty()) {

            EdgeDistance current = q.poll();
            int x = current.getX();
            int y = current.getY();

            if (x == endX && y == endY) {
                break;
            }

            if (current.getDistance() > distance[x][y]) {
                continue;
            }

            for (int[] dir : DIRECTIONS) {

                int nx = x + dir[0];
                int ny = y + dir[1];

                if (isValid(nx, ny) && !obstacles[nx][ny]) {

                    int newDist = distance[x][y] + 1;

                    if (newDist < distance[nx][ny]) {

                        distance[nx][ny] = newDist;
                        prevLoc[nx][ny] = new Edge(x, y);
                        q.offer(new EdgeDistance(nx, ny, newDist));
                    }
                }
            }
        }

        if (distance[endX][endY] == Integer.MAX_VALUE) {
            return Collections.emptyList();
        }

        List<Edge> path = new ArrayList<>();
        Edge curLoc = new Edge(endX, endY);

        while (true) {

            path.add(curLoc);
            int x = curLoc.getX();
            int y = curLoc.getY();

            if (x == startX && y == startY) {
                break;
            }

            if (prevLoc[x][y] == null) {
                break;
            }
            curLoc = new Edge(prevLoc[x][y].getX(), prevLoc[x][y].getY());
        }

        Collections.reverse(path);
        return path;
    }

    private boolean isValid(int x, int y) {
        return x >= 0 && x < FIELD_WIDTH && y >= 0 && y < FIELD_HEIGHT;
    }
}
