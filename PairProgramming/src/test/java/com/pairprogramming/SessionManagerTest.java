package com.pairprogramming;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class SessionManagerTest {

    @Test
    public void startSessionReturnsNonNull() throws IOException {
        SessionManager manager = new SessionManager(Files.createTempDirectory("t1"));
        Session s = manager.startSession();
        assertNotNull(s);
        assertNotNull(s.getStart());
    }

    @Test
    public void sessionDurationIsCalculatedCorrectly() {
        LocalDateTime start = LocalDateTime.now();
        Session s = new Session(start);
        LocalDateTime end = start.plusSeconds(60);
        s.end(end, 1, "obs");
        assertEquals(60, s.getDurationSeconds());
    }

    @Test
    public void newSessionStartsWithZeroRotations() {
        Session s = new Session(LocalDateTime.now());
        assertEquals(0, s.getRotations());
    }

    @Test
    public void endSessionSetsEndNotNull() {
        Session s = new Session(LocalDateTime.now());
        s.end(LocalDateTime.now().plusSeconds(10), 0, "ok");
        assertNotNull(s.getEnd());
    }

    @Test
    public void toCsvLineContainsFormattedStart() {
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 12, 0, 0);
        Session s = new Session(start);
        String csv = s.toCsvLine();
        assertTrue(csv.startsWith("2025-01-01 12:00:00,"));
    }

    @Test
    public void saveSessionCreatesStorageFile() throws IOException {
        Path tmp = Files.createTempDirectory("t6");
        Path storage = tmp.resolve("sessions.csv");
        SessionManager manager = new SessionManager(storage);
        Session s = new Session(LocalDateTime.of(2025, 2, 2, 8, 0, 0));
        s.end(LocalDateTime.of(2025, 2, 2, 8, 5, 0), 0, "ok");
        manager.saveSession(s);
        assertTrue(Files.exists(storage));
        assertTrue(Files.readAllLines(storage).size() >= 2);
    }

    @Test
    public void toCsvLineEscapesCommaInObservations() {
        Session s = new Session(LocalDateTime.now());
        s.end(LocalDateTime.now().plusSeconds(5), 0, "hola, mundo");
        assertFalse(s.toCsvLine().contains("hola, mundo"));
    }

    @Test
    public void endBeforeStartProducesNegativeDuration() {
        LocalDateTime start = LocalDateTime.now();
        Session s = new Session(start);
        s.end(start.minusSeconds(5), 0, "backwards");
        assertTrue(s.getDurationSeconds() < 0);
    }

    @Test
    public void saveSessionNullThrowsNpe() throws IOException {
        SessionManager manager = new SessionManager(Files.createTempDirectory("t9"));
        assertThrows(NullPointerException.class, () -> manager.saveSession(null));
    }

    @Test
    public void startSessionTimestampCloseToNow() throws IOException {
        SessionManager manager = new SessionManager(Files.createTempDirectory("t10"));
        LocalDateTime before = LocalDateTime.now();
        Session s = manager.startSession();
        LocalDateTime after = LocalDateTime.now();
        Duration d1 = Duration.between(before, s.getStart()).abs();
        Duration d2 = Duration.between(s.getStart(), after).abs();
        assertTrue(d1.toMillis() < 2000 || d2.toMillis() < 2000);
    }
}


