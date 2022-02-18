package io.github.seggan.kingoftheziggurat;

import com.google.gson.JsonObject;

import java.awt.*;
import java.util.Set;

public abstract class Bot {

    int strength = 0;
    int points = 0;

    Ziggurat ziggurat;

    MoveDirection direction = MoveDirection.NONE;

    public Bot() {
    }

    public final Point getPosition() {
        return ziggurat.getPosition(this);
    }

    public final int getElevation() {
        return ziggurat.getElevation(getPosition());
    }

    public final int getStrength() {
        if (ziggurat.currentBot == null || ziggurat.currentBot == this) {
            return strength;
        }

        throw new IllegalStateException("You cannot get the strength of any bot except the current bot");
    }

    public final Set<Bot> getPlayers() {
        return ziggurat.getPlayers();
    }

    public final void move(MoveDirection direction) {
        this.direction = direction;
    }

    public final int getElevationRelative(MoveDirection direction) {
        return ziggurat.getElevationRelative(this, direction);
    }

    public final int getPoints() {
        return points;
    }

    public final void moveUp() {
        for (MoveDirection direction : MoveDirection.values()) {
            if (getElevationRelative(direction) > getElevation()) {
                move(direction);
                return;
            }
        }
    }

    public final JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("elevation", getElevation());
        json.addProperty("x", getPosition().x);
        json.addProperty("y", getPosition().y);
        json.addProperty("name", getClass().getSimpleName());
        return json;
    }

    protected abstract boolean fight(Bot opponent);

    protected abstract void tick();
}
