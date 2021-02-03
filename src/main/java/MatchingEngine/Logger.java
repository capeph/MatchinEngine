package MatchingEngine;

// Silly simple log wrapper over System.out
public class Logger {

    private String name;

    public Logger(String name) {
        this.name = name;
    }

    public void info(String text) {
        System.out.println(name + " INFO: " + text);
    }

    public void error(String text) {
        System.out.println(name + " ERROR: " + text);
    }

    public void warn(String text) {
        System.out.println(name + "WARNING: " + text);
    }




}
