package me.johnstar128.boxofbagelbot.commands;

public class Command {
    private String name;
    private String args;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }
}
