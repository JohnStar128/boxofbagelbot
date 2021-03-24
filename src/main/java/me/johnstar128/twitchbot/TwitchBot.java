package me.johnstar128.twitchbot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.PircBot;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.StringJoiner;

public class TwitchBot extends PircBot {

    public ConfigManager configs = new ConfigManager(this);
    public Random random = new Random();
    public String channel = ConfigManager.getChannelName();
    public static final String ANSI_RED = "\u001B[31m";
    public List<String> modList = new ArrayList<>();
    public static String userDir = System.getProperty("user.dir");
    public static Map<String, String> commands = new HashMap<>();
    public static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String[] args) {
        ConfigManager.generateConfigs();
        TwitchBot bot = new TwitchBot();
        bot.setVerbose(true);
        parseCommands();

        // Connect to Twitch
        try {
            bot.connect("irc.twitch.tv", 6667, bot.configs.getOAuth());
        } catch (IOException | IrcException e) {
            e.printStackTrace();
        }
        bot.joinChannel(bot.channel);
    }

    public static void parseCommands() { ConfigManager.writeValuesToConfig("commands.json"); }

    @Override
    protected void onConnect() {
        super.onConnect();
        // Allow bot to receive Twitch command output
        sendRawLine("CAP REQ :twitch.tv/commands");

        // Add broadcaster's name to the modList arraylist
        // Used for determining which users can use certain hardcoded commands
        modList.add(channel.toLowerCase(Locale.ROOT).substring(1));

        // Populate modList ArrayList with contents of "/mods"
        // @TODO Switch to PircBotX's event system to retrieve mod list
        sendMessage(channel, "/mods");
    }

    @Override
    protected void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target, String notice) {
        super.onNotice(sourceNick, sourceLogin, sourceHostname, target, notice);
        if (notice.startsWith("The moderators of this chat are:")) {
            String[] modArray = notice.split(" ");
            Arrays.stream(modArray).skip(6).forEach(modList::add);
        }
    }

    @Override
    protected void onDisconnect() {
        while (!isConnected()) {
            modList.clear();
            try {
                reconnect();
            } catch (IOException | IrcException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
    }

    public String parsePlaceholders(String msg) {
        String returnMsg = "";
        String coinResult;
        // Coin placeholder -- rolls a number between 0 and 1
        // Sets output to heads or tails
        if (msg.contains("%coin%")) {
            int returnInt = random.nextInt(2);
            if (returnInt == 0) {
                coinResult = "heads";
            } else {
                coinResult = "tails";
            }
            returnMsg = msg.replace("%coin%", coinResult);
        }
        return returnMsg;
    }

    public void onMessage(String chan, String sender, String login, String hostname, String msg) {
        // Hardcoded commands
        if (msg.toLowerCase(Locale.ROOT).startsWith("!about")) {
            sendMessage(channel, "This is a chat bot lovingly titled \"TwitchBot1\". " +
                    "Created by JohnStar128, written in Java using an implementation of PircBot. " +
                    "Source code: https://github.com/JohnStar128/boxofbagelbot");
            return;
        }

        // List all hardcoded and user-added commands
        if (msg.toLowerCase(Locale.ROOT).startsWith("!commands")) {
            StringJoiner joiner = new StringJoiner(", ");
            if (!(commands.isEmpty())) {
                for (String name : commands.keySet()) {
                    joiner.add(name);
                }
                sendMessage(channel, "The bot commands are: !about, !commands, !addcommand, !removecommand and !editcommand. User-added commands are: " + joiner);
            } else {
                sendMessage(channel, "The bot commands are: !about, !commands, !addcommand, !removecommand and !editcommand. There are no user-added commands currently.");
            }
            return;
        }

        // Add a custom command
        // @TODO Add placeholders to custom commands that retrieve special data
        // e.g %TIME% returns system time, %UPTIME% to get stream uptime, Speedrun.com API?
        if (msg.toLowerCase(Locale.ROOT).startsWith("!addcommand")) {
            // Check if command sender is allowed to use this command
            if (!(modList.contains(sender))) {
                sendMessage(channel, "This command is only available to channel moderators or the broadcaster.");
                return;
            }

            String[] splitMsg = msg.split(" ");
            StringJoiner joiner = new StringJoiner(" ");
            BufferedWriter writer = null;
            try {
                writer = Files.newBufferedWriter(Path.of(userDir + "/commands.json"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Print usage if given incorrect syntax
            if (!(splitMsg.length > 2)) {
                sendMessage(channel, "Incorrect usage: !addcommand <name> <result>");
                return;
            }

            // Error if command already exists
            if (commands.containsKey(splitMsg[1])) {
                sendMessage(channel, "Command " + splitMsg[1] + " already exists!");
                return;
            }

            // Concatenate their message, omitting the first two parameters
            for (String s : splitMsg) {
                if (splitMsg[0].equalsIgnoreCase(s) || splitMsg[1].equalsIgnoreCase(s)) { continue; }
                joiner.add(s);
            }

            // Add command to commands List, write to commands.json
            String title = splitMsg[1];
            String returnMsg = joiner.toString();
            commands.putIfAbsent(title, returnMsg);
            gson.toJson(commands, writer);
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Done
            sendMessage(channel, "Command " + title + " added");
            return;
        }

        // Remove a user-added command
        if (msg.toLowerCase(Locale.ROOT).startsWith("!removecommand")) {
            // Check if command sender is allowed to use this command
            if (!(modList.contains(sender))) {
                sendMessage(channel, "This command is only available to channel moderators or the broadcaster.");
                return;
            }

            String[] msgSplitter = msg.split(" ");
            String title;
            BufferedWriter writer = null;
            try {
                writer = Files.newBufferedWriter(Path.of(userDir + "/commands.json"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Print usage if given incorrect syntax
            if (msgSplitter.length != 2) {
                sendMessage(channel, "Incorrect usage: !removecommand <commandname>");
                return;
            }

            // Error if command doesn't exist
            if (!(commands.containsKey(msgSplitter[1]))) {
                sendMessage(channel, "Command " + msgSplitter[1] + " doesn't exist.");
                return;
            }

            // Set the title variable to the provided command
            // Remove that command
            title = msgSplitter[1];
            commands.remove(title);
            gson.toJson(commands, writer);
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Done
            sendMessage(channel, "Command " + title + " removed.");
        }

        // Edit an existing command
        if (msg.toLowerCase(Locale.ROOT).contains("!editcommand")) {
            String[] msgSplitter = msg.split(" ");
            String title;
            String returnMsg;
            StringJoiner joiner = new StringJoiner(" ");
            BufferedWriter writer = null;
            try {
                writer = Files.newBufferedWriter(Path.of(userDir + "/commands.json"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Print usage if given incorrect syntax
            if (!(msgSplitter.length > 2)) {
                sendMessage(channel, "Incorrect usage: !editcommand <commandname> <message>");
                return;
            }

            // Error if command doesn't exist
            if (!(commands.containsKey(msgSplitter[1]))) {
                sendMessage(channel, "Command does not exist");
                return;
            }

            title = msgSplitter[1];
            // Concatenate their message, omitting the first two parameters
            for (String s : msgSplitter) {
                if (msgSplitter[0].equalsIgnoreCase(s) || msgSplitter[1].equalsIgnoreCase(s)) { continue; }
                joiner.add(s);
            }

            returnMsg = joiner.toString();
            commands.replace(title, returnMsg);
            gson.toJson(commands, writer);
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Done
            sendMessage(channel, "Command " + title + " edited");
            return;

        }

        // Print user-added commands
        if (commands.containsKey(msg)) {
            String finalMessage = parsePlaceholders(commands.get(msg));
            sendMessage(channel, finalMessage);
        }
    }
}