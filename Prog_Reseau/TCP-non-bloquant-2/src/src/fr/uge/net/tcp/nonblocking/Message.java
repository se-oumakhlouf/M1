package fr.uge.net.tcp.nonblocking;

public record Message(String login, String text) {
    @Override
    public String toString() {
        return login + ": " + text;
    }
}