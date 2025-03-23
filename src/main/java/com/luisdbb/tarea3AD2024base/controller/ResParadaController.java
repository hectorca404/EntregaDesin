package com.luisdbb.tarea3AD2024base.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import com.luisdbb.tarea3AD2024base.config.SpringFXMLLoader;
import com.luisdbb.tarea3AD2024base.config.StageManager;
import com.luisdbb.tarea3AD2024base.services.AyudaService;
import com.luisdbb.tarea3AD2024base.view.FxmlView;


/**
 * Controlador de la vista del responsable de parada.
 * Permite acceder a las funcionalidades de sellado/alojamiento,
 * exportación de datos, cierre de sesión y ayuda contextual.
 */
@Controller
public class ResParadaController {

	@FXML
	private Button exportarDatosButton;

	@FXML
	private Button sellarAlojarButton;

	@FXML
	private Button cerrarSesionButton;

	@FXML
	private Button ayudaButton;

	@FXML
	private ImageView exportarIcon;

	@FXML
	private ImageView sellarIcon;

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
     * Inicializa la vista, configurando iconos, eventos de botones
     * y accesos directos por teclado.
     */
	@FXML
	public void initialize() {
		exportarIcon.setImage(new Image(getClass().getResourceAsStream("/images/exportar.png")));
		sellarIcon.setImage(new Image(getClass().getResourceAsStream("/images/sellar.png")));
		cerrarSesionIcon.setImage(new Image(getClass().getResourceAsStream("/images/cerrarSesion.png")));
		ayudaIcon.setImage(new Image(getClass().getResourceAsStream("/images/help.png")));

		cerrarSesionButton.setOnAction(event -> volverLogin());
		sellarAlojarButton.setOnAction(event -> sellarAlojar());
		exportarDatosButton.setOnAction(event -> exportarDatos());
		ayudaButton.setOnAction(event -> ayudaService.mostrarAyuda("/help/ResParada.html"));

		configurarAtajo();

	}
	
	/**
     * Configura el atajo de teclado F1 para mostrar la ayuda contextual.
     */

	private void configurarAtajo() {
		ayudaButton.sceneProperty().addListener((observable, oldScene, newScene) -> {
			if (oldScene != null) {
				oldScene.setOnKeyPressed(null);
			}
			if (newScene != null) {
				newScene.setOnKeyPressed(event -> {
					if (event.getCode() == KeyCode.F1) {
						event.consume();
						ayudaService.mostrarAyuda("/help/ResParada.html");
					}
				});
			}
		});
	}
	
	 /**
     * Cambia la escena a la vista principal para cerrar la sesión actual.
     */

	private void volverLogin() {
		stageManager.switchScene(FxmlView.PRINCIPAL);
	}
	
	/**
     * Cambia la escena a la vista de sellado y alojamiento de peregrinos.
     */
	private void sellarAlojar() {
		stageManager.switchScene(FxmlView.SELLARALOJAR);
	}
	
	 /**
     * Cambia la escena a la vista de exportación de datos de la parada.
     */
	private void exportarDatos() {
		stageManager.switchScene(FxmlView.EXPORTPARADA);
	}

}
