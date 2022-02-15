package io.github.seggan.kingoftheziggurat.bots;

import io.github.seggan.kingoftheziggurat.impl.Bot;

public class ScaredyBot extends Bot {

    @Override
    protected boolean fight(Bot opponent) {
        return false;
    }

    @Override
    protected void tick() {
        moveUp();
    }
}
