package io.github.seggan.kingoftheziggurat.bots;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.seggan.kingoftheziggurat.Bot;
import io.github.seggan.kingoftheziggurat.Main;
import io.github.seggan.kingoftheziggurat.MoveDirection;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public class BotWrapper extends Bot implements Closeable {

    private final Process process;
    private final ServerSocket server;
    private final Socket connection;

    private final PrintWriter out;
    private final BufferedReader in;

    public BotWrapper(String... process) {
        try {
            String[] args = Arrays.copyOf(process, process.length + 1);
            int port = ThreadLocalRandom.current().nextInt(10000, 20000);
            args[process.length] = Integer.toString(port);
            this.process = new ProcessBuilder(args).inheritIO().start();
            this.server = new ServerSocket(port);
            this.connection = server.accept();
            this.out = new PrintWriter(connection.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected boolean fight(Bot opponent) {
        out.println("fight");
        JsonObject json = buildJson();
        json.add("opponent", opponent.toJson());
        out.println(json);
        try {
            return in.readLine().equalsIgnoreCase("true");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void tick() {
        out.println("tick");
        out.println(buildJson());
        try {
            move(MoveDirection.valueOf(in.readLine().toUpperCase(Locale.ROOT)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private JsonObject buildJson() {
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

        return json;
    }

    @Override
    public void close() {
        try {
            out.close();
            in.close();
            connection.close();
            server.close();
            process.destroy();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
