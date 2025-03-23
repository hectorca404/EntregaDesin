package com.luisdbb.tarea3AD2024base;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyChar;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationExtension;

import com.luisdbb.tarea3AD2024base.controller.CrearParadaController;
import com.luisdbb.tarea3AD2024base.services.ParadaService;
import com.luisdbb.tarea3AD2024base.services.ValidacionesService;
import com.luisdbb.tarea3AD2024base.view.AlertsView;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

@ExtendWith(MockitoExtension.class)
@ExtendWith(ApplicationExtension.class)
class CrearParadaControllerTest {

    @Mock
    private ParadaService paradaService;

    @Mock
    private ValidacionesService validacionesService;

    @Mock
    private AlertsView alertsView;

    @InjectMocks
    private CrearParadaController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        controller.nombreParadaField = new TextField();
        controller.regionField = new TextField();
        controller.responsableField = new TextField();
        controller.usuarioField = new TextField();
        controller.correoField = new TextField();
        controller.passwordField = new PasswordField();
        controller.confirmPasswordField = new PasswordField();
    }

    // TEST: OK
    @Test
    void testCrearParada_OK() {
        when(validacionesService.validarNombreUsuario(anyString())).thenReturn(true);
        when(validacionesService.existeUsuario(anyString())).thenReturn(false);
        when(validacionesService.validarCorreo(anyString())).thenReturn(true);
        when(validacionesService.validarNombreParadaYResponsable(anyString())).thenReturn(true);
        when(validacionesService.validarSinEspacios(anyString())).thenReturn(true);
        when(validacionesService.existeNombreYRegion(anyString(), anyChar())).thenReturn(false);

        controller.nombreParadaField.setText("Parada Camino");
        controller.regionField.setText("A");
        controller.responsableField.setText("Juan Perez");
        controller.usuarioField.setText("juanperez");
        controller.correoField.setText("juanperez@gmail.com");
        controller.passwordField.setText("password123");
        controller.confirmPasswordField.setText("password123");

        controller.registrarParada();

        verify(paradaService).registrarParada(eq("Parada Camino"), eq('A'), eq("Juan Perez"), eq("juanperez"),
                eq("juanperez@gmail.com"), eq("password123"));
        verify(alertsView).mostrarInfo("Exitoso", "Parada registrada correctamente");
    }
    
 // TEST: KO
    @Test
    void testCrearParada_KO_NoSeGuarda() {
        when(validacionesService.validarNombreUsuario(anyString())).thenReturn(true);
        when(validacionesService.existeUsuario(anyString())).thenReturn(false);
        when(validacionesService.validarCorreo(anyString())).thenReturn(true);
        when(validacionesService.validarNombreParadaYResponsable(anyString())).thenReturn(true);
        when(validacionesService.validarSinEspacios(anyString())).thenReturn(true);

        when(validacionesService.existeNombreYRegion(eq("Parada Camino"), eq('A'))).thenReturn(true);

        controller.nombreParadaField.setText("Parada Camino");
        controller.regionField.setText("A");
        controller.responsableField.setText("Juan Perez");
        controller.usuarioField.setText("juanperez");
        controller.correoField.setText("juanperez@gmail.com");
        controller.passwordField.setText("password123");
        controller.confirmPasswordField.setText("password123");

        controller.registrarParada();
        
        verify(paradaService, never()).registrarParada(any(), anyChar(), any(), any(), any(), any());

        verify(alertsView).mostrarError(eq("Error"), eq("Ya existe una parda con ese nombre en esa region"));
    }


}

