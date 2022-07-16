package me.johnstar128.boxofbagelbot;

import me.johnstar128.boxofbagelbot.commands.Placeholders;
import me.johnstar128.boxofbagelbot.config.ConfigManager;
import me.johnstar128.boxofbagelbot.commands.Command;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.PircBot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;


public class BoxOfBagelBot extends PircBot {

    public Placeholders placeholder = new Placeholders(this);
    public static ConfigManager cfgMgn = new ConfigManager();
    public static String channel;
    public static String oAuth;
    public static String prefix;
    public static List<String> modList = new ArrayList<>();

    public static void main(String[] args) {
        BoxOfBagelBot bot = new BoxOfBagelBot();
        cfgMgn.generateConfigs();
        cfgMgn.readConfigs();
        try {
            bot.connect("irc.chat.twitch.tv", 6667, oAuth);
        } catch (IOException | IrcException e) {
            e.printStackTrace();
        }
        bot.setVerbose(true);
        bot.joinChannel(channel);
    }

    @Override
    protected void onConnect() {
        super.onConnect();

        // Allow bot to receive Twitch command output
        sendRawLine("CAP REQ :twitch.tv/commands");

        modList.add(channel.toLowerCase(Locale.ROOT).substring(1));

        // Populate modList ArrayList with contents of "/mods"
        // @TODO Switch to PircBotX's event system to retrieve mod list
        sendMessage(channel, "/mods");
    }

    @Override
    protected void onDisconnect() {
        modList.clear();
        while (!isConnected()) {
            try {
                reconnect();
            } catch (IOException | IrcException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
    }

    @Override
    protected void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target, String notice) {
        super.onNotice(sourceNick, sourceLogin, sourceHostname, target, notice);
        if (notice.startsWith("The moderators of this channel are:")) {
            String[] modArray = notice.split(" ");
            Arrays.stream(modArray).skip(6).forEach(modList::add);
        }
    }

    public void onMessage(String chan, String sender, String login, String hostname, String msg) {
        String[] msgSplit = msg.split(" ");

        // Hardcoded commands
        // Return the current command prefix
        if (msgSplit[0].equalsIgnoreCase("bot.prefix")) {
            sendMessage(channel, "The bot prefix is currently: " + prefix);
        }

        if (msgSplit[0].startsWith(prefix + "about")) {
            sendMessage(channel, "This is a chat bot lovingly titled \"BoxOfBagelBot\". " +
                    "Created by JohnStar128, written in Java using an implementation of PircBot. " +
                    "Source code: https://github.com/johnstar128/boxofbagelbot");
            return;
        }

        // Add commands to the config
        if (msgSplit[0].startsWith(prefix + "addcommand")) {
            if (!(modList.contains(sender))) {
                sendMessage(channel, "This command is only available to moderators or the broadcaster.");
                return;
            }
            StringJoiner joiner = new StringJoiner(" ");
            String name;
            String args;

            // Error if incorrect syntax
            if (msgSplit.length <= 2) {
                sendMessage(channel, "Invalid usage. " + prefix + "addcommand <name> <returns>");
                return;
            }

            for (Command s : cfgMgn.cmdList.getCommands()) {
                String cmdName = prefix + s.getName();
                if (cmdName.equalsIgnoreCase(msgSplit[1])) {
                    sendMessage(channel, "Command " + msgSplit[1] + " already exists");
                    return;
                }
            }

            // Fill StringJoiner with the message
            // Ignoring the first two arguments
            for (String s : msgSplit) {
                if (s.equalsIgnoreCase(msgSplit[0]) || s.equalsIgnoreCase(msgSplit[1])) {
                    continue;
                }
                joiner.add(s);
            }
            // Set name and return args for command
            if (msgSplit[1].startsWith(prefix)) {
               name = msgSplit[1].substring(1);
            } else {
                name = msgSplit[1];
            }
            args = joiner.toString();

            // Write command to Yaml
            cfgMgn.writeCommand(name, args);

            // Done
            sendMessage(channel, "Command " + name + " added");
        }

        // List available commands
        if (msg.startsWith(prefix + "commands")) {
            StringJoiner joiner = new StringJoiner(", ");

            for (Command s : cfgMgn.cmdList.getCommands()) {
                joiner.add(prefix + s.getName());
            }

            if (cfgMgn.cmdList.getCommands().isEmpty()) {
                sendMessage(channel, "The bot commands are: " +
                        prefix + "about, " + prefix + "addcommand, " +
                        prefix + "removecommand, and " +
                        prefix + "commands. There are no custom commands available. Add some with !addcommand");
                return;
            }

            sendMessage(channel, "The bot commands are: " +
                    prefix + "about, " + prefix + "addcommand, " +
                    prefix + "removecommand, and " +
                    prefix + "commands. Available custom commands are: " + joiner);
        }

        // Removing a command from yaml
        if (msgSplit[0].startsWith(prefix + "removecommand")) {
            if (!(modList.contains(sender))) {
                sendMessage(channel, "This command is only available to moderators or the broadcaster.");
                return;
            }

            if (msgSplit.length < 2) {
                sendMessage(channel, "Invalid usage: " + prefix + "removecommand <command name>");
                return;
            }

            for (Command s : cfgMgn.cmdList.getCommands()) {
                String cmdName = prefix + s.getName();
                if (cmdName.equalsIgnoreCase(msgSplit[1])) {
                    cfgMgn.cmdList.getCommands().remove(s);
                    cfgMgn.updateCommands();
                    sendMessage(channel, "Command " + cmdName + " removed");
                }
            }
        }

        // Edit the arguments of an existing command
        if (msgSplit[0].startsWith(prefix + "editcommand")) {
            if (!(modList.contains(sender))) {
                sendMessage(channel, "This command is only available to moderators or the broadcaster.");
                return;
            }

            if (msgSplit.length < 3) {
                sendMessage(channel, "Invalid usage: " + prefix + "editcommand <command name> <new output>");
                return;
            }
            String cmdName = prefix + msgSplit[1].substring(1);
            StringJoiner joiner = new StringJoiner(" ");

            for (String s : msgSplit) {
                if (s.equalsIgnoreCase(msgSplit[0]) || s.equalsIgnoreCase(msgSplit[1])) {
                    continue;
                }
                joiner.add(s);
            }

            for (Command s : cfgMgn.cmdList.getCommands()) {
                if (s.getName().equalsIgnoreCase(msgSplit[1].substring(1))) {
                    s.setArgs(joiner.toString());
                }
            }
            cfgMgn.updateCommands();
            sendMessage(channel, "Command " + cmdName + " updated");
        }

        // Reading and executing custom commands
        if (!cfgMgn.cmdList.getCommands().isEmpty() && msgSplit[0].startsWith(prefix)) {
            for (Command s : cfgMgn.cmdList.getCommands()) {
                if (msgSplit[0].equalsIgnoreCase(prefix + s.getName())) {
                    sendMessage(channel, placeholder.parsePlaceholders(s.getArgs()));
                }
            }
        }
    }
}
