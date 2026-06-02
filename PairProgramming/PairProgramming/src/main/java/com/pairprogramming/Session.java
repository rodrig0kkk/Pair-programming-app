package com.pairprogramming;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Session {
    private LocalDateTime start;
    private LocalDateTime end;
    private long durationSeconds;
    private int rotations;
    private String observations;
    private Developer driver;
    private Developer navigator;
    private final List<Rotation> rotationHistory = new ArrayList<>();
    private final List<Task> tasks = new ArrayList<>();

    public Session(LocalDateTime start) {
        this(start, new Developer("Desconocido", Role.DRIVER), new Developer("Desconocido", Role.NAVIGATOR));
    }

    public Session(LocalDateTime start, Developer driver, Developer navigator) {
        this.start = start;
        this.driver = driver;
        this.navigator = navigator;
        this.rotations = 0;
        this.observations = "";
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public long getDurationSeconds() {
        return durationSeconds;
    }

    public int getRotations() {
        return rotations;
    }

    public String getObservations() {
        return observations;
    }

    public Developer getDriver() {
        return driver;
    }

    public Developer getNavigator() {
        return navigator;
    }

    public List<Rotation> getRotationHistory() {
        return new ArrayList<>(rotationHistory);
    }

    public List<Task> getTasks() {
        return new ArrayList<>(tasks);
    }

    public void setRotations(int rotations) {
        this.rotations = rotations;
    }

    public void addRotation(Rotation rotation) {
        if (rotation != null) {
            rotationHistory.add(rotation);
            rotations = rotation.getNumber();
        }
    }

    public void addTask(Task task) {
        if (task != null) {
            tasks.add(task);
        }
    }

    public void end(LocalDateTime end, int rotations, String observations) {
        this.end = end;
        this.durationSeconds = Duration.between(start, end).getSeconds();
        this.rotations = rotations;
        this.observations = observations == null ? "" : observations;
    }

    public String toCsvLine() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String s = start.format(f);
        String e = end != null ? end.format(f) : "";
        String obs = observations == null ? "" : observations.replaceAll("\r?\n", " ").replaceAll(",", " ");
        return String.format("%s,%s,%d,%d,%s", s, e, durationSeconds, rotations, obs);
    }
}
