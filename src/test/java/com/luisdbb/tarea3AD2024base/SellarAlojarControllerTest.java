package com.luisdbb.tarea3AD2024base;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationExtension;

import com.luisdbb.tarea3AD2024base.controller.SellarAlojarController;
import com.luisdbb.tarea3AD2024base.modelo.Carnet;
import com.luisdbb.tarea3AD2024base.modelo.Credenciales;
import com.luisdbb.tarea3AD2024base.modelo.Parada;
import com.luisdbb.tarea3AD2024base.modelo.Peregrino;
import com.luisdbb.tarea3AD2024base.services.CredencialesService;
import com.luisdbb.tarea3AD2024base.services.EstanciaService;
import com.luisdbb.tarea3AD2024base.services.ParadaService;
import com.luisdbb.tarea3AD2024base.services.PeregrinoService;
import com.luisdbb.tarea3AD2024base.services.SesionService;
import com.luisdbb.tarea3AD2024base.services.ValidacionesService;
import com.luisdbb.tarea3AD2024base.view.AlertsView;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;

@ExtendWith(MockitoExtension.class)
@ExtendWith(ApplicationExtension.class)
class SellarAlojarControllerTest {

    @Mock
    private CredencialesService credencialesService;

    @Mock
    private ParadaService paradaService;

    @Mock
    private PeregrinoService peregrinoService;

    @Mock
    private EstanciaService estanciaService;

    @Mock
    private SesionService sesionService;

    @Mock
    private ValidacionesService validacionesService;

    @Mock
    private AlertsView alertsView;

    private SellarAlojarController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new SellarAlojarController();

        controller.peregrinoComboBox = new ComboBox<>();
        controller.alojarCheckBox = new CheckBox();
        controller.vipCheckBox = new CheckBox();
        controller.confirmarButton = new Button();

        controller.credencialesService = credencialesService;
        controller.paradaService = paradaService;
        controller.peregrinoService = peregrinoService;
        controller.estanciaService = estanciaService;
        controller.sesionService = sesionService;
        controller.validacionesService = validacionesService;
        controller.alertsView = alertsView;
    }

    // **Test OK: Sellado y alojamiento exitoso**
    @Test
    void testSellarAlojar_OK() {
        Credenciales credenciales = new Credenciales();
        Peregrino peregrino = new Peregrino();
        Parada parada = new Parada();
        Carnet carnet = new Carnet();

        peregrino.setCarnet(carnet);
        credenciales.setPeregrino(peregrino);

        when(credencialesService.obtenerCredencialPorUsuario(anyString())).thenReturn(Optional.of(credenciales));
        when(sesionService.getParadaActual()).thenReturn(parada);
        when(validacionesService.yaSelladoHoy(any(), any(), any())).thenReturn(false);
        when(validacionesService.yaAlojoHoy(any(), any(), any())).thenReturn(false);

        controller.peregrinoComboBox.getItems().add("peregrino1");
        controller.peregrinoComboBox.getSelectionModel().select("peregrino1");

        controller.alojarCheckBox.setSelected(true);
        controller.vipCheckBox.setSelected(true);

		controller.sellarAlojar();

        verify(paradaService).guardarParadasPeregrinos(any());
        verify(estanciaService).guardarEstancia(any());
        verify(peregrinoService).actualizarCarnet(any());
        verify(alertsView).mostrarInfo("Éxito", "Sellado/Alojamiento completado.");
    }

    // **Test KO: Usuario no seleccionado**
    @Test
    void testSellarAlojar_KO_UsuarioNoSeleccionado() {
        controller.peregrinoComboBox.getSelectionModel().clearSelection();

        controller.sellarAlojar();

        verify(alertsView).mostrarError("Error", "Hay que seleccionar un usuario.");
        verify(paradaService, never()).guardarParadasPeregrinos(any());
    }

    // **Test KO: Usuario ya selló hoy**
    @Test
    void testSellarAlojar_KO_YaSellado() {
        Credenciales credenciales = new Credenciales();
        Peregrino peregrino = new Peregrino();
        Parada parada = new Parada();

        credenciales.setPeregrino(peregrino);

        when(credencialesService.obtenerCredencialPorUsuario(anyString())).thenReturn(Optional.of(credenciales));
        when(sesionService.getParadaActual()).thenReturn(parada);
        when(validacionesService.yaSelladoHoy(any(), any(), any())).thenReturn(true);

        controller.peregrinoComboBox.getItems().add("peregrino1");
        controller.peregrinoComboBox.getSelectionModel().select("peregrino1");

        controller.alojarCheckBox.setSelected(false);

        controller.sellarAlojar();

        verify(alertsView).mostrarError("Error", "Este peregrino ya ha sellado en esta parada hoy.");
        verify(paradaService, never()).guardarParadasPeregrinos(any());
    }

    // **Test KO: Usuario ya alojado hoy**
    @Test
    void testSellarAlojar_KO_YaAlojado() {
        Credenciales credenciales = new Credenciales();
        Peregrino peregrino = new Peregrino();
        Parada parada = new Parada();

        credenciales.setPeregrino(peregrino);

        when(credencialesService.obtenerCredencialPorUsuario(anyString())).thenReturn(Optional.of(credenciales));
        when(sesionService.getParadaActual()).thenReturn(parada);
        when(validacionesService.yaSelladoHoy(any(), any(), any())).thenReturn(false);
        when(validacionesService.yaAlojoHoy(any(), any(), any())).thenReturn(true);

        controller.peregrinoComboBox.getItems().add("peregrino1");
        controller.peregrinoComboBox.getSelectionModel().select("peregrino1");

        controller.alojarCheckBox.setSelected(true);

        controller.sellarAlojar();

        verify(alertsView).mostrarError("Error", "Este peregrino ya se ha alojado en esta parada hoy.");
        verify(estanciaService, never()).guardarEstancia(any());
    }
}
