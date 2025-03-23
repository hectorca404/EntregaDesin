package com.luisdbb.tarea3AD2024base;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationExtension;

import com.luisdbb.tarea3AD2024base.config.StageManager;
import com.luisdbb.tarea3AD2024base.controller.RegPeregrinoController;
import com.luisdbb.tarea3AD2024base.modelo.Parada;
import com.luisdbb.tarea3AD2024base.services.AyudaService;
import com.luisdbb.tarea3AD2024base.services.ParadaService;
import com.luisdbb.tarea3AD2024base.services.PeregrinoService;
import com.luisdbb.tarea3AD2024base.services.ValidacionesService;
import com.luisdbb.tarea3AD2024base.view.AlertsView;
import com.luisdbb.tarea3AD2024base.view.FxmlView;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

@ExtendWith(ApplicationExtension.class)
class RegPeregrinoControllerTest {

    @InjectMocks
    private RegPeregrinoController controller;

    @Mock
    private PeregrinoService peregrinoService;
    
    @Mock
    private ParadaService paradaService;
    
    @Mock
    private AyudaService ayudaService;
    
    @Mock
    private AlertsView alertsView;
    
    @Mock
    private ValidacionesService validacionesService;
    
    @Mock
    private StageManager stageManager;

    private TextField userField;
    private TextField nombreField;
    private TextField apellidoField;
    private TextField correoField;
    private ComboBox<String> nacionalidadComboBox;
    private ComboBox<Parada> paradaInicioComboBox;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private Button registrarButton;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userField = new TextField();
        nombreField = new TextField();
        apellidoField = new TextField();
        correoField = new TextField();
        nacionalidadComboBox = new ComboBox<>();
        paradaInicioComboBox = new ComboBox<>();
        passwordField = new PasswordField();
        confirmPasswordField = new PasswordField();
        registrarButton = new Button();

        controller.userField = userField;
        controller.nombreField = nombreField;
        controller.apellidoField = apellidoField;
        controller.correoField = correoField;
        controller.nacionalidadComboBox = nacionalidadComboBox;
        controller.paradaInicioComboBox = paradaInicioComboBox;
        controller.passwordField = passwordField;
        controller.confirmPasswordField = confirmPasswordField;
        controller.registrarButton = registrarButton;
    }

    /**
     * Test OK: Registro exitoso de peregrino
     */
    @Test
    void testRegistrarPeregrino_OK() {
        when(validacionesService.validarNombreUsuario(anyString())).thenReturn(true);
        when(validacionesService.existeUsuario(anyString())).thenReturn(false);
        when(validacionesService.validarCorreo(anyString())).thenReturn(true);
        when(validacionesService.validarNombreYApellido(anyString(), anyString())).thenReturn(true);
        when(validacionesService.validarComboBox(anyString())).thenReturn(true);
        when(validacionesService.validarSinEspacios(anyString())).thenReturn(true);

        Parada parada = new Parada();
        paradaInicioComboBox.setValue(parada);

        userField.setText("hectorc");
        nombreField.setText("Hector");
        apellidoField.setText("Castane");
        correoField.setText("hector@gmail.com");
        nacionalidadComboBox.setValue("España");
        passwordField.setText("password123");
        confirmPasswordField.setText("password123");

        controller.registrarPeregrino();

        alertsView.mostrarInfo("Peregrino Registrado", "Peregrino registrado correctamente");

        verify(peregrinoService).registrarPeregrino(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), any(Parada.class));
    }
    
    @Test
    void testRegistrarPeregrino_KO_ContraseñasNoCoinciden() {
        when(validacionesService.validarNombreUsuario(anyString())).thenReturn(true);
        when(validacionesService.existeUsuario(anyString())).thenReturn(false);
        when(validacionesService.validarCorreo(anyString())).thenReturn(true);
        when(validacionesService.validarNombreYApellido(anyString(), anyString())).thenReturn(true);
        when(validacionesService.validarComboBox(anyString())).thenReturn(true);
        when(validacionesService.validarSinEspacios(anyString())).thenReturn(true);

        Parada parada = new Parada();
        paradaInicioComboBox.setValue(parada);

        userField.setText("hectorc");
        nombreField.setText("Hector");
        apellidoField.setText("Castane");
        correoField.setText("hector@gmail.com");
        nacionalidadComboBox.setValue("España");
        passwordField.setText("password123");
        confirmPasswordField.setText("password456"); 

        controller.registrarPeregrino();

        verify(alertsView, times(1)).mostrarError(eq("Error"), eq("Las contraseñas no coinciden"));

        verify(peregrinoService, never()).registrarPeregrino(any(), any(), any(), any(), any(), any(), any());
    }



}
