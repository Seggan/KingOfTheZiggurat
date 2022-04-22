package io.github.seggan.kingoftheziggurat.bots;

import io.github.seggan.kingoftheziggurat.Bot;

import static io.github.seggan.kingoftheziggurat.MoveDirection.*;

public class Crab extends Bot {

    @Override
    protected boolean fight(Bot opponent) {
        return true;
    }

    @Override
    protected void tick () {
        if (getElevation() == 6) {
            move(NONE);
        } else {
            moveUp();
        }
    }
}