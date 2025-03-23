package com.luisdbb.tarea3AD2024base.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import com.luisdbb.tarea3AD2024base.config.StageManager;
import com.luisdbb.tarea3AD2024base.modelo.Peregrino;
import com.luisdbb.tarea3AD2024base.services.AyudaService;
import com.luisdbb.tarea3AD2024base.services.SesionService;
import com.luisdbb.tarea3AD2024base.view.FxmlView;


/**
 * Controlador de la vista principal del usuario con perfil de peregrino.
 * Permite al peregrino exportar sus datos, editar su información de contacto
 * y cerrar sesión. También ofrece acceso a la ayuda contextual.
 */
@Controller
public class PeregrinoController {

	@FXML
	private Button exportarButton;

	@FXML
	private Button editarContactoButton;

	@FXML
	private Button cerrarSesionButton;

	@FXML
	private Button ayudaButton;

	@FXML
	private ImageView exportarIcon;

	@FXML
	private ImageView editarContactoIcon;

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
     * Inicializa la vista del peregrino configurando íconos,
     * acciones de los botones y atajos de teclado.
     */
	@FXML
	public void initialize() {
		exportarIcon.setImage(new Image(getClass().getResourceAsStream("/images/exportar.png")));
		editarContactoIcon.setImage(new Image(getClass().getResourceAsStream("/images/editarUsuario.png")));
		cerrarSesionIcon.setImage(new Image(getClass().getResourceAsStream("/images/cerrarSesion.png")));
		ayudaIcon.setImage(new Image(getClass().getResourceAsStream("/images/help.png")));

		cerrarSesionButton.setOnAction(event -> volverLogin());
		editarContactoButton.setOnAction(event -> editarPeregrino());
		exportarButton.setOnAction(event -> exportarPeregrino());
		ayudaButton.setOnAction(event -> ayudaService.mostrarAyuda("/help/Peregrino.html"));

		configurarAtajo();
	}
	
	 /**
     * Configura el atajo de teclado F1 para mostrar la ayuda de la vista del peregrino.
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
						ayudaService.mostrarAyuda("/help/Peregrino.html");
					}
				});
			}
		});
	}
	
	/**
     * Cierra la sesión actual y redirige al usuario a la vista principal de inicio de sesión.
     */
	private void volverLogin() {
		stageManager.switchScene(FxmlView.PRINCIPAL);
	}
	
	 /**
     * Redirige al usuario a la vista de edición de sus datos de contacto.
     */
	private void editarPeregrino() {
		stageManager.switchScene(FxmlView.EDITPEREGRINO);
	}
	
	/**
     * Redirige al usuario a la vista de exportación de sus propios datos.
     */

	private void exportarPeregrino() {

		stageManager.switchScene(FxmlView.EXPORTPEREGRINO);

	}

}
