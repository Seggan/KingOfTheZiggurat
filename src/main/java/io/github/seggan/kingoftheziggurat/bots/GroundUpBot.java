package io.github.seggan.kingoftheziggurat.bots;

import io.github.seggan.kingoftheziggurat.Bot;
import io.github.seggan.kingoftheziggurat.MoveDirection;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static io.github.seggan.kingoftheziggurat.MoveDirection.*;

public class GroundUpBot extends Bot {
    Random random = new Random();
    int ticks = 0;

    private static Point relativePositionFrom(MoveDirection direction, Point from) {//For WeightCrab/CrabberInTraining
        int x = from.x;
        int y = from.y;
        return switch (direction) {
            case NORTH -> new Point(x, y - 1);
            case SOUTH -> new Point(x, y + 1);
            case EAST -> new Point(x + 1, y);
            case WEST -> new Point(x - 1, y);
            case NORTH_EAST -> new Point(x + 1, y - 1);
            case NORTH_WEST -> new Point(x - 1, y - 1);
            case SOUTH_EAST -> new Point(x + 1, y + 1);
            case SOUTH_WEST -> new Point(x - 1, y + 1);
            default -> // why won't it let me just do an exhaustive enum match ;(
                from;
        };
    }

    private static int elevationAt(Point point) {//For WeightCrab/CrabberInTraining
        int fromCorner = Math.min(point.x, point.y) + 1;
        return Math.min(fromCorner, 12 - fromCorner);
    }

    private static Point defaultUpFrom(Point from) {//For WeightCrab/CrabberInTraining
        for (MoveDirection direction : MoveDirection.values()) {
            if (elevationAt(relativePositionFrom(direction, from)) > elevationAt(from)) {
                return relativePositionFrom(direction, from);
            }
        }
        return from;
    }

    private static boolean withinReachOf(Point a, Point b) {//For WeightCrab/CrabberInTraining
        return Math.abs(a.x - b.x) <= 1 && Math.abs(a.y - b.y) <= 1;
    }

    private static Point whereMoveTake(MoveDirection direction, Bot bot) {//Generally used by GroundUpBot
        int x = bot.getPosition().x;
        int y = bot.getPosition().y;
        return switch (direction) {
            case NORTH -> new Point(x, y - 1);
            case SOUTH -> new Point(x, y + 1);
            case EAST -> new Point(x + 1, y);
            case WEST -> new Point(x - 1, y);
            case NORTH_EAST -> new Point(x + 1, y - 1);
            case NORTH_WEST -> new Point(x - 1, y - 1);
            case SOUTH_EAST -> new Point(x + 1, y + 1);
            case SOUTH_WEST -> new Point(x - 1, y + 1);
            default -> bot.getPosition();
        };
    }

    @Override
    protected boolean fight(Bot opponent) {
        if (ticks < 10 || (50 < ticks && ticks < 60) || (150 < ticks && ticks < 160) || (300 < ticks && ticks < 310) || (800 < ticks && ticks < 810)) {
            return getStrength() > 2; //Fight if not helpless on first ten ticks of each round
        } else {
            return getStrength() > 5; //Otherwise, fight once strong enough to stand up to some stuff
        }

    }

    private MoveDirection whichWayUp(Bot bot) {//For ChaoticWalkerIII/CrabberInTraining. Can now take arbitrary bot instead of just current bot.
        int relEvUp = bot.getElevationRelative(NORTH) - bot.getElevation();
        int relEvDown = bot.getElevationRelative(SOUTH) - bot.getElevation();
        int relEvLeft = bot.getElevationRelative(WEST) - bot.getElevation();
        if (relEvUp > 0) {
            return NORTH;
        } else if (relEvDown > 0) {
            return SOUTH;
        } else if (relEvLeft > 0) {
            return WEST;
        } else {
            return EAST;
        }
    }

    private MoveDirection moveBotUp(Bot bot) {//Used by most bots as a replacement for MoveUp
        for (MoveDirection direction : MoveDirection.values()) {
            if (getElevationRelative(direction) > bot.getElevation()) {
                return direction;
            }
        }
        return MoveDirection.NONE;
    }

    protected HashMap<Point, Double> whatMove(Bot bot, HashMap<Point, Double> defaultGrid)//The main function
    {/*
    This predicts the movement of any bot it is given and saves the probabilities of motion to each point on the HashMap.
    */
        if (bot instanceof Crab) {
            Point p = whereMoveTake(moveBotUp(bot), bot);
            defaultGrid.put(p, defaultGrid.getOrDefault(p, 0.) - 1);//Crab is deterministic, it's easy to predict.
            return defaultGrid;
        } else if (bot instanceof RealisticPacifist) {
            if (bot.getElevation() > 4) {
                defaultGrid.put(bot.getPosition(), defaultGrid.getOrDefault(bot.getPosition(), 0.) - .7);
                Point p = whereMoveTake(moveBotUp(bot), bot); //70-30 for RealisticPacifist when at high elevation.
                defaultGrid.put(p, defaultGrid.getOrDefault(p, 0.) - .3);
                return defaultGrid;
            } else {
                Point p = whereMoveTake(moveBotUp(bot), bot);
                defaultGrid.put(p, defaultGrid.getOrDefault(p, 0.) - .95); //95% it moves up in these cases
                if (bot.getPosition().x > 7) { //Otherwise, if near an edge, move towards center
                    Point p2 = whereMoveTake(WEST, bot);
                    defaultGrid.put(p2, defaultGrid.getOrDefault(p2, 0.) - .05);
                } else if (bot.getPosition().y > 7) {
                    Point p2 = whereMoveTake(NORTH, bot);
                    defaultGrid.put(p2, defaultGrid.getOrDefault(p2, 0.) - .05);
                } else if (bot.getPosition().x < 4) {
                    Point p2 = whereMoveTake(EAST, bot);
                    defaultGrid.put(p2, defaultGrid.getOrDefault(p2, 0.) - .05);
                } else if (bot.getPosition().y < 4) {
                    Point p2 = whereMoveTake(SOUTH, bot);
                    defaultGrid.put(p2, defaultGrid.getOrDefault(p2, 0.) - .05);
                } else {//Otherwise move randomly on a cardinal direction
                    Point p2 = whereMoveTake(WEST, bot);
                    defaultGrid.put(p2, defaultGrid.getOrDefault(p2, 0.) - .0125);
                    Point p3 = whereMoveTake(NORTH, bot);
                    defaultGrid.put(p3, defaultGrid.getOrDefault(p3, 0.) - .0125);
                    Point p4 = whereMoveTake(EAST, bot);
                    defaultGrid.put(p4, defaultGrid.getOrDefault(p4, 0.) - .0125);
                    Point p5 = whereMoveTake(SOUTH, bot);
                    defaultGrid.put(p5, defaultGrid.getOrDefault(p5, 0.) - .0125);
                }
                return defaultGrid;
            }
        } else if (bot instanceof JazzJock) {
            Point pos = bot.getPosition();
            if (ticks < 4 || (50 < ticks && ticks < 54) || (150 < ticks && ticks < 154) || (300 < ticks && ticks < 304) || (800 < ticks && ticks < 804)) {//Can't check its strength, so instead assume it spends ticks 0-3 preparing.
                if (pos.x == pos.y)//On northwest-southeast diagonal?
                {
                    if (pos.x + pos.y < 10)//On the northwest side?
                    {
                        Point p = whereMoveTake(EAST, bot);//Move either east or south.
                        Point p2 = whereMoveTake(SOUTH, bot);
                        defaultGrid.put(p, defaultGrid.getOrDefault(p, 0.) - .5);
                        defaultGrid.put(p2, defaultGrid.getOrDefault(p2, 0.) - .5);
                    } else {
                        Point p = whereMoveTake(NORTH, bot); //Otherwise, move north or west.
                        Point p2 = whereMoveTake(WEST, bot);
                        defaultGrid.put(p, defaultGrid.getOrDefault(p, 0.) - .5);
                        defaultGrid.put(p2, defaultGrid.getOrDefault(p2, 0.) - .5);
                    }
                } else if (pos.x + pos.y == 10)//On the other diagonal?
                {
                    if (pos.x - pos.y > 0)//Check which side of top you are on.
                    {
                        Point p = whereMoveTake(SOUTH, bot);//Move south or west.
                        Point p2 = whereMoveTake(WEST, bot);
                        defaultGrid.put(p, defaultGrid.getOrDefault(p, 0.) - .5);
                        defaultGrid.put(p2, defaultGrid.getOrDefault(p2, 0.) - .5);
                    } else {
                        Point p = whereMoveTake(EAST, bot);//Move north or east.
                        Point p2 = whereMoveTake(NORTH, bot);
                        defaultGrid.put(p, defaultGrid.getOrDefault(p, 0.) - .5);
                        defaultGrid.put(p2, defaultGrid.getOrDefault(p2, 0.) - .5);
                    }
                } else if (pos.x + pos.y < 10)//North or west of center?
                {
                    if (pos.x - pos.y < 0)//If west, move N/S
                    {
                        Point p = whereMoveTake(NORTH, bot);
                        Point p2 = whereMoveTake(SOUTH, bot);
                        defaultGrid.put(p, defaultGrid.getOrDefault(p, 0.) - .5);
                        defaultGrid.put(p2, defaultGrid.getOrDefault(p2, 0.) - .5);
                    } else//If north, move E/W
                    {
                        Point p = whereMoveTake(EAST, bot);
                        Point p2 = whereMoveTake(WEST, bot);
                        defaultGrid.put(p, defaultGrid.getOrDefault(p, 0.) - .5);
                        defaultGrid.put(p2, defaultGrid.getOrDefault(p2, 0.) - .5);
                    }
                } else//Do the same for the southeastern side.
                {
                    if (pos.x - pos.y < 0) {
                        Point p = whereMoveTake(EAST, bot);
                        Point p2 = whereMoveTake(WEST, bot);
                        defaultGrid.put(p, defaultGrid.getOrDefault(p, 0.) - .5);
                        defaultGrid.put(p2, defaultGrid.getOrDefault(p2, 0.) - .5);
                    } else {
                        Point p = whereMoveTake(NORTH, bot);
                        Point p2 = whereMoveTake(SOUTH, bot);
                        defaultGrid.put(p, defaultGrid.getOrDefault(p, 0.) - .5);
                        defaultGrid.put(p2, defaultGrid.getOrDefault(p2, 0.) - .5);
                    }
                }
            } else//Strength high enough to move
            {
                if (bot.getElevation() == 6)//Are you on top?
                {
                    defaultGrid.put(pos, defaultGrid.getOrDefault(pos, 0.) - 1);//If so, JazzJock won't move.
                } else if (pos.x == pos.y)//Are you on that diagonal?
                {
                    if (pos.x < 5)//Figure out which side
                    {
                        //Move southeast if that's towards the top
                        defaultGrid.put(whereMoveTake(SOUTH_EAST, bot), defaultGrid.getOrDefault(whereMoveTake(SOUTH_EAST, bot), 0.) - 1);
                    } else {
                        //Otherwise, move northwest
                        defaultGrid.put(whereMoveTake(NORTH_WEST, bot), defaultGrid.getOrDefault(whereMoveTake(NORTH_WEST, bot), 0.) - 1);
                    }
                } else if (pos.x + pos.y == 10)//Are you on the second diagonal?
                {
                    if (pos.x - pos.y < 0) {
                        //If so, move northeast or southwest, depending on which is towards the center.
                        defaultGrid.put(whereMoveTake(NORTH_EAST, bot), defaultGrid.getOrDefault(whereMoveTake(NORTH_EAST, bot), 0.) - 1);
                    } else {
                        defaultGrid.put(whereMoveTake(SOUTH_WEST, bot), defaultGrid.getOrDefault(whereMoveTake(SOUTH_WEST, bot), 0.) - 1);
                    }
                } else if (bot.getElevation() == 5)//Are you on the second highest level?
                {
                    if (pos.x + pos.y < 10)//If so, move directly to the top via cardinal directions.
                    {
                        if (pos.x - pos.y < 0) {
                            defaultGrid.put(whereMoveTake(EAST, bot), defaultGrid.getOrDefault(whereMoveTake(EAST, bot), 0.) - 1);
                        } else {
                            defaultGrid.put(whereMoveTake(SOUTH, bot), defaultGrid.getOrDefault(whereMoveTake(SOUTH, bot), 0.) - 1);
                        }
                    } else {
                        if (pos.x - pos.y < 0) {
                            defaultGrid.put(whereMoveTake(NORTH, bot), defaultGrid.getOrDefault(whereMoveTake(NORTH, bot), 0.) - 1);
                        } else {
                            defaultGrid.put(whereMoveTake(WEST, bot), defaultGrid.getOrDefault(whereMoveTake(WEST, bot), 0.) - 1);
                        }
                    }
                } else//Otherwise, move somewhat randomly towards the top. Put a 1/3 probability for all 3 options.
                {
                    if (pos.x + pos.y < 10) {
                        if (pos.x - pos.y < 0)//If on the west side
                        {
                            //Move east or east-adjacent.
                            Point p = whereMoveTake(EAST, bot);
                            Point p2 = whereMoveTake(SOUTH_EAST, bot);
                            Point p3 = whereMoveTake(NORTH_EAST, bot);
                            defaultGrid.put(p, defaultGrid.getOrDefault(p, 0.) - 1. / 3);
                            defaultGrid.put(p2, defaultGrid.getOrDefault(p2, 0.) - 1. / 3);
                            defaultGrid.put(p3, defaultGrid.getOrDefault(p3, 0.) - 1. / 3);
                        } else//If on the east side
                        {
                            //Move west or diagonally west.
                            Point p = whereMoveTake(WEST, bot);
                            Point p2 = whereMoveTake(SOUTH_WEST, bot);
                            Point p3 = whereMoveTake(NORTH_WEST, bot);
                            defaultGrid.put(p, defaultGrid.getOrDefault(p, 0.) - 1. / 3);
                            defaultGrid.put(p2, defaultGrid.getOrDefault(p2, 0.) - 1. / 3);
                            defaultGrid.put(p3, defaultGrid.getOrDefault(p3, 0.) - 1. / 3);
                        }
                    } else {
                        if (pos.x - pos.y < 0)//In the south?
                        {
                            //Move towards the north.
                            Point p = whereMoveTake(NORTH, bot);
                            Point p2 = whereMoveTake(NORTH_EAST, bot);
                            Point p3 = whereMoveTake(NORTH_WEST, bot);
                            defaultGrid.put(p, defaultGrid.getOrDefault(p, 0.) - 1. / 3);
                            defaultGrid.put(p2, defaultGrid.getOrDefault(p2, 0.) - 1. / 3);
                            defaultGrid.put(p3, defaultGrid.getOrDefault(p3, 0.) - 1. / 3);
                        } else//Otherwise
                        {
                            //Move south.
                            Point p = whereMoveTake(SOUTH, bot);
                            Point p2 = whereMoveTake(SOUTH_EAST, bot);
                            Point p3 = whereMoveTake(SOUTH_WEST, bot);
                            defaultGrid.put(p, defaultGrid.getOrDefault(p, 0.) - 1. / 3);
                            defaultGrid.put(p2, defaultGrid.getOrDefault(p2, 0.) - 1. / 3);
                            defaultGrid.put(p3, defaultGrid.getOrDefault(p3, 0.) - 1. / 3);
                        }
                    }
                }
            }
            return defaultGrid;
        } else if (bot instanceof ChaoticWalkerIII)//Is it a ChaoticWalker?
        {
            if (ticks < 10)//On the first few rounds, ChaoticWalker moves diagonally upwards. Can't guess which way, so assign 50% to each.
            {
                MoveDirection v = whichWayUp(bot);//Find which way the bot will head
                if (v == NORTH)//Then fill in the diagonal moves from that
                {
                    Point p = whereMoveTake(NORTH_EAST, bot);
                    Point p2 = whereMoveTake(NORTH_WEST, bot);
                    defaultGrid.put(p, defaultGrid.getOrDefault(p, 0.) - .5);
                    defaultGrid.put(p2, defaultGrid.getOrDefault(p2, 0.) - .5);
                } else if (v == SOUTH) {
                    Point p = whereMoveTake(SOUTH_EAST, bot);
                    Point p2 = whereMoveTake(SOUTH_WEST, bot);
                    defaultGrid.put(p, defaultGrid.getOrDefault(p, 0.) - .5);
                    defaultGrid.put(p2, defaultGrid.getOrDefault(p2, 0.) - .5);
                } else if (v == EAST) {
                    Point p = whereMoveTake(NORTH_EAST, bot);
                    Point p2 = whereMoveTake(SOUTH_EAST, bot);
                    defaultGrid.put(p, defaultGrid.getOrDefault(p, 0.) - .5);
                    defaultGrid.put(p2, defaultGrid.getOrDefault(p2, 0.) - .5);
                } else {
                    Point p = whereMoveTake(NORTH_WEST, bot);
                    Point p2 = whereMoveTake(SOUTH_WEST, bot);
                    defaultGrid.put(p, defaultGrid.getOrDefault(p, 0.) - .5);
                    defaultGrid.put(p2, defaultGrid.getOrDefault(p2, 0.) - .5);
                }
            } else if (bot.getElevation() < 6)//Otherwise, 90% of the time you head straight up.
            {

                Point p1 = whereMoveTake(moveBotUp(bot), bot);
                defaultGrid.put(p1, defaultGrid.getOrDefault(p1, 0.) - .9);
                MoveDirection v = whichWayUp(bot);
                if (v == NORTH)//It sometimes moves diagonally though, but quite rare.
                {
                    Point p = whereMoveTake(NORTH_EAST, bot);
                    Point p2 = whereMoveTake(NORTH_WEST, bot);
                    defaultGrid.put(p, defaultGrid.getOrDefault(p, 0.) - 0.0475);
                    defaultGrid.put(p2, defaultGrid.getOrDefault(p2, 0.) - 0.0475);
                } else if (v == SOUTH) {
                    Point p = whereMoveTake(SOUTH_EAST, bot);
                    Point p2 = whereMoveTake(SOUTH_WEST, bot);
                    defaultGrid.put(p, defaultGrid.getOrDefault(p, 0.) - 0.0475);
                    defaultGrid.put(p2, defaultGrid.getOrDefault(p2, 0.) - 0.0475);
                } else if (v == EAST) {
                    Point p = whereMoveTake(NORTH_EAST, bot);
                    Point p2 = whereMoveTake(SOUTH_EAST, bot);
                    defaultGrid.put(p, defaultGrid.getOrDefault(p, 0.) - 0.0475);
                    defaultGrid.put(p2, defaultGrid.getOrDefault(p2, 0.) - 0.0475);
                } else {
                    Point p = whereMoveTake(NORTH_WEST, bot);
                    Point p2 = whereMoveTake(SOUTH_WEST, bot);
                    defaultGrid.put(p, defaultGrid.getOrDefault(p, 0.) - 0.0475);
                    defaultGrid.put(p2, defaultGrid.getOrDefault(p2, 0.) - 0.0475);
                }
                //Even less often, it will move at random or stay still.
                defaultGrid.put(whereMoveTake(NORTH, bot), defaultGrid.getOrDefault(whereMoveTake(NORTH, bot), 0.) - 0.000125);
                defaultGrid.put(whereMoveTake(SOUTH, bot), defaultGrid.getOrDefault(whereMoveTake(SOUTH, bot), 0.) - 0.000125);
                defaultGrid.put(whereMoveTake(EAST, bot), defaultGrid.getOrDefault(whereMoveTake(EAST, bot), 0.) - 0.000125);
                defaultGrid.put(whereMoveTake(WEST, bot), defaultGrid.getOrDefault(whereMoveTake(WEST, bot), 0.) - 0.000125);
                defaultGrid.put(whereMoveTake(NORTH_EAST, bot), defaultGrid.getOrDefault(whereMoveTake(NORTH_EAST, bot), 0.) - 0.000125);
                defaultGrid.put(whereMoveTake(NORTH_WEST, bot), defaultGrid.getOrDefault(whereMoveTake(NORTH_WEST, bot), 0.) - 0.000125);
                defaultGrid.put(whereMoveTake(SOUTH_EAST, bot), defaultGrid.getOrDefault(whereMoveTake(SOUTH_EAST, bot), 0.) - 0.000125);
                defaultGrid.put(whereMoveTake(SOUTH_WEST, bot), defaultGrid.getOrDefault(whereMoveTake(SOUTH_WEST, bot), 0.) - 0.000125);
                defaultGrid.put(bot.getPosition(), defaultGrid.getOrDefault(bot.getPosition(), 0.) - 0.004);

            } else {
                //If on top,it will stay 30% of the time, but move the other 70%.
                defaultGrid.put(bot.getPosition(), defaultGrid.getOrDefault(bot.getPosition(), 0.) - 0.3);
                defaultGrid.put(whereMoveTake(NORTH, bot), defaultGrid.getOrDefault(whereMoveTake(NORTH, bot), 0.) - 0.0875);
                defaultGrid.put(whereMoveTake(SOUTH, bot), defaultGrid.getOrDefault(whereMoveTake(SOUTH, bot), 0.) - 0.0875);
                defaultGrid.put(whereMoveTake(EAST, bot), defaultGrid.getOrDefault(whereMoveTake(EAST, bot), 0.) - 0.0875);
                defaultGrid.put(whereMoveTake(WEST, bot), defaultGrid.getOrDefault(whereMoveTake(WEST, bot), 0.) - 0.0875);
                defaultGrid.put(whereMoveTake(NORTH_EAST, bot), defaultGrid.getOrDefault(whereMoveTake(NORTH_EAST, bot), 0.) - 0.0875);
                defaultGrid.put(whereMoveTake(NORTH_WEST, bot), defaultGrid.getOrDefault(whereMoveTake(NORTH_WEST, bot), 0.) - 0.0875);
                defaultGrid.put(whereMoveTake(SOUTH_EAST, bot), defaultGrid.getOrDefault(whereMoveTake(SOUTH_EAST, bot), 0.) - 0.0875);
                defaultGrid.put(whereMoveTake(SOUTH_WEST, bot), defaultGrid.getOrDefault(whereMoveTake(SOUTH_WEST, bot), 0.) - 0.0875);
            }
            return defaultGrid;
        } else if (bot instanceof WeightCrab)//Otherwise, is it WeightCrab?
        {
            if (bot.getElevation() == 6)//WeightCrab never moves down when on the top.
            {
                defaultGrid.put(bot.getPosition(), defaultGrid.getOrDefault(bot.getPosition(), 0.) - 1);
            } else {
                Map<MoveDirection, Integer> weights = new HashMap<>();

                Map<Point, Integer> botPositionCounts = new HashMap<>();

                for (Bot bot2 : bot.getPlayers()) {//Otherwise, WeightCrab uses a weighting algorithm which assumes bots would rather go up or stay still.
                    Point position = bot2.getPosition();
                    botPositionCounts.put(position, botPositionCounts.getOrDefault(position, 0) + 1);//Mark each point with a bot
                }
                for (MoveDirection direction : MoveDirection.values()) {

                    int weight = (bot.getElevationRelative(direction) - bot.getElevation()) * 89;//Assume going up is better
                    Point candidate = relativePositionFrom(direction, bot.getPosition());

                    for (Map.Entry<Point, Integer> entry : botPositionCounts.entrySet()) {

                        Point position = entry.getKey();
                        int count = entry.getValue();

                        if (defaultUpFrom(position).equals(candidate)) {
                            weight -= 2 * count;
                        }//If you would move somewhere straight up from a bot, WeightCrab avoids that move.

                        if (elevationAt(candidate) > elevationAt(position) && withinReachOf(candidate, position)) {
                            weight -= count;
                        }//If you would move next to a bot, WeightCrab prefers to avoid that move.

                    }
                    weights.put(direction, weight);

                }

                MoveDirection chosen = weights.entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey();

                defaultGrid.put(whereMoveTake(chosen, bot), defaultGrid.get(whereMoveTake(chosen, bot)) - 1);//Find the best option.

            }
            return defaultGrid;
        } else if (bot instanceof CrabberInTraining)//Now, what if it's CrabberInTraining?
        {
            if (ticks < 10 || (50 < ticks && ticks < 60) || (150 < ticks && ticks < 160) || (300 < ticks && ticks < 310) || (800 < ticks && ticks < 810)) {
                //On the first 10 ticks of a round, it moves diagonally upwards, so we can reuse the code from ChaoticWalkerIII.
                MoveDirection v = whichWayUp(bot);
                if (v == NORTH) {
                    Point p = whereMoveTake(NORTH_EAST, bot);
                    Point p2 = whereMoveTake(NORTH_WEST, bot);
                    defaultGrid.put(p, defaultGrid.getOrDefault(p, 0.) - .5);
                    defaultGrid.put(p2, defaultGrid.getOrDefault(p2, 0.) - .5);
                } else if (v == SOUTH) {
                    Point p = whereMoveTake(SOUTH_EAST, bot);
                    Point p2 = whereMoveTake(SOUTH_WEST, bot);
                    defaultGrid.put(p, defaultGrid.getOrDefault(p, 0.) - .5);
                    defaultGrid.put(p2, defaultGrid.getOrDefault(p2, 0.) - .5);
                } else if (v == EAST) {
                    Point p = whereMoveTake(NORTH_EAST, bot);
                    Point p2 = whereMoveTake(SOUTH_EAST, bot);
                    defaultGrid.put(p, defaultGrid.getOrDefault(p, 0.) - .5);
                    defaultGrid.put(p2, defaultGrid.getOrDefault(p2, 0.) - .5);
                } else {
                    Point p = whereMoveTake(NORTH_WEST, bot);
                    Point p2 = whereMoveTake(SOUTH_WEST, bot);
                    defaultGrid.put(p, defaultGrid.getOrDefault(p, 0.) - .5);
                    defaultGrid.put(p2, defaultGrid.getOrDefault(p2, 0.) - .5);
                }
            } else if (bot.getElevation() == 6) {
                //If it's on top, it will usually stay still, but rarely will move to another point.
                defaultGrid.put(whereMoveTake(NORTH, bot), defaultGrid.get(whereMoveTake(NORTH, bot)) - 0.0125);
                defaultGrid.put(whereMoveTake(SOUTH, bot), defaultGrid.get(whereMoveTake(SOUTH, bot)) - 0.0125);
                defaultGrid.put(whereMoveTake(EAST, bot), defaultGrid.get(whereMoveTake(EAST, bot)) - 0.0125);
                defaultGrid.put(whereMoveTake(WEST, bot), defaultGrid.get(whereMoveTake(WEST, bot)) - 0.0125);
                defaultGrid.put(whereMoveTake(NORTH_EAST, bot), defaultGrid.get(whereMoveTake(NORTH_EAST, bot)) - 0.0125);
                defaultGrid.put(whereMoveTake(NORTH_WEST, bot), defaultGrid.get(whereMoveTake(NORTH_WEST, bot)) - 0.0125);
                defaultGrid.put(whereMoveTake(SOUTH_EAST, bot), defaultGrid.get(whereMoveTake(SOUTH_EAST, bot)) - 0.0125);
                defaultGrid.put(whereMoveTake(SOUTH_WEST, bot), defaultGrid.get(whereMoveTake(SOUTH_WEST, bot)) - 0.0125);
                defaultGrid.put(bot.getPosition(), defaultGrid.getOrDefault(bot.getPosition(), 0.) - 0.9);
            } else//Otherwise, CrabberInTraining will act like WeightCrab, but is generally more aggressive.
            {
                Map<MoveDirection, Double> weights = new HashMap<>();

                Map<Point, Integer> botPositionCounts = new HashMap<>();

                for (Bot bot2 : bot.getPlayers()) {
                    Point position = bot2.getPosition();
                    botPositionCounts.put(position, botPositionCounts.getOrDefault(position, 0) + 1);
                }
                for (MoveDirection direction : MoveDirection.values()) {

                    double weight = (bot.getElevationRelative(direction) - bot.getElevation()) * 20;
                    Point candidate = relativePositionFrom(direction, bot.getPosition());

                    for (Map.Entry<Point, Integer> entry : botPositionCounts.entrySet()) {

                        Point position = entry.getKey();
                        int count = entry.getValue();

                        if (defaultUpFrom(position).equals(candidate)) {
                            weight -= .3 * count;
                        }

                        if (elevationAt(candidate) > elevationAt(position) && withinReachOf(candidate, position)) {
                            weight -= count;
                        }

                    }
                    weights.put(direction, weight);
                }

                MoveDirection chosen = weights.entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey();

                defaultGrid.put(whereMoveTake(chosen, bot), defaultGrid.getOrDefault(whereMoveTake(chosen, bot), 0.) - 1);//Guess where it will move.

            }
            return defaultGrid;
        } else//If it's an unknown bot, assume it acts like Crab. Most bots do a good chunk of the time.
        {
            Point p = whereMoveTake(moveBotUp(bot), bot);
            defaultGrid.put(p, defaultGrid.getOrDefault(p, 0.) - 1);
            return defaultGrid;
        }
    }

    @Override
    protected void tick() {
        double scalingFactor = 2; //use to vary how much you care about going up
        ticks++;//Advance the turns.
        HashMap<Point, Double> weights = new HashMap<Point, Double>();
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++)//For all the points on the grid:
            {
                double val;
                Point loc = new Point(i, j);
                if (getStrength() > 8)//If you are strong enough
                {
                    if (Math.min(10 - Math.max(j, i), Math.min(j, i)) + 1 == 6) {
                        val = 12.5 * scalingFactor;
                    }//Assign (usually) 25 points to top level
                    else if (Math.min(10 - Math.max(j, i), Math.min(j, i)) + 1 == 5) {
                        val = 7.5 * scalingFactor;
                    }//15 to level 5
                    else {
                        val = .1 * Math.pow(((double) (Math.min(10 - Math.max(j, i), Math.min(j, i)) + 1)), 3);
                    }
                }//6.4 to level 4, 2.7 to level 3, .8 to level 2, and .1 to level 1.
                //This means that it will always move up, except between levels 1 and 3, where a high chance of another bot interfering could cause it to stay.
                //Theoretically, if it also knows multiple bots will move to the same location, it won't move there, but on level 6, it would take 5 other bots to try, so it's unlikely to occur the one spot it could.
                else {
                    val = (Math.min(10 - Math.max(j, i), Math.min(j, i)) + 1) * scalingFactor;//Otherwise, assign 12 points to level 6, 10 to level 5...
                }
                weights.put(loc, val / 2.1);//Divide these weights by 2.1, so that a value of 1 (the standard if it knows a bot will move somewhere) is enough to make a cell not worth visiting relative to one a level lower if the strength is less than 9.
            }
        }
        for (Bot b : getPlayers()) {
            weights = whatMove(b, weights);//Edit weights for each bot in the game using all that code above.
        }

        Point bestScoredLocation = getPosition();
        int x = getPosition().x;
        int y = getPosition().y;
        double bestScore = -1000;//Define some values we need to decide where to move.
        HashMap<Point, Double> movablePlaces = new HashMap<Point, Double>();//Create a list of places close enough to move to
        List<Point> highestScored = new ArrayList<Point>();//Create a list of which of those have the highest score
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                movablePlaces.put(new Point(x + i, y + j), weights.getOrDefault(new Point(x + i, y + j), 0.));
                //Fill in the list of places to move to and their weights
            }
        }
        for (Map.Entry<Point, Double> entry : movablePlaces.entrySet()) {//Find highest scored points
            if (bestScore == entry.getValue()) {
                bestScore = entry.getValue();
                highestScored.add(entry.getKey());
            } else if (entry.getValue() > bestScore) {
                bestScore = entry.getValue();
                highestScored.clear();
                highestScored.add(entry.getKey());
            }
        }
        bestScoredLocation = highestScored.get(random.nextInt(highestScored.size()));//Choose one at random (to stop other bots from using its own technique against it very well)
        if (bestScoredLocation.x == x)//Decide where to move.
        {
            if (bestScoredLocation.y < y) {
                move(NORTH);
            } else if (bestScoredLocation.y > y) {
                move(SOUTH);
            } else {
                move(NONE);
            }
        } else if (bestScoredLocation.x > x) {
            if (bestScoredLocation.y < y) {
                move(NORTH_EAST);
            } else if (bestScoredLocation.y > y) {
                move(SOUTH_EAST);
            } else {
                move(EAST);
            }
        } else {
            if (bestScoredLocation.y < y) {
                move(NORTH_WEST);
            } else if (bestScoredLocation.y > y) {
                move(SOUTH_WEST);
            } else {
                move(WEST);
            }
        }


    }

}