# Pair Programming App — JavaFX

## Requisitos
- JDK 17 o superior (ya tienes JDK 21 ✓)
- Maven (si no lo tienes, ver abajo)

---

## Instalar Maven en Windows (si no lo tienes)

1. Descarga Maven: https://maven.apache.org/download.cgi  
   → elige `apache-maven-3.9.x-bin.zip`
2. Descomprime en `C:\maven`
3. Agrega `C:\maven\bin` al PATH del sistema
4. Verifica: `mvn -version`

---

## Estructura del proyecto

```
PairProgramming/
├── pom.xml
└── src/
    └── main/
        └── java/
            └── com/
                └── pairprogramming/
                    └── PairProgrammingApp.java
```

---

## Correr la app

Abre una terminal en la carpeta `PairProgramming/` y ejecuta:

```bash
mvn javafx:run
```

Maven descarga JavaFX automáticamente la primera vez (~30 segundos).

---

## Desde VS Code

1. Instala extensión **Extension Pack for Java**
2. Instala extensión **Maven for Java**
3. Abre la carpeta `PairProgramming/`
4. Terminal → `mvn javafx:run`

---

## Funcionalidades

- Configura nombre del Driver y Navigator
- Botón Iniciar sesión → habilita el editor de código
- Solo el Driver puede escribir
- Botón Cambiar roles → swap animado de Driver/Navigator
- Timer de sesión en tiempo real
- Contador de rotaciones
- Log con timestamps de todos los eventos
