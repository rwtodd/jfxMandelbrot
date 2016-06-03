/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rwt.mandelbrot;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 *
 * @author richa
 */
public class JFXMandelbrot extends Application {
    
    AutoCloseable controller;
    
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        Pane root = fxmlLoader.load(getClass().getResource("MainScene.fxml").openStream());
        controller = (AutoCloseable) fxmlLoader.getController();        
        stage.setTitle("Mandelbrot Explorer");
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        if(controller != null) {
            try {
                controller.close();
            } catch (Exception ex) {
                System.err.println("Problem closing! " + ex.toString());
            }
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
