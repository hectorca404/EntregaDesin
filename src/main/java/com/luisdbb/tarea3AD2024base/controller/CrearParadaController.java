package com.luisdbb.tarea3AD2024base.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import com.luisdbb.tarea3AD2024base.config.SpringFXMLLoader;
import com.luisdbb.tarea3AD2024base.config.StageManager;
import com.luisdbb.tarea3AD2024base.services.AyudaService;
import com.luisdbb.tarea3AD2024base.services.CredencialesService;
import com.luisdbb.tarea3AD2024base.services.ParadaService;
import com.luisdbb.tarea3AD2024base.services.ValidacionesService;
import com.luisdbb.tarea3AD2024base.view.AlertsView;
import com.luisdbb.tarea3AD2024base.view.FxmlView;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;



@Controller
public class CrearParadaController {

	@FXML
	public TextField responsableField;

	@FXML
	public TextField correoField;

	@FXML
	public TextField nombreParadaField;

	@FXML
	public TextField regionField;

	@FXML
	public TextField usuarioField;

	@FXML
	public PasswordField passwordField;

	@FXML
	public PasswordField confirmPasswordField;

	@FXML
	private Button crearButton;

	@FXML
	private Button limpiarButton;

	@FXML
	private Hyperlink volverMenuLink;

	@FXML
	private Button ayudaButton;

	@FXML
	private ImageView ayudaIcon;

	@Lazy
	@Autowired
	private StageManager stageManager;

	@Autowired
	public ParadaService paradaService;

	@Autowired
	private AyudaService ayudaService;

	@Autowired
	public ValidacionesService validacionesService;

	@Autowired
	public AlertsView alertsView;
	
	/**
	 * Configura atajos de teclado para la vista:
	 * ENTER (crear parada), F1 (ayuda), ESCAPE (volver) y Ctrl+L (limpiar formulario).
	 */
	@FXML
	public void initialize() {
		regionField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.length() > 1) {
				regionField.setText(oldValue);
			}
		});
		ayudaIcon.setImage(new Image(getClass().getResourceAsStream("/images/help.png")));

		crearButton.setOnAction(event -> registrarParada());
		limpiarButton.setOnAction(event -> limpiarFormulario());
		volverMenuLink.setOnAction(event -> volverMenu());
		ayudaButton.setOnAction(event -> ayudaService.mostrarAyuda("/help/CrearParada.html"));

		configurarAtajos();
	}
	
	
	/**
	 * Configura atajos de teclado para la vista:
	 * ENTER (crear parada), F1 (ayuda), ESCAPE (volver) y Ctrl+L (limpiar formulario).
	 */
	private void configurarAtajos() {
		crearButton.sceneProperty().addListener((observable, oldScene, newScene) -> {
			if (oldScene != null) {
				oldScene.setOnKeyPressed(null);
			}
			if (newScene != null) {
				newScene.setOnKeyPressed(event -> {
					switch (event.getCode()) {
					case ENTER -> {
						event.consume();
						registrarParada();
					}
					case F1 -> {
						event.consume();
						ayudaService.mostrarAyuda("/help/CrearParada.html");
					}
					case ESCAPE -> {
						event.consume();
						volverMenu();
					}
					case L -> {
						if (event.isControlDown()) {
							event.consume();
							limpiarFormulario();
						}
					}
					}
				});
			}
		});
	}
	
	/**
	 * Valida los campos del formulario y registra una nueva parada en el sistema.
	 * Muestra mensajes de error en caso de validaciones fallidas o excepciones.
	 */
	public void registrarParada() {
		try {
			String nombre = nombreParadaField.getText();
			char region = regionField.getText().charAt(0);
			String responsable = responsableField.getText();
			String usuario = usuarioField.getText();
			String correo = correoField.getText();
			String password = passwordField.getText();
			String confirmPassword = confirmPasswordField.getText();

			if (!validacionesService.validarNombreUsuario(usuario)) {
				return;
			}

			if (validacionesService.existeUsuario(usuario)) {
				return;
			}

			if (!validacionesService.validarCorreo(correo)) {
				return;
			}
			if (!password.equals(confirmPassword)) {
				alertsView.mostrarError("Error", "Las contraseñas no coinciden");
				return;
			}

			if (!validacionesService.validarNombreParadaYResponsable(nombre)) {
				return;
			}

			if (!validacionesService.validarNombreParadaYResponsable(responsable)) {
				return;
			}

			if (!validacionesService.validarSinEspacios(password)) {
				return;
			}

			if (validacionesService.existeNombreYRegion(nombre, region)) {
				alertsView.mostrarError("Error", "Ya existe una parda con ese nombre en esa region");
				return;
			}

			paradaService.registrarParada(nombre, region, responsable, usuario, correo, password);
			alertsView.mostrarInfo("Exitoso", "Parada registrada correctamente");
			limpiarFormulario();
		} catch (Exception e) {
			alertsView.mostrarError("Error", e.getMessage());
		}
	}
	
	/**
	 * Limpia todos los campos del formulario de registro de parada.
	 */

	private void limpiarFormulario() {
		responsableField.clear();
		correoField.clear();
		usuarioField.clear();
		nombreParadaField.clear();
		regionField.clear();
		passwordField.clear();
		confirmPasswordField.clear();
	}
	
	/**
	 * Redirige al usuario de vuelta al menú del administrador.
	 */

	private void volverMenu() {
		stageManager.switchScene(FxmlView.ADMIN);
	}

}
