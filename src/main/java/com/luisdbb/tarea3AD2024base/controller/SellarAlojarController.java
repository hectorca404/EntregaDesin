package com.luisdbb.tarea3AD2024base.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import com.luisdbb.tarea3AD2024base.config.StageManager;
import com.luisdbb.tarea3AD2024base.modelo.Carnet;
import com.luisdbb.tarea3AD2024base.modelo.Credenciales;
import com.luisdbb.tarea3AD2024base.modelo.Estancia;
import com.luisdbb.tarea3AD2024base.modelo.Parada;
import com.luisdbb.tarea3AD2024base.modelo.ParadasPeregrinos;
import com.luisdbb.tarea3AD2024base.modelo.Peregrino;
import com.luisdbb.tarea3AD2024base.modelo.Perfil;
import com.luisdbb.tarea3AD2024base.services.CredencialesService;
import com.luisdbb.tarea3AD2024base.services.EstanciaService;
import com.luisdbb.tarea3AD2024base.services.ParadaService;
import com.luisdbb.tarea3AD2024base.services.PeregrinoService;
import com.luisdbb.tarea3AD2024base.services.SesionService;
import com.luisdbb.tarea3AD2024base.view.FxmlView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;

@Controller
public class SellarAlojarController {

	@FXML
	private ComboBox<String> peregrinoComboBox;

	@FXML
	private CheckBox alojarCheckBox;

	@FXML
	private CheckBox vipCheckBox;

	@FXML
	private Button confirmarButton;

	@FXML
	private Button limpiarButton;

	@FXML
	private Button volverMenuButton;

	@Autowired
	private CredencialesService credencialesService;

	@Autowired
	private PeregrinoService peregrinoService;
	@Autowired
	private SesionService sesionService;
	@Autowired
	private ParadaService paradaService;

	@Autowired
	private EstanciaService estanciaService;

	@Lazy
	@Autowired
	private StageManager stageManager;

	@FXML
	public void initialize() {
		alojarCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
			vipCheckBox.setDisable(!newValue);
			if (!newValue) {
				vipCheckBox.setSelected(false);
			}
		});
		cargarPeregrinosComboBox();

		volverMenuButton.setOnAction(event -> volverLogin());

		limpiarButton.setOnAction(event -> limpiarFormulario());

		confirmarButton.setOnAction(event -> sellarAlojar());
	}

	private void volverLogin() {
		stageManager.switchScene(FxmlView.RESPARADA);
	}

	private void limpiarFormulario() {
		peregrinoComboBox.getSelectionModel().clearSelection();
		alojarCheckBox.setSelected(false);
		vipCheckBox.setSelected(false);
	}

	private List<Credenciales> cargarPeregrinos() {
		List<Credenciales> credenciales = credencialesService.obtenerUsuarios(Perfil.PEREGRINO);
		return credenciales;
	}

	private void cargarPeregrinosComboBox() {
		List<Credenciales> credenciales = cargarPeregrinos();

		ObservableList<String> peregrinoNombres = FXCollections.observableArrayList();
		for (Credenciales credencial : credenciales) {
			peregrinoNombres.add(credencial.getNombreUsuario());
		}

		peregrinoComboBox.setItems(peregrinoNombres);
	}

	private void sellarAlojar() {
		String nombreUsuarioSeleccionado = peregrinoComboBox.getSelectionModel().getSelectedItem();
		if (nombreUsuarioSeleccionado == null) {
			mostrarAlerta("Error", "Hay que seleccionar un usuario.");
			return;
		}

		Optional<Credenciales> credencialesOp = credencialesService
				.obtenerCredencialPorUsuario(nombreUsuarioSeleccionado);

		Credenciales credenciales = credencialesOp.get();
		Peregrino peregrino = credenciales.getPeregrino();

		Carnet carnet = peregrino.getCarnet();

		carnet.setDistancia(carnet.getDistancia() + 5);

		ParadasPeregrinos paradasPeregrinos = new ParadasPeregrinos(peregrino, sesionService.getParadaActual());
		paradaService.guardarParadasPeregrinos(paradasPeregrinos);

		if (alojarCheckBox.isSelected()) {
			boolean esVip = vipCheckBox.isSelected();

			Estancia estancia = new Estancia();
			estancia.setVip(esVip);
			estancia.setParada(sesionService.getParadaActual());
			estancia.setPeregrino(peregrino);

			estanciaService.guardarEstancia(estancia);

			if (esVip) {
				carnet.setnVips(carnet.getnVips() + 1);
			}
		}

		peregrinoService.actualizarCarnet(carnet);

		mostrarAlerta("Exito", "Sellado/Alojamiento completado.");

		limpiarFormulario();
	}

	private void mostrarAlerta(String titulo, String mensaje) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(titulo);
		alert.setHeaderText(null);
		alert.setContentText(mensaje);
		alert.showAndWait();
	}

}
