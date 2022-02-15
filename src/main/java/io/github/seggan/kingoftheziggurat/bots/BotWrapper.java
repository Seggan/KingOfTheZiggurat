package io.github.seggan.kingoftheziggurat.bots;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.seggan.kingoftheziggurat.Bot;
import io.github.seggan.kingoftheziggurat.Main;
import io.github.seggan.kingoftheziggurat.MoveDirection;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.util.Scanner;

public class BotWrapper extends Bot implements Closeable {

    private final Process process;
    private final Scanner processOutput;
    private final PrintStream processInput;

    public BotWrapper(String... process) {
        try {
            this.process = new ProcessBuilder(process).start();
            this.processOutput = new Scanner(this.process.getInputStream());
            this.processInput = new PrintStream(this.process.getOutputStream());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    protected boolean fight(Bot opponent) {
        processInput.println("fight");
        processInput.println(buildJson());
        return Boolean.parseBoolean(processOutput.nextLine());
    }

    @Override
    protected void tick() {
        processInput.println("tick");
        processInput.println(buildJson());
        move(MoveDirection.valueOf(processOutput.nextLine()));
    }

    private String buildJson() {
        JsonObject json = new JsonObject();
        json.addProperty("x", getPosition().x);
        json.addProperty("y", getPosition().y);
        json.addProperty("strength", getStrength());
        json.addProperty("elevation", getElevation());
        json.addProperty("map", Main.MAP);

        JsonArray players = new JsonArray();
        for (Bot bot : getPlayers()) {
            players.add(bot.toJson());
        }
        json.add("players", players);

        return json.toString();
    }

    @Override
    public void close() {
        process.destroy();
    }
}
