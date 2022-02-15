package io.github.seggan.kingoftheziggurat.impl;

import java.awt.*;
import java.util.Set;

public abstract class Bot {

    int strength = 0;
    int points = 0;

    boolean fought = false;

    Ziggurat ziggurat;

    MoveDirection direction = MoveDirection.NONE;

    public Bot() {
    }

    public Point getPosition() {
        return ziggurat.getPosition(this);
    }

    public int getElevation() {
        return ziggurat.getElevation(getPosition());
    }

    public int getStrength() {
        if (ziggurat.currentBot == null || ziggurat.currentBot == this) {
            return strength;
        }

        throw new IllegalStateException("You cannot get the strength of any bot except the current bot");
    }

    public Set<Bot> getPlayers() {
        return ziggurat.getPlayers();
    }

    public void move(MoveDirection direction) {
        this.direction = direction;
    }

    public int getElevationRelative(MoveDirection direction) {
        return ziggurat.getElevationRelative(this, direction);
    }

    public int getPoints() {
        return points;
    }

    public void moveUp() {
        for (MoveDirection direction : MoveDirection.values()) {
            if (getElevationRelative(direction) > getElevation()) {
                move(direction);
                return;
            }
        }
    }

    protected abstract boolean fight(Bot opponent);

    protected abstract void tick();
}
