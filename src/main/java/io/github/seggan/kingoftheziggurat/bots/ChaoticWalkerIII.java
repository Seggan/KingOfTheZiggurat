package io.github.seggan.kingoftheziggurat.bots;

import io.github.seggan.kingoftheziggurat.Bot;
import io.github.seggan.kingoftheziggurat.MoveDirection;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static io.github.seggan.kingoftheziggurat.MoveDirection.*;

public class ChaoticWalkerIII extends Bot {
    Random random = new Random();
    int ticks = 0;

    @Override
    protected boolean fight(Bot opponent) {
        if (random.nextDouble() > .9) {
            return ticks > 6;
        } else {
            return true;
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

    @Override
    protected void tick() {
        ticks++;
        if (ticks < 10) {
            move(diagonalMove(whichWayUp()));
        } else if (getElevation() < 6) {
            if (random.nextDouble() > .1 || (getStrength() > 15)) {
                moveUp();
            } else if (random.nextDouble() > .05) {
                move(diagonalMove(whichWayUp()));
            } else if (random.nextDouble() > .8) {
                move(MoveDirection.values()[ThreadLocalRandom.current().nextInt(MoveDirection.values().length)]);
            } else {
                move(MoveDirection.NONE);
            }
        } else if (random.nextDouble() > .3) {
            move(MoveDirection.values()[ThreadLocalRandom.current().nextInt(MoveDirection.values().length)]);
        } else {
            move(MoveDirection.NONE);
        }
    }
}