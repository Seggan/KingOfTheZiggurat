package io.github.seggan.kingoftheziggurat.impl;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Ziggurat {

    private static final int MAP_X = 11;
    private static final int MAP_Y = 11;

    private final int[][] map;
    private final Map<Bot, Point> players = new HashMap<>();

    private int round = 0;
    private final int maxRounds;

    Bot currentBot = null;

    public Ziggurat(int[][] map, int maxRounds, Bot... players) {
        this.map = map;
        this.maxRounds = maxRounds;
        for (Bot player : players) {
            player.ziggurat = this;
            this.players.put(player, new Point(
                ThreadLocalRandom.current().nextInt(MAP_X),
                ThreadLocalRandom.current().nextInt(MAP_Y)
            ));
        }
    }

    public void run() {
        while (round < maxRounds) {
            round++;
            for (Bot player : players.keySet()) {
                currentBot = player;
                player.tick();
            }
            tick();
        }
        currentBot = null;
    }

    private void tick() {
        for (Map.Entry<Bot, Point> entry : players.entrySet()) {
            Bot bot = entry.getKey();
            if (!checkOutOfBounds(move(getPosition(bot), bot.direction))) {
                move(entry.getValue(), bot.direction);
            }
            bot.direction = MoveDirection.NONE;
            bot.strength++;
        }
        for (Bot bot : players.keySet()) {
            for (Bot other : players.keySet()) {
                if (bot != other && bot.getPosition().equals(other.getPosition())) {
                    fightBots(bot, other);
                }
            }
        }
        for (Bot bot : players.keySet()) {
            bot.points += bot.getElevation();
        }
    }

    private void fightBots(Bot bot, Bot other) {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        currentBot = bot;
        if (bot.fight(other)) {
            currentBot = other;
            if (other.fight(bot)) {
                int bot1 = r.nextInt(bot.strength / 2, bot.strength);
                int bot2 = r.nextInt(other.strength / 2, other.strength);
                if (bot1 > bot2) {
                    other.strength -= other.strength * 0.2;
                    bot.strength -= bot.strength * 0.1;
                    moveBotDown(other);
                } else {
                    if (bot1 != bot2) {
                        bot.strength -= bot.strength * 0.2;
                        other.strength -= other.strength * 0.1;
                        moveBotDown(bot);
                    }
                }
            } else {
                moveBotDown(other);
            }
        } else {
            moveBotDown(bot);
        }
    }

    private void moveBotDown(Bot bot) {
        if (bot.getElevation() > 1) {
            for (MoveDirection direction : MoveDirection.values()) {
                Point position = move(getPosition(bot), direction);
                if (!checkOutOfBounds(position)) {
                    if (getElevation(position) < bot.getElevation()) {
                        move(players.get(bot), direction);
                        break;
                    }
                }
            }
        }
    }

    public Set<Bot> getPlayers() {
        return new HashSet<>(players.keySet());
    }

    Point getPosition(Bot bot) {
        return new Point(players.get(bot));
    }

    int getElevation(Point position) {
        return map[position.x][position.y];
    }

    int getElevationRelative(Bot bot, MoveDirection direction) {
        Point position = move(getPosition(bot), direction);
        if (checkOutOfBounds(position)) {
            return 1;
        } else {
            return getElevation(position);
        }
    }

    private Point move(Point position, MoveDirection direction) {
        switch (direction) {
            case UP -> position.y--;
            case DOWN -> position.y++;
            case LEFT -> position.x--;
            case RIGHT -> position.x++;
            case UP_LEFT -> {
                position.y--;
                position.x--;
            }
            case UP_RIGHT -> {
                position.y--;
                position.x++;
            }
            case DOWN_LEFT -> {
                position.y++;
                position.x--;
            }
            case DOWN_RIGHT -> {
                position.y++;
                position.x++;
            }
            default -> {
                // do nothing
            }
        }

        return position;
    }

    private boolean checkOutOfBounds(Point position) {
        return position.x < 0 || position.x >= MAP_X || position.y < 0 || position.y >= MAP_Y;
    }
}
