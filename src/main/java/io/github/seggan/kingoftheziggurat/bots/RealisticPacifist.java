package io.github.seggan.kingoftheziggurat.bots;

import io.github.seggan.kingoftheziggurat.Bot;

import java.util.Random;

import static io.github.seggan.kingoftheziggurat.MoveDirection.*;

public class RealisticPacifist extends Bot {
    Random random = new Random();
    int ticks = 0;

    @Override
    protected boolean fight(Bot opponent) {
        if (random.nextDouble() < 0.2) {
            return true;
        } else {
            return getStrength() > (ticks / 9);
        }
    }

    @Override
    protected void tick() {
        ticks += 1;
        if (getElevation() > 4) {
            if (random.nextDouble() < 0.3) {
                moveUp();
            } else {
                move(NONE);
            }
        } else {
            if (random.nextDouble() > 0.05) {
                moveUp();
            } else {
                if (getPosition().x > 7) {
                    move(WEST);
                } else if (getPosition().y > 7) {
                    move(NORTH);
                } else if (getPosition().x < 4) {
                    move(EAST);
                } else if (getPosition().y < 4) {
                    move(SOUTH);
                } else {
                    switch (random.nextInt(4)) {
                        case 0 -> move(EAST);
                        case 1 -> move(WEST);
                        case 2 -> move(NORTH);
                        case 3 -> move(SOUTH);
                    }
                }
            }
        }
    }
}
