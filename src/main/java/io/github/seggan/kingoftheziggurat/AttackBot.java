package io.github.seggan.kingoftheziggurat;

import io.github.seggan.kingoftheziggurat.impl.Bot;

public class AttackBot extends Bot {
    @Override
    protected boolean fight(Bot opponent) {
        return true;
    }

    @Override
    protected void tick() {
        moveUp();
    }
}
