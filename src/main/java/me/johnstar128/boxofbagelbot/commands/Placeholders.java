package me.johnstar128.boxofbagelbot.commands;

import me.johnstar128.boxofbagelbot.BoxOfBagelBot;
import org.apache.commons.text.StringSubstitutor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Placeholders {
    BoxOfBagelBot bot;
    Random random = new Random();

    public Placeholders(BoxOfBagelBot bot) {
        this.bot = bot;
    }

    public String parsePlaceholders(String command) {
        Map<String, String> data = new HashMap<>();

        data.put("coin", coinFlip());
        data.put("time", getTime());

        return StringSubstitutor.replace(command, data);
    }

    public String coinFlip() {
        String coinResult;

        if (random.nextBoolean()) {
            coinResult = "heads";
        } else {
            coinResult = "tails";
        }

        return coinResult;
    }

    public String getTime() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm 'on' MM/dd/yyyy");

        return sdf.format(date);
    }
}
