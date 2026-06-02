package com.pairprogramming;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public class SessionManagerTest {

    @Test
    public void saveSession_appendsCsvLine() throws Exception {
        Path tmp = Files.createTempFile("sessions_test", ".csv");
        try {
            SessionManager sm = new SessionManager(tmp);
            Session s = new Session(LocalDateTime.now().minusSeconds(5));
            s.end(LocalDateTime.now(), 2, "prueba de observación");
            sm.saveSession(s);

            List<String> lines = Files.readAllLines(tmp);
            assertTrue(lines.size() >= 2, "Debe existir cabecera y al menos una línea de sesión");
            assertTrue(lines.get(1).contains("prueba de observación"));
        } finally {
            Files.deleteIfExists(tmp);
        }
    }
}
