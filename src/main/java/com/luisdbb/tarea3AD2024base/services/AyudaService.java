package com.luisdbb.tarea3AD2024base.services;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.File;

import org.springframework.stereotype.Service;

@Service
public class AyudaService {

	public void mostrarAyuda(String path) {
		Platform.runLater(() -> {
			Stage stage = new Stage();
			stage.setTitle("Ayuda");

			WebView webView = new WebView();
			webView.getEngine().load(getClass().getResource(path).toExternalForm());

			Scene scene = new Scene(webView, 800, 600);
			stage.setScene(scene);
			stage.show();
		});
	}
	
	

}
