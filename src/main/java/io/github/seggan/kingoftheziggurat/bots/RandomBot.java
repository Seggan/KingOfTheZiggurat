package io.github.seggan.kingoftheziggurat.bots;

import io.github.seggan.kingoftheziggurat.Bot;
import io.github.seggan.kingoftheziggurat.MoveDirection;

import java.util.concurrent.ThreadLocalRandom;

public class RandomBot extends Bot {
    @Override
    protected boolean fight(Bot opponent) {
        return ThreadLocalRandom.current().nextBoolean();
    }

    @Override
    protected void tick() {
        move(MoveDirection.values()[ThreadLocalRandom.current().nextInt(MoveDirection.values().length)]);
    }
}
