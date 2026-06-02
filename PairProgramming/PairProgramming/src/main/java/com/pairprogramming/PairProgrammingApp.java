package com.pairprogramming;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.animation.*;
import javafx.util.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.nio.file.Paths;
import java.nio.file.Path;

public class PairProgrammingApp extends Application {

    
    private Developer driver;
    private Developer navigator;
    private boolean rotacionEnCurso = false;
    private int rotaciones = 0;
    private int sesionSegundos = 0;
    private Timeline timerTimeline;

    
    private Label    lblDriver, lblRolDriver;
    private Label    lblNavigator, lblRolNavigator;
    private Label    lblQuienEscribe;
    private TextArea codigoArea;
    private TextArea logArea;
    private Button   btnRotar, btnIniciar, btnDetener;
    private Button   btnPausar;
    private Label    lblTimer, lblRotaciones, lblUltimaDuracion;
    private TextField txtDriverNombre, txtNavigatorNombre, txtAutorNombre;
    private TextArea txtObservaciones;
    private TextArea suggestionsArea;

    private SessionManager sessionManager;
    private com.pairprogramming.Session currentSession;
    private boolean paused = false;

    @Override
    public void start(Stage stage) {
        driver = new Developer("Desarrollador A", Role.DRIVER);
        navigator = new Developer("Desarrollador B", Role.NAVIGATOR);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1A1A1A;");

        root.setTop(crearTopBar());
        SplitPane split = new SplitPane();
        split.setStyle("-fx-background-color: #1A1A1A; -fx-box-border: transparent;");
        split.setDividerPositions(0.36);
        split.getItems().addAll(crearPanelRoles(), crearPanelCodigo());
        root.setCenter(split);
        root.setBottom(crearPanelLog());

        Scene scene = new Scene(root, 1020, 700);
        stage.setTitle("Pair Programming — Sistema de Sesiones");
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(560);
        stage.show();
        sessionManager = new SessionManager(Paths.get("sessions.csv"));
        txtAutorNombre.setText(driver.getName());
        actualizarCardsRol();
        actualizarPermisoEdicion();
        registrarLog("Sistema listo. Configura los nombres y presiona Iniciar sesión.");
    }

  
    private HBox crearTopBar() {
        HBox bar = new HBox(14);
        bar.setPadding(new Insets(11, 20, 11, 20));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: #2B2B2B; -fx-border-color: #3D3D3D; -fx-border-width: 0 0 1 0;");

        Label titulo = new Label("Pair Programming");
        titulo.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 16px; -fx-font-weight: bold; -fx-font-family: monospace;");

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        lblRotaciones = new Label("Rotaciones: 0");
        lblRotaciones.setStyle("-fx-text-fill: #9F9F9F; -fx-font-size: 13px;");

        lblTimer = new Label("00:00:00");
        lblTimer.setStyle("-fx-text-fill: #9F9F9F; -fx-font-family: monospace; -fx-font-size: 15px;");

        lblUltimaDuracion = new Label("");
        lblUltimaDuracion.setStyle("-fx-text-fill: #9F9F9F; -fx-font-size: 13px; -fx-font-style: italic;");

        txtAutorNombre = new TextField();
        txtAutorNombre.setPrefWidth(140);
        txtAutorNombre.setPromptText("Tu nombre para escribir");
        txtAutorNombre.textProperty().addListener((obs, oldText, newText) -> actualizarPermisoEdicion());

        btnIniciar = new Button("▶  Iniciar sesión");
        btnPausar  = new Button("⏸  Pausar");
        btnPausar.setDisable(true);
        btnDetener = new Button("⏹  Detener");
        btnDetener.setDisable(true);

        estilizarBoton(btnIniciar, "#534AB7", "#FFFFFF");
        estilizarBoton(btnDetener, "#3D3D3D", "#9F9F9F");

        btnIniciar.setOnAction(e -> iniciarSesion());
        btnPausar.setOnAction(e -> pausarSesion());
        btnDetener.setOnAction(e -> detenerSesion());

        Label lblAutor = new Label("Autor:");
        lblAutor.setStyle("-fx-text-fill: #9F9F9F; -fx-font-size: 13px;");

        bar.getChildren().addAll(titulo, sp, lblRotaciones, lblTimer, lblUltimaDuracion, lblAutor, txtAutorNombre, btnIniciar, btnPausar, btnDetener);
        return bar;
    }

  
    private VBox crearPanelRoles() {
        VBox panel = new VBox(14);
        panel.setPadding(new Insets(18));
        panel.setStyle("-fx-background-color: #242424;");

        Label lblConfig = new Label("PARTICIPANTES");
        lblConfig.setStyle("-fx-text-fill: #5F5E5A; -fx-font-size: 11px; -fx-font-weight: bold;");

        txtDriverNombre = new TextField(driver.getName());
        txtNavigatorNombre = new TextField(navigator.getName());
        estilizarTextField(txtDriverNombre, "Nombre del Driver");
        estilizarTextField(txtNavigatorNombre, "Nombre del Navigator");

        Button btnGuardar = new Button("Guardar nombres");
        estilizarBoton(btnGuardar, "#2B2B2B", "#9F9F9F");
        btnGuardar.setMaxWidth(Double.MAX_VALUE);
        btnGuardar.setOnAction(e -> {
            guardarNombres();
            registrarLog("Nombres actualizados → Driver: " + driver.getName() + "  |  Navigator: " + navigator.getName());
        });

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #3D3D3D;");

      
        VBox cardDriver = crearCard("DRIVER  👨‍💻", "#534AB7", "#D9D2E9");
        lblDriver    = (Label) ((VBox) cardDriver.getChildren().get(1)).getChildren().get(0);
        lblRolDriver = (Label) ((VBox) cardDriver.getChildren().get(1)).getChildren().get(1);

     
        VBox cardNavigator = crearCard("NAVIGATOR  🧠", "#0F6E56", "#D0EBDA");
        lblNavigator    = (Label) ((VBox) cardNavigator.getChildren().get(1)).getChildren().get(0);
        lblRolNavigator = (Label) ((VBox) cardNavigator.getChildren().get(1)).getChildren().get(1);

       
        lblQuienEscribe = new Label("Inicia la sesión para comenzar");
        lblQuienEscribe.setWrapText(true);
        lblQuienEscribe.setMaxWidth(Double.MAX_VALUE);
        lblQuienEscribe.setStyle(
            "-fx-text-fill: #E8E8E8; -fx-font-size: 13px; "
            + "-fx-background-color: #2B2B2B; -fx-padding: 10 14 10 14; "
            + "-fx-background-radius: 8;");

        btnRotar = new Button("⇄   Cambiar roles");
        btnRotar.setMaxWidth(Double.MAX_VALUE);
        btnRotar.setPrefHeight(42);
        estilizarBoton(btnRotar, "#993C1D", "#FFFFFF");
        btnRotar.setDisable(true);
        btnRotar.setOnAction(e -> rotarRoles());

   
        VBox info = new VBox(5);
        info.setStyle("-fx-background-color: #2B2B2B; -fx-padding: 10; -fx-background-radius: 8;");
        info.getChildren().addAll(
            infoLabel("Driver:", "escribe el código activamente"),
            infoLabel("Navigator:", "guía y revisa sin teclear")
        );

        panel.getChildren().addAll(
            lblConfig,
            txtDriverNombre, txtNavigatorNombre, btnGuardar,
            sep,
            cardDriver, cardNavigator,
            lblQuienEscribe, btnRotar,
            info
        );
        return panel;
    }

    private VBox crearCard(String rolLabel, String colorBorde, String colorTexto) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(12));
        card.setStyle(
            "-fx-background-color: #2B2B2B; -fx-background-radius: 10; "
            + "-fx-border-color: " + colorBorde + "; "
            + "-fx-border-width: 0 0 0 3; -fx-border-radius: 0;");

        Label etiqueta = new Label(rolLabel);
        etiqueta.setStyle("-fx-text-fill: " + colorTexto + "; -fx-font-size: 11px; -fx-font-weight: bold;");

        VBox textos = new VBox(2);
        Label nombre = new Label("—");
        nombre.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 15px; -fx-font-weight: bold;");
        Label desc = new Label("sin asignar");
        desc.setStyle("-fx-text-fill: #9F9F9F; -fx-font-size: 12px;");
        textos.getChildren().addAll(nombre, desc);

        card.getChildren().addAll(etiqueta, textos);
        return card;
    }

    private Label infoLabel(String titulo, String desc) {
        Label l = new Label(titulo + " " + desc);
        l.setStyle("-fx-text-fill: #9F9F9F; -fx-font-size: 11px;");
        l.setWrapText(true);
        return l;
    }

  
    private VBox crearPanelCodigo() {
        VBox panel = new VBox(0);
        panel.setStyle("-fx-background-color: #1E1E1E;");

        HBox barEditor = new HBox(12);
        barEditor.setPadding(new Insets(9, 16, 9, 16));
        barEditor.setAlignment(Pos.CENTER_LEFT);
        barEditor.setStyle("-fx-background-color: #2D2D2D; -fx-border-color: #3D3D3D; -fx-border-width: 0 0 1 0;");

        Label lblEditor = new Label("Main.java");
        lblEditor.setStyle("-fx-text-fill: #CCCCCC; -fx-font-family: monospace; -fx-font-size: 13px;");

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Button btnLimpiar = new Button("Limpiar");
        estilizarBoton(btnLimpiar, "#2B2B2B", "#9F9F9F");
        btnLimpiar.setOnAction(e -> codigoArea.clear());

        barEditor.getChildren().addAll(lblEditor, sp, btnLimpiar);

        codigoArea = new TextArea();
        codigoArea.setPromptText("// El Driver escribe aquí...\n// El Navigator observa y guía sin tocar el teclado");
        codigoArea.setEditable(false);
        codigoArea.setWrapText(false);
        codigoArea.setStyle(
            "-fx-control-inner-background: #1E1E1E; "
            + "-fx-text-fill: #D4D4D4; "
            + "-fx-font-family: 'Courier New', monospace; "
            + "-fx-font-size: 14px; "
            + "-fx-border-color: transparent; "
            + "-fx-background-color: #1E1E1E;");
        VBox.setVgrow(codigoArea, Priority.ALWAYS);

        // Sugerencias del Navigator (no toma control del editor)
        Label lblSugerencias = new Label("Sugerencias del Navigator");
        lblSugerencias.setStyle("-fx-text-fill: #9F9F9F; -fx-font-size: 11px;");
        suggestionsArea = new TextArea();
        suggestionsArea.setPromptText("El Navigator puede escribir sugerencias aquí sin cambiar el código...");
        suggestionsArea.setPrefRowCount(3);
        Button btnRegistrarSugerencia = new Button("Registrar sugerencia");
        estilizarBoton(btnRegistrarSugerencia, "#2B2B2B", "#9F9F9F");
        btnRegistrarSugerencia.setOnAction(e -> {
            String s = suggestionsArea.getText().trim();
            if (!s.isEmpty()) {
                registrarLog("SUGERENCIA: " + s);
                suggestionsArea.clear();
            }
        });

        HBox sugerenciasBar = new HBox(8, suggestionsArea, btnRegistrarSugerencia);
        HBox.setHgrow(suggestionsArea, Priority.ALWAYS);

        panel.getChildren().addAll(barEditor, codigoArea, lblSugerencias, sugerenciasBar);
        return panel;
    }


    private VBox crearPanelLog() {
        VBox panel = new VBox(0);
        panel.setMaxHeight(120);
        panel.setMinHeight(90);

        HBox barLog = new HBox();
        barLog.setPadding(new Insets(6, 16, 6, 16));
        barLog.setAlignment(Pos.CENTER_LEFT);
        barLog.setStyle("-fx-background-color: #2B2B2B; -fx-border-color: #3D3D3D; -fx-border-width: 1 0 0 0;");

        Label lblTitulo = new Label("REGISTRO DE SESIÓN");
        lblTitulo.setStyle("-fx-text-fill: #5F5E5A; -fx-font-size: 11px; -fx-font-weight: bold;");

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Button btnLimpiarLog = new Button("Limpiar");
        estilizarBoton(btnLimpiarLog, "#2B2B2B", "#9F9F9F");
        btnLimpiarLog.setOnAction(e -> logArea.clear());
        barLog.getChildren().addAll(lblTitulo, sp, btnLimpiarLog);

        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setStyle(
            "-fx-control-inner-background: #1A1A1A; "
            + "-fx-text-fill: #6A9955; "
            + "-fx-font-family: 'Courier New', monospace; "
            + "-fx-font-size: 12px; "
            + "-fx-border-color: transparent;");
        VBox.setVgrow(logArea, Priority.ALWAYS);

        panel.getChildren().addAll(barLog, logArea);
        // Observaciones / comentarios que se guardarán al detener la sesión
        HBox obsBar = new HBox(8);
        txtObservaciones = new TextArea();
        txtObservaciones.setPromptText("Observaciones o comentarios (se guardarán al detener la sesión)");
        txtObservaciones.setPrefRowCount(2);
        Button btnAgregarObs = new Button("Agregar observación");
        estilizarBoton(btnAgregarObs, "#2B2B2B", "#9F9F9F");
        btnAgregarObs.setOnAction(e -> registrarObservacion());
        HBox.setHgrow(txtObservaciones, Priority.ALWAYS);
        obsBar.getChildren().addAll(txtObservaciones, btnAgregarObs);
        panel.getChildren().add(obsBar);
        return panel;
    }

   
    private void guardarNombres() {
        String nd = txtDriverNombre.getText().trim();
        String nn = txtNavigatorNombre.getText().trim();
        if (!nd.isEmpty()) driver.setName(nd);
        if (!nn.isEmpty()) navigator.setName(nn);
        actualizarCardsRol();
        actualizarPermisoEdicion();
    }

    private void iniciarSesion() {
        sesionSegundos = 0;
        rotaciones = 0;
        lblRotaciones.setText("Rotaciones: 0");
        guardarNombres();
        codigoArea.setEditable(true);
        btnRotar.setDisable(false);
        btnIniciar.setDisable(true);
        btnDetener.setDisable(false);
        estilizarBoton(btnDetener, "#993C1D", "#FFFFFF");
        lblTimer.setStyle("-fx-text-fill: #D4D4D4; -fx-font-family: monospace; -fx-font-size: 15px;");

        timerTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            sesionSegundos++;
            int h = sesionSegundos / 3600;
            int m = (sesionSegundos % 3600) / 60;
            int s = sesionSegundos % 60;
            lblTimer.setText(String.format("%02d:%02d:%02d", h, m, s));
        }));
        timerTimeline.setCycleCount(Timeline.INDEFINITE);
        timerTimeline.play();

        // iniciar registro de sesión
        currentSession = sessionManager.startSession(driver, navigator);
        btnPausar.setDisable(false);
        lblUltimaDuracion.setText("");

        actualizarCardsRol();
        registrarLog("▶  Sesión iniciada — Driver: " + driver.getName() + "  |  Navigator: " + navigator.getName());
    }

    private void pausarSesion() {
        if (timerTimeline == null) return;
        if (!paused) {
            timerTimeline.pause();
            codigoArea.setEditable(false);
            btnPausar.setText("▶  Reanudar");
            paused = true;
            registrarLog("⏸  Sesión en pausa");
        } else {
            timerTimeline.play();
            paused = false;
            btnPausar.setText("⏸  Pausar");
            actualizarPermisoEdicion();
            registrarLog("▶  Sesión reanudada");
        }
    }

    private void detenerSesion() {
        if (timerTimeline != null) timerTimeline.stop();
        codigoArea.setEditable(false);
        btnRotar.setDisable(true);
        btnIniciar.setDisable(false);
        btnDetener.setDisable(true);
        btnPausar.setDisable(true);
        estilizarBoton(btnDetener, "#3D3D3D", "#9F9F9F");
        lblTimer.setStyle("-fx-text-fill: #9F9F9F; -fx-font-family: monospace; -fx-font-size: 15px;");
        lblUltimaDuracion.setText("Última duración: " + lblTimer.getText());
        registrarLog("⏹  Sesión detenida — Duración: " + lblTimer.getText() + "  |  Rotaciones: " + rotaciones);

        // finalizar y persistir sesión
        if (currentSession != null) {
            currentSession.end(LocalDateTime.now(), rotaciones, txtObservaciones.getText());
            sessionManager.saveSession(currentSession);
            registrarLog("✔  Sesión guardada (sessions.csv)");
            currentSession = null;
        }
    }

    private void rotarRoles() {
        if (rotacionEnCurso) return;
        rotacionEnCurso = true;

        Developer previousDriver = driver;
        driver = navigator;
        navigator = previousDriver;
        driver.setRole(Role.DRIVER);
        navigator.setRole(Role.NAVIGATOR);
        rotaciones++;
        lblRotaciones.setText("Rotaciones: " + rotaciones);
        if (currentSession != null) {
            currentSession.addRotation(new Rotation(rotaciones, navigator, driver));
        }

        FadeTransition ft = new FadeTransition(Duration.millis(250), lblQuienEscribe);
        ft.setFromValue(1.0); ft.setToValue(0.1);
        ft.setOnFinished(e -> {
            actualizarCardsRol();
            FadeTransition ft2 = new FadeTransition(Duration.millis(250), lblQuienEscribe);
            ft2.setFromValue(0.1); ft2.setToValue(1.0);
            ft2.setOnFinished(ev -> rotacionEnCurso = false);
            ft2.play();
        });
        ft.play();

        registrarLog("⇄  Rotación #" + rotaciones + " — nuevo Driver: " + driver.getName() + "  |  Navigator: " + navigator.getName());
    }

    private void actualizarCardsRol() {
        if (lblDriver == null) return;
        lblDriver.setText(driver.getName());
        lblRolDriver.setText(driver.getRole().getDisplayName());
        lblNavigator.setText(navigator.getName());
        lblRolNavigator.setText(navigator.getRole().getDisplayName());

        boolean sesionActiva = (btnIniciar != null && btnIniciar.isDisabled());
        if (!sesionActiva) {
            lblQuienEscribe.setText("⏸  Inicia la sesión para comenzar");
        } else {
            actualizarPermisoEdicion();
        }
    }

    private void actualizarPermisoEdicion() {
        if (txtAutorNombre == null || lblQuienEscribe == null || codigoArea == null) return;

        String autor = txtAutorNombre.getText().trim();
        boolean sesionActiva = btnIniciar != null && btnIniciar.isDisabled();
        boolean autorizado = sesionActiva && !paused && driver.hasName(autor);

        codigoArea.setEditable(autorizado);
        if (!sesionActiva) {
            lblQuienEscribe.setText("⏸  Inicia la sesión para comenzar");
        } else if (autor.isBlank()) {
            lblQuienEscribe.setText("⚠ Ingresa tu nombre para escribir");
        } else if (driver.hasName(autor)) {
            lblQuienEscribe.setText("✏  Driver activo: " + driver.getName());
        } else if (navigator.hasName(autor)) {
            lblQuienEscribe.setText("⚠ Eres navigator: solo el driver puede escribir");
        } else {
            lblQuienEscribe.setText("⚠ Nombre no autorizado para escribir");
        }
    }

    private void registrarLog(String msg) {
        if (logArea == null) return;
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        logArea.appendText("[" + ts + "]  " + msg + "\n");
    }

    private void registrarObservacion() {
        String o = txtObservaciones.getText().trim();
        if (o.isEmpty()) return;
        registrarLog("OBSERVACIÓN: " + o);
        // no limpiar automáticamente para que usuario pueda seguir editando
    }

 
    private void estilizarBoton(Button btn, String bg, String fg) {
        btn.setStyle(
            "-fx-background-color: " + bg + "; "
            + "-fx-text-fill: " + fg + "; "
            + "-fx-font-size: 13px; "
            + "-fx-cursor: hand; "
            + "-fx-background-radius: 6; "
            + "-fx-padding: 7 14 7 14;");
        btn.setOnMouseEntered(e -> btn.setOpacity(0.82));
        btn.setOnMouseExited(e  -> btn.setOpacity(1.0));
    }

    private void estilizarTextField(TextField tf, String prompt) {
        tf.setPromptText(prompt);
        tf.setMaxWidth(Double.MAX_VALUE);
        tf.setStyle(
            "-fx-background-color: #2B2B2B; "
            + "-fx-text-fill: #FFFFFF; "
            + "-fx-prompt-text-fill: #5F5E5A; "
            + "-fx-border-color: #3D3D3D; "
            + "-fx-border-radius: 6; "
            + "-fx-background-radius: 6; "
            + "-fx-font-size: 13px; "
            + "-fx-padding: 7 10 7 10;");
    }

    public static void main(String[] args) { launch(args); }
}
