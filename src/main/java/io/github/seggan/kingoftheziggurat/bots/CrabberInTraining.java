package io.github.seggan.kingoftheziggurat.bots;

import io.github.seggan.kingoftheziggurat.Bot;
import io.github.seggan.kingoftheziggurat.MoveDirection;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static io.github.seggan.kingoftheziggurat.MoveDirection.*;

public class CrabberInTraining extends Bot {
    Random random = new Random();
    int ticks = 0;

    private static Point relativePositionFrom(MoveDirection direction, Point from) {
        int x = from.x;
        int y = from.y;
        return switch (direction) {
            case NORTH -> new Point(x, y - 1);
            case SOUTH -> new Point(x, y + 1);
            case EAST -> new Point(x + 1, y);
            case WEST -> new Point(x - 1, y);
            case NORTH_EAST -> new Point(x + 1, y - 1);
            case NORTH_WEST -> new Point(x - 1, y - 1);
            case SOUTH_EAST -> new Point(x + 1, y + 1);
            case SOUTH_WEST -> new Point(x - 1, y + 1);
            default -> from;
        };
    }

    private static int elevationAt(Point point) {
        int fromCorner = Math.min(point.x, point.y) + 1;
        return Math.min(fromCorner, 12 - fromCorner);
    }

    private static boolean withinReachOf(Point a, Point b) {
        return Math.abs(a.x - b.x) <= 1 && Math.abs(a.y - b.y) <= 1;
    }

    private static Point defaultUpFrom(Point from) {
        for (MoveDirection direction : MoveDirection.values()) {
            if (elevationAt(relativePositionFrom(direction, from)) > elevationAt(from)) {
                return relativePositionFrom(direction, from);
            }
        }
        return from;
    }

    protected boolean fight(Bot opponent) {
        return getElevation() > 4 || getStrength() > 18;
    }

    private MoveDirection diagonalMove(MoveDirection m) {
        if (m == NORTH) {
            if (random.nextBoolean()) {
                return NORTH_EAST;
            }
            return NORTH_WEST;
        } else if (m == SOUTH) {
            if (random.nextBoolean()) {
                return SOUTH_EAST;
            }
            return SOUTH_WEST;
        } else if (m == EAST) {
            if (random.nextBoolean()) {
                return NORTH_EAST;
            }
            return SOUTH_EAST;
        } else {
            if (random.nextBoolean()) {
                return NORTH_WEST;
            }
            return SOUTH_WEST;
        }
    }

    private MoveDirection whichWayUp() {
        int relEvUp = getElevationRelative(NORTH) - getElevation();
        int relEvDown = getElevationRelative(SOUTH) - getElevation();
        int relEvLeft = getElevationRelative(WEST) - getElevation();
        if (relEvUp > 0) {
            return NORTH;
        } else if (relEvDown > 0) {
            return SOUTH;
        } else if (relEvLeft > 0) {
            return WEST;
        } else {
            return EAST;
        }
    }

    @Override
    protected void tick() {
        if (ticks < 10 || (50 < ticks && ticks < 60) || (150 < ticks && ticks < 160) || (300 < ticks && ticks < 310) || (800 < ticks && ticks < 810)) {
            move(diagonalMove(whichWayUp()));
        }
        if (getElevation() == 6) {
            if (random.nextDouble() < .9) {
                move(NONE);
            }
            {
                move(MoveDirection.values()[ThreadLocalRandom.current().nextInt(MoveDirection.values().length)]);
            }
            return;
        }
        Map<MoveDirection, Double> weights = new HashMap<>();

        Map<Point, Integer> botPositionCounts = new HashMap<>();

        for (Bot bot : getPlayers()) {
            Point position = bot.getPosition();
            botPositionCounts.put(position, botPositionCounts.getOrDefault(position, 0) + 1);
        }


        for (MoveDirection direction : MoveDirection.values()) {

            double weight = (getElevationRelative(direction) - 2 * getElevation()) * 200;
            Point candidate = relativePositionFrom(direction, getPosition());

            for (Map.Entry<Point, Integer> entry : botPositionCounts.entrySet()) {

                Point position = entry.getKey();
                int count = entry.getValue();

                if (defaultUpFrom(position).equals(candidate)) {
                    weight -= 3 * count;
                }

                if (elevationAt(candidate) > elevationAt(position) && withinReachOf(candidate, position)) {
                    weight -= count;
                }
                weights.put(direction, weight);

            }

            MoveDirection chosen = weights.entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey();

            move(chosen);

        }
    }
}