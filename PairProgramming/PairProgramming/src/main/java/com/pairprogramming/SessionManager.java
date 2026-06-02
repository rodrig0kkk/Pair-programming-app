package com.pairprogramming;

import java.nio.file.*;
import java.io.*;
import java.time.LocalDateTime;
import java.util.List;

public class SessionManager {
    private final Path storage;

    public SessionManager(Path storage) {
        this.storage = storage == null ? Paths.get("sessions.csv") : storage;
        try {
            if (Files.notExists(this.storage)) {
                if (this.storage.getParent() != null) Files.createDirectories(this.storage.getParent());
                Files.write(this.storage, List.of("start,end,duration_seconds,rotations,observations"), StandardOpenOption.CREATE_NEW);
            }
        } catch (IOException e) {
            System.err.println("No se pudo inicializar almacenamiento de sesiones: " + e.getMessage());
        }
    }

    public Session startSession() {
        return new Session(LocalDateTime.now());
    }

    public Session startSession(Developer driver, Developer navigator) {
        return new Session(LocalDateTime.now(), driver, navigator);
    }

    public void saveSession(Session s) {
        try {
            String line = s.toCsvLine();
            Files.write(this.storage, List.of(line), StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("No se pudo guardar la sesión: " + e.getMessage());
        }
    }
}
