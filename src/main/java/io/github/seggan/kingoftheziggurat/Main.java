package io.github.seggan.kingoftheziggurat;

import io.github.seggan.kingoftheziggurat.bots.AttackBot;
import io.github.seggan.kingoftheziggurat.bots.RandomBot;
import io.github.seggan.kingoftheziggurat.bots.ScaredyBot;
import io.github.seggan.kingoftheziggurat.impl.Bot;
import io.github.seggan.kingoftheziggurat.impl.Ziggurat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        String map = """
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
        String[] split = map.split("\n");
        int[][] ziggurat = new int[split.length][split[0].length()];
        for (int i = 0; i < split.length; i++) {
            String line = split[i];
            ziggurat[i] = line.chars().map(c -> c - '0').toArray();
        }

        Map<Class<? extends Bot>, List<Double>> scores = new HashMap<>();

        int[] rounds = new int[] {
            50,
            100,
            250,
            500,
            1000,
        };

        for (int i = 0; i < rounds.length; i++) {
            int round = rounds[i];

            // Add your bots here
            Bot[] bots = new Bot[] {
                new AttackBot(),
                new RandomBot(),
                new ScaredyBot()
            };

            Ziggurat z = new Ziggurat(ziggurat, round, bots);
            z.run();
            System.out.println("Round " + (i + 1) + " (" + round + " turns)" + ": ");
            for (Bot bot : z.getPlayers()) {
                System.out.print('\t');
                System.out.print(bot.getClass().getSimpleName() + ": ");
                System.out.print("Total Score: " + bot.getPoints());
                System.out.print(", ");
                double avg = bot.getPoints() / (double) round;
                System.out.println("Averaged Score: " + avg);
                scores.computeIfAbsent(bot.getClass(), k -> new ArrayList<>()).add(avg);
            }
            System.out.println();
        }

        System.out.println("Final Scores:");
        for (Class<? extends Bot> bot : scores.keySet()) {
            System.out.print('\t');
            System.out.printf(
                "%s: %.3f%n",
                bot.getSimpleName(),
                scores.get(bot).stream().mapToDouble(d -> d).average().orElse(0)
            );
        }
    }
}
