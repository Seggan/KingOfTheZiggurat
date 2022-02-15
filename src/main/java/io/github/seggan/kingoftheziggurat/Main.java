package io.github.seggan.kingoftheziggurat;

import io.github.seggan.kingoftheziggurat.impl.Bot;
import io.github.seggan.kingoftheziggurat.impl.Ziggurat;

import java.util.Arrays;

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
        Ziggurat z = new Ziggurat(ziggurat, 50, new ScaredyBot(), new AttackBot(), new RandomBot());
        z.run();
        for (Bot bot : z.getPlayers()) {
            System.out.println(bot.getClass().getSimpleName() + ": " + bot.getPoints());
        }
    }
}
