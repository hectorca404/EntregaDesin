package com.luisdbb.tarea3AD2024base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationExtension;

import com.luisdbb.tarea3AD2024base.controller.EditPeregrinoController;
import com.luisdbb.tarea3AD2024base.modelo.Peregrino;
import com.luisdbb.tarea3AD2024base.services.PeregrinoService;
import com.luisdbb.tarea3AD2024base.services.SesionService;
import com.luisdbb.tarea3AD2024base.services.ValidacionesService;
import com.luisdbb.tarea3AD2024base.view.AlertsView;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

@ExtendWith(ApplicationExtension.class)
class EditPeregrinoControllerTest {

	@InjectMocks
	private EditPeregrinoController controller;

	@Mock
	private PeregrinoService peregrinoService;

	@Mock
	private ValidacionesService validacionesService;

	@Mock
	private SesionService sesionService;

	@Mock
	private AlertsView alertsView;

	private TextField nombreField;
	private TextField apellidoField;
	private TextField correoField;
	private ComboBox<String> nacionalidadComboBox;
	private Button guardarButton;

	private Peregrino peregrino;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		nombreField = new TextField();
		apellidoField = new TextField();
		correoField = new TextField();
		nacionalidadComboBox = new ComboBox<>();
		guardarButton = new Button();

		controller.nombreField = nombreField;
		controller.apellidoField = apellidoField;
		controller.correoField = correoField;
		controller.nacionalidadComboBox = nacionalidadComboBox;
		controller.guardarButton = guardarButton;

		peregrino = new Peregrino();
		peregrino.setNombre("Hector");
		peregrino.setApellido("Castane");
		peregrino.setNacionalidad("España");
	}

	@Test
	void testGuardarCambios_OK() {
	    when(validacionesService.validarNombreYApellido(anyString(), anyString())).thenReturn(true);
	    when(validacionesService.validarComboBox(anyString())).thenReturn(true);
	    when(validacionesService.validarCorreo(anyString())).thenReturn(true);

	    // ✅ Usamos un objeto Peregrino real y lo establecemos como el actual
	    peregrino.setNombre("AntiguoNombre");
	    peregrino.setApellido("AntiguoApellido");
	    peregrino.setNacionalidad("Francia");

	    when(sesionService.getPeregrinoActual()).thenReturn(peregrino);

	    // ✅ Simulamos los valores ingresados en los campos
	    nombreField.setText("Hector");
	    apellidoField.setText("Castane");
	    correoField.setText("hector@gmail.com");
	    nacionalidadComboBox.setValue("España");

	    // 🔥 Aseguramos que `guardarCambios()` usa el mismo objeto `peregrinoActual`
	    controller.peregrinoActual = peregrino; // <== 🚀 Importante

	    // 🔥 Llamamos al método que realiza los cambios
	    controller.guardarCambios();

	    // ✅ Verificamos que el objeto **REALMENTE** cambió sus valores
	    assertEquals("Hector", peregrino.getNombre(), "El nombre no se actualizó correctamente.");
	    assertEquals("Castane", peregrino.getApellido(), "El apellido no se actualizó correctamente.");
	    assertEquals("España", peregrino.getNacionalidad(), "La nacionalidad no se actualizó correctamente.");

	    // ✅ Aseguramos que el servicio fue llamado con los datos correctos
	    verify(peregrinoService).guardarCambiosPeregrino(eq(peregrino), eq("hector@gmail.com"));

	    // ✅ Aseguramos que se muestra el mensaje de éxito
	    verify(alertsView).mostrarInfo("Peregrino Editado Correctamente", "Los cambios se guardaron correctamente");
	}



	// TEST: KO
	@Test
	void testGuardarCambios_KO() {
		when(validacionesService.validarNombreYApellido(anyString(), anyString())).thenReturn(true);
		when(validacionesService.validarComboBox(anyString())).thenReturn(true);
		when(validacionesService.validarCorreo(anyString())).thenReturn(true);

		doThrow(new IllegalArgumentException("Error al guardar los cambios.")).when(peregrinoService)
				.guardarCambiosPeregrino(any(), anyString());

		nombreField.setText("Hector");
		apellidoField.setText("Castane");
		correoField.setText("hector@gmail.com");
		nacionalidadComboBox.setValue("España");

		controller.guardarCambios();

		verify(alertsView).mostrarError("Error", "Error al guardar los cambios.");
	}
}
