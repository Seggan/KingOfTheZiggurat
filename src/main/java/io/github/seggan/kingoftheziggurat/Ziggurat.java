package io.github.seggan.kingoftheziggurat;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Ziggurat {

    private static final int MAP_X = 11;
    private static final int MAP_Y = 11;

    private final int[][] map;
    private final Map<Bot, Point> players = new HashMap<>();
    private final int maxRounds;
    Bot currentBot = null;
    private int round = 0;

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
            Point p = getPosition(bot);
            if (!checkOutOfBounds(move(getPosition(bot), bot.direction))) {
                move(entry.getValue(), bot.direction);
            }
            if (p.distance(getPosition(bot)) < .2) {
                if (getElevation(p) < 3) {
                    bot.strength += 3;
                } else if (getElevation(p) < 6) {
                    bot.strength += 1;
                } else {
                    bot.strength--;
                }
            }
            bot.strength++;
            bot.direction = MoveDirection.NONE;
        }
        // group bots by position
        HashMap<Point, ArrayList<Bot>> botsByPos = new HashMap<>();
        for (Bot bot : players.keySet()) {
            botsByPos.computeIfAbsent(bot.getPosition(), pos -> new ArrayList<>()).add(bot);
        }
        // fight bots in the same squares
        for (ArrayList<Bot> here : botsByPos.values()) {
            Collections.shuffle(here, ThreadLocalRandom.current());
            for (int i = 1; i < here.size(); i++) {
                Bot aggressor = here.get(i);
                for (int j = 0; j < i; j++) {
                    Bot defender = here.get(j);
                    // is the defender still here, or was it knocked down already?
                    if (defender.getPosition().equals(aggressor.getPosition())) {
                        fightBots(aggressor, defender);
                        // was the aggressor knocked down now?
                        if (aggressor.getPosition().equals(defender.getPosition())) {
                            break;
                        }
                    }
                }
            }
        }
        for (Bot bot : players.keySet()) {
            bot.points += bot.getElevation();
        }
    }

    private void fightBots(Bot bot, Bot other) {
        // ask both bots simultaneously
        currentBot = bot;
        boolean botWants = bot.fight(other);
        currentBot = other;
        boolean otherWants = other.fight(bot);
        if (botWants && otherWants) {
            int bot1;
            int bot2;
            int count = 0;
            do {
                bot1 = nextInt(bot.strength / 2, bot.strength);
                bot2 = nextInt(other.strength / 2, other.strength);
                count++;
                if (count > 10) {
                    return;
                }
            } while (bot1 == bot2);
            if (bot1 > bot2) {
                other.strength -= Math.ceil(other.strength * 0.2);
                bot.strength -= Math.ceil(bot.strength * 0.1);
                moveBotDown(other);
            } else {
                bot.strength -= Math.ceil(bot.strength * 0.2);
                other.strength -= Math.ceil(other.strength * 0.1);
                moveBotDown(bot);
            }
        } else if (botWants) {
            moveBotDown(other);
        } else if (otherWants) {
            moveBotDown(bot);
        }
    }

    private int nextInt(int origin, int bound) {
        if (bound <= origin) {
            return origin;
        } else {
            return ThreadLocalRandom.current().nextInt(origin, bound);
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
        return new LinkedHashSet<>(players.keySet());
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
            case NORTH -> position.y--;
            case SOUTH -> position.y++;
            case EAST -> position.x++;
            case WEST -> position.x--;
            case NORTH_EAST -> {
                position.y--;
                position.x++;
            }
            case NORTH_WEST -> {
                position.y--;
                position.x--;
            }
            case SOUTH_EAST -> {
                position.y++;
                position.x++;
            }
            case SOUTH_WEST -> {
                position.y++;
                position.x--;
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
