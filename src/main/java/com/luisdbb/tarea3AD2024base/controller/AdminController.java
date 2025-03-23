package com.luisdbb.tarea3AD2024base.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import com.luisdbb.tarea3AD2024base.config.SpringFXMLLoader;
import com.luisdbb.tarea3AD2024base.config.StageManager;
import com.luisdbb.tarea3AD2024base.services.AyudaService;
import com.luisdbb.tarea3AD2024base.view.FxmlView;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

@Controller
public class AdminController {

	@FXML
	private Button crearParadaButton;

	@FXML
	private Button cerrarSesionButton;

	@FXML
	private Button ayudaButton;

	@FXML
	private ImageView crearParadaIcon;

	@FXML
	private ImageView cerrarSesionIcon;

	@FXML
	private ImageView ayudaIcon;

	@Lazy
	@Autowired
	private StageManager stageManager;

	@Autowired
	private AyudaService ayudaService;
	
	/**
	 * Inicializa la vista del administrador.
	 * Configura iconos, acciones de botones y accesos rápidos del teclado.
	 */
	@FXML
	public void initialize() {
		crearParadaIcon.setImage(new Image(getClass().getResourceAsStream("/images/parada.png")));
		cerrarSesionIcon.setImage(new Image(getClass().getResourceAsStream("/images/cerrarSesion.png")));
		ayudaIcon.setImage(new Image(getClass().getResourceAsStream("/images/help.png")));

		crearParadaButton.setOnAction(event -> crearParada());
		cerrarSesionButton.setOnAction(event -> cerrarSesion());
		ayudaButton.setOnAction(event -> ayudaService.mostrarAyuda("/help/Admin.html"));

		configurarAtajo();
	}
	
	/**
	 * Configura el atajo de teclado F1 para mostrar la ayuda de la vista de administrador.
	 */

	private void configurarAtajo() {
		ayudaButton.sceneProperty().addListener((observable, oldScene, newScene) -> {
			if (newScene != null) {
				newScene.setOnKeyPressed(event -> {
					if (event.getCode() == KeyCode.F1) {
						event.consume();
						ayudaService.mostrarAyuda("/help/Admin.html");
					}
				});
			}
		});
	}
	

	/**
	 * Redirige a la vista para crear una nueva parada.
	 */

	private void crearParada() {
		stageManager.switchScene(FxmlView.CREARPARADA);
	}
	
	/**
	 * Cierra la sesión del administrador y vuelve a la vista principal.
	 */
	private void cerrarSesion() {
		stageManager.switchScene(FxmlView.PRINCIPAL);
	}

}
