package io.github.seggan.kingoftheziggurat;

import io.github.seggan.kingoftheziggurat.bots.AttackBot;
import io.github.seggan.kingoftheziggurat.bots.BotWrapper;
import io.github.seggan.kingoftheziggurat.bots.RandomBot;
import io.github.seggan.kingoftheziggurat.bots.ScaredyBot;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static final String MAP = """
        11111111111
        12222222221
        12333333321
        12344444321
        12345554321
        12345654321
        12345554321
        12344444321
        12333333321
        12222222221
        11111111111""";

    public static void main(String[] args) {
        String[] split = MAP.split("\n");
        int[][] ziggurat = new int[split.length][split[0].length()];
        for (int i = 0; i < split.length; i++) {
            String line = split[i];
            ziggurat[i] = line.chars().map(c -> c - '0').toArray();
        }

        Map<Bot, List<Double>> scores = new LinkedHashMap<>();

        int[] rounds = new int[]{
            50,
            100,
            250,
            500,
            1000,
        };

        // Add your bots here
        Bot[] bots = new Bot[]{
            new AttackBot(),
            new RandomBot(),
            new ScaredyBot(),
            new BotWrapper("python", "bot.py")
        };

        int index;
        for (int i = 0; i < rounds.length; i++) {
            index = 1;
            int round = rounds[i];

            for (Bot bot : bots) {
                bot.strength = 0;
                bot.points = 0;
                bot.move(MoveDirection.NONE);
            }

            Ziggurat z = new Ziggurat(ziggurat, round, bots);
            z.run();
            System.out.println("Round " + (i + 1) + " (" + round + " turns)" + ": ");
            for (Bot bot : z.getPlayers()) {
                double avg = bot.points / (double) round;
                System.out.printf(
                    "\t%s (#%d): %d, Averaged: %.3f%n",
                    bot.getClass().getSimpleName(),
                    index++,
                    bot.points,
                    avg
                );
                scores.computeIfAbsent(bot, k -> new ArrayList<>()).add(avg);
            }
            System.out.println();
        }

        System.out.println("Final Scores:");
        index = 1;
        for (Bot bot : scores.keySet()) {
            System.out.printf(
                "\t%s (#%d): %.3f%n",
                bot.getClass().getSimpleName(),
                index++,
                scores.get(bot).stream().mapToDouble(d -> d).average().orElse(0)
            );
            if (bot instanceof BotWrapper wrapper) {
                wrapper.close();
            }
        }
    }
}
