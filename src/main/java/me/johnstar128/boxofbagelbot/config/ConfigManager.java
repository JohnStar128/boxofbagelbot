package me.johnstar128.boxofbagelbot.config;

import me.johnstar128.boxofbagelbot.BoxOfBagelBot;
import me.johnstar128.boxofbagelbot.commands.Command;
import me.johnstar128.boxofbagelbot.commands.CommandsSection;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

public class ConfigManager {
    private final String userDir = System.getProperty("user.dir");
    public CommandsSection cmdList;

    public void generateConfigs() {
        InputStream cmdIn = this.getClass().getClassLoader().getResourceAsStream("commands.yml");
        InputStream cfgIn = this.getClass().getClassLoader().getResourceAsStream("config.yml");
        OutputStream cmdOut = null;
        OutputStream cfgOut = null;
        File cmdDest = new File(userDir + "/commands.yml");
        File cfgDest = new File(userDir + "/config.yml");

        if (!(cmdDest.exists())) {
            System.out.println("Commands file doesn't exist! Generating a new one...");
            try {
                cmdOut = new BufferedOutputStream(new FileOutputStream(cmdDest));

                byte[] readBytes = new byte[1024];
                int read;

                if (cmdIn != null) {
                    while ((read = cmdIn.read(readBytes)) > 0) {
                        cmdOut.write(readBytes, 0, read);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    cmdIn.close();
                    cmdOut.close();
                    System.out.println("Command file generated");
                } catch (NullPointerException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (!(cfgDest.exists())) {
            System.out.println("Config file doesn't exist! Generating a new one...");
            try {
                cfgOut = new BufferedOutputStream(new FileOutputStream(cfgDest));

                byte[] readBytes = new byte[1024];
                int read;

                if (cfgIn != null) {
                    while ((read = cfgIn.read(readBytes)) > 0) {
                        cfgOut.write(readBytes, 0, read);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally  {
                try {
                    cfgIn.close();
                    cfgOut.close();
                    System.out.println("Config file generated");
                } catch (NullPointerException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void readConfigs() {
        InputStream cmdIn = null;
        InputStream cfgIn = null;
        try {
            cmdIn = new FileInputStream(userDir + "/commands.yml");
            cfgIn = new FileInputStream(userDir + "/config.yml");
        } catch (FileNotFoundException e ) {
            e.printStackTrace();
        }
        Yaml cmdYml = new Yaml(new Constructor(CommandsSection.class));
        Yaml cfgYml = new Yaml(new Constructor(Config.class));
        Config cfgData = cfgYml.load(cfgIn);
        BoxOfBagelBot.channel = cfgData.getChannel();
        BoxOfBagelBot.oAuth = cfgData.getOauth();
        BoxOfBagelBot.prefix = cfgData.getPrefix();
        cmdList = cmdYml.load(cmdIn);
    }

    public void writeCommand(String name, String args) {
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(userDir + "/commands.yml");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        DumperOptions options = new DumperOptions();
        options.setIndent(2);
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(options);
        Command cmd = new Command();
        cmd.setName(name);
        cmd.setArgs(args);

        cmdList.getCommands().add(cmd);

        yaml.dump(cmdList, writer);
    }

    public void updateCommands() {
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(userDir + "/commands.yml");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        DumperOptions options = new DumperOptions();
        options.setIndent(2);
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(options);

        yaml.dump(cmdList, writer);
    }
}
