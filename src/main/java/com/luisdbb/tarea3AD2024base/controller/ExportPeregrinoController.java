package com.luisdbb.tarea3AD2024base.controller;

import com.luisdbb.tarea3AD2024base.config.StageManager;
import com.luisdbb.tarea3AD2024base.modelo.Credenciales;
import com.luisdbb.tarea3AD2024base.modelo.Estancia;
import com.luisdbb.tarea3AD2024base.modelo.Parada;
import com.luisdbb.tarea3AD2024base.modelo.Peregrino;
import com.luisdbb.tarea3AD2024base.services.AyudaService;
import com.luisdbb.tarea3AD2024base.services.CredencialesService;
import com.luisdbb.tarea3AD2024base.services.PeregrinoService;
import com.luisdbb.tarea3AD2024base.services.SesionService;
import com.luisdbb.tarea3AD2024base.view.AlertsView;
import com.luisdbb.tarea3AD2024base.view.FxmlView;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRGraphics2DExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleGraphics2DExporterOutput;
import net.sf.jasperreports.view.JasperViewer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Controller;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.imageio.ImageIO;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Controlador de la vista de exportación de datos del peregrino.
 * Permite mostrar la información del carnet y del historial de paradas/estancias,
 * así como exportar dicha información en formato XML e imagen.
 */
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
	private Hyperlink volverMenuLink;
	@FXML
	private Button ayudaButton;

	@FXML
	private ImageView ayudaIcon;

	@Lazy
	@Autowired
	private StageManager stageManager;

	@Autowired
	private SesionService sesionService;

	@Autowired
	private PeregrinoService peregrinoService;

	@Autowired
	private CredencialesService credencialesService;

	@Autowired
	private AyudaService ayudaService;

	@Autowired
	private AlertsView alertsView;
	
	@Autowired
    private DataSource dataSource;

	private Peregrino peregrinoActual;
	
	 /**
     * Inicializa la vista cargando los datos del peregrino actual,
     * sus estancias y configurando los botones y accesos rápidos.
     */
	@FXML
	public void initialize() {
		peregrinoActual = sesionService.getPeregrinoActual();
		
		ayudaIcon.setImage(new Image(getClass().getResourceAsStream("/images/help.png")));

		cargarDatosPeregrino(peregrinoActual);
		cargarParadasYEstancias(peregrinoActual);

		volverMenuLink.setOnAction(event -> volverMenu());
		exportarButton.setOnAction(event -> {
		    exportarPeregrinoXML(peregrinoActual);
		    exportarCarnetPeregrino(peregrinoActual);
		});
		ayudaButton.setOnAction(event -> ayudaService.mostrarAyuda("/help/ExportPeregrino.html"));

		configurarAtajos();
	}
	
	/**
     * Configura atajos de teclado como ENTER (exportar), ESC (volver) y F1 (ayuda).
     */
	private void configurarAtajos() {
		exportarButton.sceneProperty().addListener((observable, oldScene, newScene) -> {
			if (oldScene != null) {
				oldScene.setOnKeyPressed(null);
			}
			if (newScene != null) {
				newScene.setOnKeyPressed(event -> {
					switch (event.getCode()) {
					case ENTER -> {
						event.consume();
						exportarPeregrinoXML(peregrinoActual);
					}
					case ESCAPE -> {
						event.consume();
						volverMenu();
					}
					case F1 -> {
						event.consume();
						ayudaService.mostrarAyuda("/help/ExportPeregrino.html");
					}
					}
				});
			}
		});
	}
	
	/**
     * Carga y muestra los datos personales y del carnet del peregrino.
     *
     * @param peregrino El peregrino cuyos datos se mostrarán.
     */
	private void cargarDatosPeregrino(Peregrino peregrino) {
		nombreField.setText(peregrino.getNombre());
		apellidoField.setText(peregrino.getApellido());

		Credenciales credenciales = credencialesService.obtenerCredencialesPeregrino(peregrino);
		correoField.setText(credenciales != null ? credenciales.getCorreo() : "Sin correo");

		nacionalidadField.setText(peregrino.getNacionalidad());

		idCarnetField.setText(String.valueOf(peregrino.getCarnet().getId()));
		fechaExpedicionField.setText(peregrino.getCarnet().getFechaexp().toString());
		distanciaField.setText(String.valueOf(peregrino.getCarnet().getDistancia()));
		numeroVipsField.setText(String.valueOf(peregrino.getCarnet().getnVips()));

		paradaInicialField.setText(peregrino.getCarnet().getParadaInicio().getNombre());

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
	
	/**
     * Carga en la tabla las paradas visitadas por el peregrino, indicando
     * si se ha alojado y si fue estancia VIP en cada una.
     *
     * @param peregrino El peregrino del cual se obtendrán las paradas y estancias.
     */

	private void cargarParadasYEstancias(Peregrino peregrino) {
		List<Parada> paradas = peregrinoService.obtenerParadas(peregrino.getId());
		ObservableList<Parada> paradasObservable = FXCollections.observableArrayList(paradas);

		tablaParadas.setItems(paradasObservable);

		paradaColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));

		estanciaColumn.setCellValueFactory(cellData -> {
			Parada parada = cellData.getValue();
			boolean tieneEstancia = peregrinoService.obtenerEstancias(peregrino.getId()).stream()
					.anyMatch(estancia -> estancia.getParada().getId().equals(parada.getId()));
			return new SimpleStringProperty(tieneEstancia ? "Estancia" : "Sin estancia");
		});

		vipColumn.setCellValueFactory(cellData -> {
			Parada parada = cellData.getValue();
			boolean esVip = peregrinoService.obtenerEstancias(peregrino.getId()).stream()
					.anyMatch(estancia -> estancia.getParada().getId().equals(parada.getId()) && estancia.isVip());
			return new SimpleStringProperty(esVip ? "Si" : "No");
		});
	}
	
	/**
     * Exporta los datos del peregrino a un archivo XML con su historial de paradas y estancias.
     *
     * @param peregrino El peregrino que será exportado.
     */

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
				regionElement.setTextContent("" + parada.getRegion());
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
				alertsView.mostrarInfo("Expotado correctamente", "Peregrino exportado exitosamente a :" + fichero);
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error al exportar el peregrino XML: " + e.getMessage());
		}
	}
	
	/**
     * Redirige al menú principal del peregrino.
     */
	@FXML
	private void volverMenu() {
		stageManager.switchScene(FxmlView.PEREGRINO);
	}
	
	
	  /**
     * Exporta el carnet del peregrino como una imagen (.png) utilizando JasperReports.
     * La imagen se guarda en disco y se muestra en una nueva ventana de la aplicación.
     *
     * @param peregrinoActual El peregrino del que se exportará el carnet.
     */
	// -----INFORME EXPORTA CAARNET NO INCRUSTADO
	public void exportarCarnetPeregrino(Peregrino peregrinoActual) {
		try {
			File reporteFile = new ClassPathResource("CarnetInforme/Carnet.jasper").getFile();
			JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reporteFile);

			Map<String, Object> parametros = new HashMap<>();
			parametros.put("P_PEREGRINO_ID", peregrinoActual.getId().intValue());
			parametros.put("NOMBRE", peregrinoActual.getNombre());
			parametros.put("APELLIDO", peregrinoActual.getApellido());
			parametros.put("NACIONALIDAD", peregrinoActual.getNacionalidad());
			parametros.put("ID_CARNET", peregrinoActual.getCarnet().getId());
			parametros.put("FECHA_EXPEDICION", peregrinoActual.getCarnet().getFechaexp());
			parametros.put("DISTANCIA", peregrinoActual.getCarnet().getDistancia());
			parametros.put("PARADA_INICIAL", peregrinoActual.getCarnet().getParadaInicio().getNombre());

			Connection connection = DataSourceUtils.getConnection(dataSource);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, connection);

			BufferedImage image = (BufferedImage) JasperPrintManager.printPageToImage(jasperPrint, 0, 2.0f);

			File carpetaExportacion = new File("src/main/resources/CarnetInforme");

			String nombreImagen = peregrinoActual.getNombre().replaceAll(" ", "_") + "_P" + peregrinoActual.getId()
					+ ".png";
			File archivoImagen = new File(carpetaExportacion, nombreImagen);

			ImageIO.write(image, "png", archivoImagen);

			mostrarCarnetEnApp(archivoImagen);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("❌ Error al exportar carnet como imagen: " + e.getMessage());
		}
	}
	
	/**
     * Muestra el carnet exportado como imagen dentro de la aplicación en una nueva ventana.
     *
     * @param archivoImagen Archivo de imagen generado a partir del informe Jasper.
     */
	private void mostrarCarnetEnApp(File archivoImagen) {
		Platform.runLater(() -> {
			try {
				Stage stage = new Stage();
				stage.setTitle("Carnet del Peregrino");

				ImageView imageView = new ImageView(new Image(archivoImagen.toURI().toString()));
				imageView.setFitWidth(600);
				imageView.setPreserveRatio(true);

				StackPane layout = new StackPane(imageView);

				Scene scene = new Scene(layout, 800, 600);
				stage.setScene(scene);
				stage.showAndWait();
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Error al mostrar la imagen en la aplicacion: " + e.getMessage());
			}
		});
	}

}
