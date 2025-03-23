package com.luisdbb.tarea3AD2024base.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import com.luisdbb.tarea3AD2024base.config.SpringFXMLLoader;
import com.luisdbb.tarea3AD2024base.config.StageManager;
import com.luisdbb.tarea3AD2024base.services.AyudaService;
import com.luisdbb.tarea3AD2024base.view.FxmlView;


/**
 * Controlador de la vista de recuperación de contraseña.
 * Permite al usuario regresar al login o acceder a la ayuda contextual.
 * (La funcionalidad de recuperación aún no está implementada).
 */
@Controller
public class ForgotPasswordController {

	@FXML
	private TextField userField;

	@FXML
	private Button recuperarButton;

	@FXML
	private Button ayudaButton;

	@FXML
	private Hyperlink volverLogin;

	@FXML
	private ImageView ayudaIcon;

	@Lazy
	@Autowired
	private StageManager stageManager;

	@Autowired
	private AyudaService ayudaService;
	
	/**
     * Inicializa la vista de recuperación de contraseña.
     * Configura los iconos, botones y atajos de teclado.
     */
	@FXML
	public void initialize() {
		ayudaIcon.setImage(new Image(getClass().getResourceAsStream("/images/help.png")));

		volverLogin.setOnAction(event -> volverLogin());
		ayudaButton.setOnAction(event -> ayudaService.mostrarAyuda("/help/ForgotPass.html"));

		configurarAtajos();
	}
	
	/**
     * Configura atajos de teclado como ENTER (pendiente de implementación),
     * F1 (ayuda) y ESCAPE (volver al login).
     */

	private void configurarAtajos() {
		userField.sceneProperty().addListener((observable, oldScene, newScene) -> {
			if (newScene != null) {
				newScene.setOnKeyPressed(event -> {
					switch (event.getCode()) {
					case ENTER -> event.consume();
					case F1 -> {
						event.consume();
						ayudaService.mostrarAyuda("/help/ForgotPass.html");
					}
					case ESCAPE -> {
						event.consume();
						volverLogin();
					}
					}
				});
			}
		});
	}
	
	/**
     * Redirige al usuario de vuelta a la vista de inicio de sesión.
     */

	private void volverLogin() {
		stageManager.switchScene(FxmlView.PRINCIPAL);
	}
}
