package com.luisdbb.tarea3AD2024base.controller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.luisdbb.tarea3AD2024base.config.StageManager;
import com.luisdbb.tarea3AD2024base.modelo.Credenciales;
import com.luisdbb.tarea3AD2024base.modelo.Peregrino;
import com.luisdbb.tarea3AD2024base.services.CredencialesService;
import com.luisdbb.tarea3AD2024base.services.PeregrinoService;
import com.luisdbb.tarea3AD2024base.services.SesionService;
import com.luisdbb.tarea3AD2024base.view.FxmlView;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

@Controller
public class EditPeregrinoController {

	@FXML
	private TextField nombreField;
	@FXML
	private TextField apellidoField;
	@FXML
	private TextField correoField;
	@FXML
	private ComboBox<String> nacionalidadComboBox;
	@FXML
	private Button guardarButton;
	@FXML
	private Button limpiarButton;
	@FXML
	private Button volverMenuButton;

	@Lazy
	@Autowired
	private StageManager stageManager;

	@Autowired
	private PeregrinoService peregrinoService;

	@Autowired
	private CredencialesService credencialesService;

	@Autowired
	private SesionService sesionService;

	private Peregrino peregrinoActual;

	@FXML
	public void initialize() {
		peregrinoActual = sesionService.getPeregrinoActual();

		cargarPeregrino(peregrinoActual);

		guardarButton.setOnAction(event -> guardarCambios());
		limpiarButton.setOnAction(event -> limpiarFormulario());
		volverMenuButton.setOnAction(event -> volverMenu());

		cargarNacionalidades();
	}

	public void cargarPeregrino(Peregrino peregrino) {
		nombreField.setText(peregrino.getNombre());
		apellidoField.setText(peregrino.getApellido());

		Credenciales credenciales = credencialesService.obtenerCredencialesPeregrino(peregrino);
		correoField.setText(credenciales.getCorreo());

		nacionalidadComboBox.setValue(peregrino.getNacionalidad());
	}

	private void cargarNacionalidades() {
		try {
			List<String> nacionalidades = obtenerNacionalidadesXML("/paises.xml");
			nacionalidadComboBox.setItems(FXCollections.observableArrayList(nacionalidades));
		} catch (Exception e) {
			mostrarAlerta("Error", "No se pudieron cargar las nacionalidades", Alert.AlertType.ERROR);
		}
	}

	private void guardarCambios() {

		peregrinoActual.setNombre(nombreField.getText());
		peregrinoActual.setApellido(apellidoField.getText());
		peregrinoActual.setNacionalidad(nacionalidadComboBox.getValue());

		String nuevoCorreo = correoField.getText();

		peregrinoService.guardarCambiosPeregrino(peregrinoActual, nuevoCorreo);

		mostrarAlerta("Peregrino Editado Correctamente", "Los cambios se guardaron correctamente",
				Alert.AlertType.INFORMATION);
		volverMenu();

	}

	private void limpiarFormulario() {
		nombreField.clear();
		apellidoField.clear();
		correoField.clear();
		nacionalidadComboBox.getSelectionModel().clearSelection();
	}

	private void volverMenu() {
		stageManager.switchScene(FxmlView.PEREGRINO);
	}

	private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
		Alert alerta = new Alert(tipo);
		alerta.setTitle(titulo);
		alerta.setHeaderText(null);
		alerta.setContentText(mensaje);
		alerta.showAndWait();
	}

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
