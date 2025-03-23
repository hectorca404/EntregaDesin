package com.luisdbb.tarea3AD2024base;

import static org.mockito.ArgumentMatchers.any;
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

import com.luisdbb.tarea3AD2024base.config.StageManager;
import com.luisdbb.tarea3AD2024base.controller.PrincipalController;
import com.luisdbb.tarea3AD2024base.modelo.Credenciales;
import com.luisdbb.tarea3AD2024base.modelo.Parada;
import com.luisdbb.tarea3AD2024base.modelo.Peregrino;
import com.luisdbb.tarea3AD2024base.modelo.Perfil;
import com.luisdbb.tarea3AD2024base.services.SesionService;
import com.luisdbb.tarea3AD2024base.services.ValidacionesService;
import com.luisdbb.tarea3AD2024base.view.FxmlView;

import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

@ExtendWith(MockitoExtension.class)
@ExtendWith(ApplicationExtension.class)
class PrincipalControllerTest {

    @Mock
    private ValidacionesService validacionesService;

    @Mock
    private StageManager stageManager;

    @Mock
    private SesionService sesionService;

    @InjectMocks
    private PrincipalController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller.userLogField = new TextField();
        controller.passField = new PasswordField();
        controller.logButton = new Button();
        controller.regisLink = new Hyperlink();
    }

    // **Test OK: Usuario Peregrino inicia sesión correctamente**
    @Test
    void testIniciarSesion_OK_Peregrino() {
        Credenciales credenciales = new Credenciales();
        Peregrino peregrino = new Peregrino();
        credenciales.setPerfil(Perfil.PEREGRINO);
        credenciales.setPeregrino(peregrino);

        when(validacionesService.validarCredenciales(anyString(), anyString())).thenReturn(credenciales);

        controller.userLogField.setText("peregrinoUser");
        controller.passField.setText("password");

		controller.iniciarSesion();

        verify(sesionService).setPeregrinoActual(eq(peregrino));
        verify(stageManager).switchScene(FxmlView.PEREGRINO);
    }

    // **Test OK: Usuario Administrador inicia sesión correctamente**
    @Test
    void testIniciarSesion_OK_Administrador() {
        Credenciales credenciales = new Credenciales();
        credenciales.setPerfil(Perfil.ADMINISTRADOR);

        when(validacionesService.validarCredenciales(anyString(), anyString())).thenReturn(credenciales);

        controller.userLogField.setText("adminUser");
        controller.passField.setText("adminPass");

        controller.iniciarSesion();

        verify(stageManager).switchScene(FxmlView.ADMIN);
    }

    // **Test OK: Usuario de Parada inicia sesión correctamente**
    @Test
    void testIniciarSesion_OK_Parada() {
        Credenciales credenciales = new Credenciales();
        Parada parada = new Parada();
        credenciales.setPerfil(Perfil.PARADA);
        credenciales.setParada(parada);

        when(validacionesService.validarCredenciales(anyString(), anyString())).thenReturn(credenciales);

        controller.userLogField.setText("paradaUser");
        controller.passField.setText("paradaPass");

        controller.iniciarSesion();

        verify(sesionService).setParadaActual(eq(parada));
        verify(stageManager).switchScene(FxmlView.RESPARADA);
    }

    // **Test KO: Usuario o contraseña incorrectos**
    @Test
    void testIniciarSesion_KO_CredencialesInvalidas() {
        when(validacionesService.validarCredenciales(anyString(), anyString())).thenReturn(null);

        controller.userLogField.setText("incorrectUser");
        controller.passField.setText("wrongPass");

        controller.iniciarSesion();

        verify(stageManager, never()).switchScene(any());
    }
}
