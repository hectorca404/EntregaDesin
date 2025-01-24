package com.luisdbb.tarea3AD2024base.controller;

import com.luisdbb.tarea3AD2024base.config.StageManager;
import com.luisdbb.tarea3AD2024base.modelo.Credenciales;
import com.luisdbb.tarea3AD2024base.modelo.Estancia;
import com.luisdbb.tarea3AD2024base.modelo.Parada;
import com.luisdbb.tarea3AD2024base.modelo.Peregrino;
import com.luisdbb.tarea3AD2024base.services.CredencialesService;
import com.luisdbb.tarea3AD2024base.services.PeregrinoService;
import com.luisdbb.tarea3AD2024base.services.SesionService;
import com.luisdbb.tarea3AD2024base.view.FxmlView;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Controller
public class ExportPeregrinoController {

    @FXML
    private TextField nombreField;
    @FXML
    private TextField apellidoField;
    @FXML
    private TextField correoField;
    @FXML
    private TextField nacionalidadField;
    @FXML
    private TextField idCarnetField;
    @FXML
    private TextField fechaExpedicionField;
    @FXML
    private TextField distanciaField;
    @FXML
    private TextField numeroVipsField;
    @FXML
    private TextField paradaInicialField;

    @FXML
    private TableView<Parada> tablaParadas;
    @FXML
    private TableColumn<Parada, String> paradaColumn;
    @FXML
    private TableColumn<Parada, String> estanciaColumn;
    @FXML
    private TableColumn<Parada, String> vipColumn;

    @FXML
    private Button exportarButton;
    @FXML
    private Button volverMenuButton;

    @Lazy
    @Autowired
    private StageManager stageManager;

    @Autowired
    private SesionService sesionService;

    @Autowired
    private PeregrinoService peregrinoService;

    @Autowired
    private CredencialesService credencialesService;

    private Peregrino peregrinoActual;

    @FXML
    public void initialize() {
        peregrinoActual = sesionService.getPeregrinoActual();
        
            cargarDatosPeregrino(peregrinoActual);
            cargarParadasYEstancias(peregrinoActual);
        
       volverMenuButton.setOnAction(event -> volverMenu());
       exportarButton.setOnAction(event -> exportarPeregrinoXML(peregrinoActual));
    }

    private void cargarDatosPeregrino(Peregrino peregrino) {
        nombreField.setText(peregrino.getNombre());
        apellidoField.setText(peregrino.getApellido());

        Credenciales credenciales = credencialesService.obtenerCredencialesPeregrino(peregrino);
        correoField.setText(credenciales != null ? credenciales.getCorreo() : "Sin correo");

        nacionalidadField.setText(peregrino.getNacionalidad());

        if (peregrino.getCarnet() != null) {
            idCarnetField.setText(String.valueOf(peregrino.getCarnet().getId()));
            fechaExpedicionField.setText(peregrino.getCarnet().getFechaexp().toString());
            distanciaField.setText(String.valueOf(peregrino.getCarnet().getDistancia()));
            numeroVipsField.setText(String.valueOf(peregrino.getCarnet().getnVips()));
        } else {
            idCarnetField.setText("Sin carnet");
            fechaExpedicionField.setText("Sin fecha");
            distanciaField.setText("0");
            numeroVipsField.setText("0");
        }

        paradaInicialField.setText(
            peregrino.getParadaInicio() != null ? peregrino.getParadaInicio().getNombre() : "Sin parada inicial"
        );

        nombreField.setEditable(false);
        apellidoField.setEditable(false);
        correoField.setEditable(false);
        nacionalidadField.setEditable(false);
        paradaInicialField.setEditable(false);
        idCarnetField.setEditable(false);
        fechaExpedicionField.setEditable(false);
        distanciaField.setEditable(false);
        numeroVipsField.setEditable(false);
    }

    private void cargarParadasYEstancias(Peregrino peregrino) {
        List<Parada> paradas = peregrinoService.obtenerParadas(peregrino.getId());
        ObservableList<Parada> paradasObservable = FXCollections.observableArrayList(paradas);

        tablaParadas.setItems(paradasObservable);

        paradaColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));

        estanciaColumn.setCellValueFactory(cellData -> {
            Parada parada = cellData.getValue();
            boolean tieneEstancia = peregrinoService.obtenerEstancias(peregrino.getId())
                .stream()
                .anyMatch(estancia -> estancia.getParada().getId().equals(parada.getId()));
            return new SimpleStringProperty(tieneEstancia ? "Estancia" : "Sin estancia");
        });

        vipColumn.setCellValueFactory(cellData -> {
            Parada parada = cellData.getValue();
            boolean esVip = peregrinoService.obtenerEstancias(peregrino.getId())
                .stream()
                .anyMatch(estancia -> 
                    estancia.getParada().getId().equals(parada.getId()) && estancia.isVip()
                );
            return new SimpleStringProperty(esVip ? "Si" : "No");
        });
    }
    
    public void exportarPeregrinoXML(Peregrino peregrino) {
        String pathExportacion = "src/main/resources/peregrinosXML/";

        List<Parada> paradas = peregrinoService.obtenerParadas(peregrino.getId());
        List<Estancia> estancias = peregrinoService.obtenerEstancias(peregrino.getId());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate fechaExpCarnet = peregrino.getCarnet().getFechaexp();
        String fechaExpFormateada = (fechaExpCarnet != null) ? fechaExpCarnet.format(formatter) : "Fecha no disponible";

        String nombreFichero = peregrino.getNombre().replaceAll(" ", "_") + "_P" + peregrino.getId() + ".xml";
        String fichero = pathExportacion + nombreFichero;

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element carnetElement = doc.createElement("carnet");
            doc.appendChild(carnetElement);

            Element idElement = doc.createElement("id");
            idElement.setTextContent(String.valueOf(peregrino.getCarnet().getId()));
            carnetElement.appendChild(idElement);

            Element fechaExpElement = doc.createElement("fechaexp");
            fechaExpElement.setTextContent(fechaExpFormateada);
            carnetElement.appendChild(fechaExpElement);

            Element expedidoEnElement = doc.createElement("expedidoen");
            expedidoEnElement.setTextContent(peregrino.getCarnet().getParadaInicio().getNombre());
            carnetElement.appendChild(expedidoEnElement);

            Element peregrinoElement = doc.createElement("peregrino");
            carnetElement.appendChild(peregrinoElement);

            Element nombreElement = doc.createElement("nombre");
            nombreElement.setTextContent(peregrino.getNombre());
            peregrinoElement.appendChild(nombreElement);

            Element nacionalidadElement = doc.createElement("nacionalidad");
            nacionalidadElement.setTextContent(peregrino.getNacionalidad());
            peregrinoElement.appendChild(nacionalidadElement);

            Element hoyElement = doc.createElement("hoy");
            hoyElement.setTextContent(LocalDate.now().format(formatter));
            carnetElement.appendChild(hoyElement);

            Element distanciaTotalElement = doc.createElement("distanciatotal");
            distanciaTotalElement.setTextContent(String.valueOf(peregrino.getCarnet().getDistancia()));
            carnetElement.appendChild(distanciaTotalElement);

            Element paradasElement = doc.createElement("paradas");
            carnetElement.appendChild(paradasElement);

            int orden = 1;
            for (Parada parada : paradas) {
                Element paradaElement = doc.createElement("parada");

                Element ordenElement = doc.createElement("orden");
                ordenElement.setTextContent(String.valueOf(orden++));
                paradaElement.appendChild(ordenElement);

                Element nombreParadaElement = doc.createElement("nombre");
                nombreParadaElement.setTextContent(parada.getNombre());
                paradaElement.appendChild(nombreParadaElement);

                Element regionElement = doc.createElement("region");
                regionElement.setTextContent(""+parada.getRegion());
                paradaElement.appendChild(regionElement);

                paradasElement.appendChild(paradaElement);
            }

            Element estanciasElement = doc.createElement("estancias");
            carnetElement.appendChild(estanciasElement);

            for (Estancia estancia : estancias) {
                Element estanciaElement = doc.createElement("estancia");

                Element idEstanciaElement = doc.createElement("id");
                idEstanciaElement.setTextContent(String.valueOf(estancia.getId()));
                estanciaElement.appendChild(idEstanciaElement);

                Element fechaEstanciaElement = doc.createElement("fecha");
                fechaEstanciaElement.setTextContent(estancia.getFecha().format(formatter));
                estanciaElement.appendChild(fechaEstanciaElement);

                Element paradaEstanciaElement = doc.createElement("parada");
                paradaEstanciaElement.setTextContent(estancia.getParada().getNombre());
                estanciaElement.appendChild(paradaEstanciaElement);

                if (estancia.isVip()) {
                    Element vipElement = doc.createElement("vip");
                    vipElement.setTextContent("Sí");
                    estanciaElement.appendChild(vipElement);
                }

                estanciasElement.appendChild(estanciaElement);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            try (OutputStream outputStream = new FileOutputStream(new File(fichero))) {
                transformer.transform(new DOMSource(doc), new StreamResult(outputStream));
                System.out.println("Peregrino XML exportado exitosamente como: " + fichero);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al exportar el peregrino XML: " + e.getMessage());
        }
    }



    @FXML
    private void volverMenu() {
        stageManager.switchScene(FxmlView.PEREGRINO);
    }
}

