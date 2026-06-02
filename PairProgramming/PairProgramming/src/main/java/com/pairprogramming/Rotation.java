package com.pairprogramming;

import java.time.LocalDateTime;

public class Rotation {
    private final int number;
    private final Developer from;
    private final Developer to;
    private final LocalDateTime timestamp;

    public Rotation(int number, Developer from, Developer to) {
        this.number = number;
        this.from = from;
        this.to = to;
        this.timestamp = LocalDateTime.now();
    }

    public int getNumber() {
        return number;
    }

    public Developer getFrom() {
        return from;
    }

    public Developer getTo() {
        return to;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Rotación #" + number + ": " + from.getName() + " → " + to.getName();
    }
}
