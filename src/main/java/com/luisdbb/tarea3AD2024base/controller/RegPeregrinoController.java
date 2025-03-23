package com.luisdbb.tarea3AD2024base.controller;

import com.luisdbb.tarea3AD2024base.config.StageManager;
import com.luisdbb.tarea3AD2024base.modelo.Parada;
import com.luisdbb.tarea3AD2024base.services.AyudaService;
import com.luisdbb.tarea3AD2024base.services.ParadaService;
import com.luisdbb.tarea3AD2024base.services.PeregrinoService;
import com.luisdbb.tarea3AD2024base.services.ValidacionesService;
import com.luisdbb.tarea3AD2024base.view.AlertsView;
import com.luisdbb.tarea3AD2024base.view.FxmlView;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Controlador de la vista de registro de peregrinos.
 * Gestiona el formulario de registro, la validación de datos,
 * el registro en la base de datos y la carga de datos iniciales como paradas y nacionalidades.
 */
@Controller
public class RegPeregrinoController {

	@FXML
	public TextField userField;

	@FXML
	public TextField nombreField;

	@FXML
	public TextField apellidoField;

	@FXML
	public TextField correoField;

	@FXML
	public ComboBox<String> nacionalidadComboBox;

	@FXML
	public ComboBox<Parada> paradaInicioComboBox;

	@FXML
	public PasswordField passwordField;

	@FXML
	public PasswordField confirmPasswordField;

	@FXML
	public Button registrarButton;

	@FXML
	private Button limpiarButton;

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
	private PeregrinoService peregrinoService;

	@Autowired
	private ParadaService paradaService;

	@Autowired
	private AyudaService ayudaService;

	@Autowired
	private AlertsView alertsView;

	@Autowired
	private ValidacionesService validacionesService;
	
	/**
     * Inicializa la vista cargando nacionalidades y paradas.
     * También configura los eventos de botones e iconos, así como los atajos de teclado.
     */
	@FXML
	public void initialize() {
		cargarNacionalidades();
		cargarParadas();

		ayudaIcon.setImage(new Image(getClass().getResourceAsStream("/images/help.png")));

		registrarButton.setOnAction(event -> registrarPeregrino());
		limpiarButton.setOnAction(event -> limpiarFormulario());
		volverLogin.setOnAction(event -> volverLogin());
		ayudaButton.setOnAction(event -> ayudaService.mostrarAyuda("/help/RegPeregrino.html"));

		configurarAtajos();
	}
	
	/**
     * Configura atajos de teclado como ENTER (registrar), F1 (ayuda),
     * ESC (volver) y Ctrl+L (limpiar).
     */
	private void configurarAtajos() {
		registrarButton.sceneProperty().addListener((observable, oldScene, newScene) -> {
			if (newScene != null) {
				newScene.setOnKeyPressed(event -> {
					switch (event.getCode()) {
					case ENTER -> {
						event.consume();
						registrarPeregrino();
					}
					case F1 -> {
						event.consume();
						ayudaService.mostrarAyuda("/help/RegPeregrino.html");
					}
					case ESCAPE -> {
						event.consume();
						volverLogin();
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
     * Navega de vuelta a la vista principal.
     */
	private void volverLogin() {
		stageManager.switchScene(FxmlView.PRINCIPAL);
	}
	
    /**
     * Carga y muestra las nacionalidades desde un archivo XML.
     */
	private void cargarNacionalidades() {
		List<String> nacionalidades = obtenerNacionalidadesXML("/paises.xml");
		nacionalidadComboBox.setItems(FXCollections.observableArrayList(nacionalidades));
	}
	
	/**
     * Carga y muestra las paradas disponibles desde el servicio.
     */
	private void cargarParadas() {
		List<Parada> paradas = paradaService.obtenerTodasLasParadas();
		paradaInicioComboBox.setItems(FXCollections.observableArrayList(paradas));
	}
	
	/**
     * Registra un nuevo peregrino tras validar los datos del formulario.
     * Si el registro es exitoso, limpia el formulario y muestra una alerta de éxito.
     * Si ocurre un error, muestra la alerta correspondiente.
     */
	public void registrarPeregrino() {

		String nombreUsuario = userField.getText();
		if (validacionesService.existeUsuario(nombreUsuario)) {
			return;
		}
		if (!validacionesService.validarNombreUsuario(nombreUsuario)) {
			return;
		}

		String contrasena = passwordField.getText();
		String confirmarContrasena = confirmPasswordField.getText();

		String correo = correoField.getText();
		if (!validacionesService.validarCorreo(correo)) {
			return;
		}

		String nombre = nombreField.getText();
		String apellido = apellidoField.getText();
		if (!validacionesService.validarNombreYApellido(nombre, apellido)) {
			return;
		}

		String nacionalidad = nacionalidadComboBox.getValue();
		if (!validacionesService.validarComboBox(nacionalidad)) {
			return;
		}

		Parada paradaInicio = paradaInicioComboBox.getValue();
		if (paradaInicio == null) {
			alertsView.mostrarError("Error", "Debes seleccionar una parada de inicio.");
			return;
		}
		if (!validacionesService.validarSinEspacios(contrasena)) {
			return;
		}

		if (!contrasena.equals(confirmarContrasena)) {
			alertsView.mostrarError("Error", "Las contraseñas no coinciden");
			return;
		}

		try {
			peregrinoService.registrarPeregrino(nombreUsuario, contrasena, correo, nombre, apellido, nacionalidad,
					paradaInicio);
			alertsView.mostrarInfo("Peregrino Reistrado", "Peregrino registrado correctamente");
			limpiarFormulario();

		} catch (IllegalArgumentException e) {
			alertsView.mostrarError("Error", e.getMessage());
		}

	}
	
	/**
     * Limpia todos los campos del formulario de registro.
     */
	private void limpiarFormulario() {
		userField.clear();
		nombreField.clear();
		apellidoField.clear();
		correoField.clear();
		nacionalidadComboBox.getSelectionModel().clearSelection();
		paradaInicioComboBox.getSelectionModel().clearSelection();
		passwordField.clear();
		confirmPasswordField.clear();
	}
	
	 /**
     * Obtiene una lista de nacionalidades desde un archivo XML.
     *
     * @param rutaArchivo Ruta al archivo XML con los países.
     * @return Lista de nombres de países extraídos del archivo.
     */
	private List<String> obtenerNacionalidadesXML(String rutaArchivo) {
		List<String> nacionalidades = new ArrayList<>();
		try {
			InputStream inputStream = getClass().getResourceAsStream(rutaArchivo);

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(inputStream);
			NodeList paises = document.getElementsByTagName("pais");
			for (int i = 0; i < paises.getLength(); i++) {
				Element pais = (Element) paises.item(i);
				String nombre = pais.getElementsByTagName("nombre").item(0).getTextContent();
				nacionalidades.add(nombre);
			}
		} catch (Exception e) {
			System.out.println("Error al cargar las nacionalidades: " + e.getMessage());
		}
		return nacionalidades;
	}
}
