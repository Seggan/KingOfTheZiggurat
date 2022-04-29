package io.github.seggan.kingoftheziggurat.bots;

import io.github.seggan.kingoftheziggurat.Bot;

import java.util.Random;

import static io.github.seggan.kingoftheziggurat.MoveDirection.*;

public class GreedyBot extends io.github.seggan.kingoftheziggurat.Bot {
    private Random random = new Random();
    private int roundcount = 0;
    private int prevheight = 0; //doesn't really matter at first
    private int prevheight2 = -1; //doesn't really matter at first
    private int height = -1; //doesn't really matter at first


    @Override
    protected boolean fight(Bot opponent) {
        if (roundcount < 10) {
            return false;
        }
        if (getElevation() >= 5) {
            if (random.nextDouble() >= 0.7) {
                return true;
            }
            return false;
        } else if (getElevation() == 4) {
            if (random.nextDouble() >= 0.95) {
                return true;
            }
            return false;
        } else {
            return false;
        }
    }


    @Override
    protected void tick() {
        roundcount++;
        if (getElevation() > prevheight) {
            moveUp();
        } else if (getElevation() == prevheight && prevheight == prevheight2) {
            moveUp();
        } else if (getElevation() == prevheight) {
            if (getElevationRelative(NORTH) < getElevation()) {
                move(SOUTH);
            } else {
                move(NORTH);
            }
        } else {
            if (getElevationRelative(NORTH) < getElevation()) {
                if (getElevationRelative(EAST) < getElevation()) {
                    move(SOUTH_WEST);
                } else {
                    move(SOUTH_EAST);
                }

            } else {
                if (getElevationRelative(EAST) < getElevation()) {
                    move(NORTH_WEST);
                } else {
                    move(NORTH_EAST);
                }
            }
        }
        prevheight2 = prevheight;
        prevheight = height;
        height = getElevation();


    }


}