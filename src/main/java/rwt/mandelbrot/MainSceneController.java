/*
 * Copyright Richard Todd. I put the code under the
 * GPL v2.0.  See the LICENSE file in the repository.
 * for more information.
 */
package rwt.mandelbrot;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.converter.NumberStringConverter;

/**
 *
 * @author richa
 */
public class MainSceneController implements Initializable, AutoCloseable {
        
    @FXML
    private ImageView drawing;
    
    @FXML
    private RadioButton rbZIn;
    
    @FXML
    private RadioButton rbZOut;
    
    @FXML 
    private TextField txtCX; // bound to sceneX, center of pic
    @FXML 
    private TextField txtCY; // bound to sceneY, center of pic
    @FXML 
    private TextField txtWid; // bound to expanseX
    @FXML 
    private TextField txtHt; // bound to expanseY
    @FXML 
    private TextField txtImWid; // bound to imageX
    @FXML 
    private TextField txtImHt; // bound to imageY
    @FXML 
    private TextField txtImDpth; // bound to imageY
      
    @FXML
    private javafx.scene.control.ChoiceBox<String> which; 
    @FXML
    private javafx.scene.control.TextField arg1;
    @FXML
    private javafx.scene.control.TextField arg2;
    @FXML
    private javafx.scene.control.TextField escVal;
    
    
    @FXML
    private void onReset(ActionEvent event) {
       artist.reset();
       artist.drawScene();
    }
 
    @FXML
    private void onRedraw(ActionEvent event) {
        artist.drawScene();    
    }
        
    @FXML
    private void onSave(ActionEvent event) {
       FileChooser fileChooser = new FileChooser();
       fileChooser.setTitle("Save GIF File");
       fileChooser.getExtensionFilters().add(new ExtensionFilter("GIF Files",".GIF"));
       fileChooser.setInitialFileName("mandel.gif");
       File file = fileChooser.showSaveDialog(drawing.getScene().getWindow());
       if(file == null) return;

       try {
            Image im = drawing.getImage(); //.snapshot(null,null);
            java.awt.image.BufferedImage bim = javafx.embed.swing.SwingFXUtils.fromFXImage(im, null);       
            javax.imageio.ImageIO.write(bim, "gif", file);
       } catch(java.io.IOException e) {
           System.err.println(e.toString());
       }
    }
    
    @FXML
    private void msClick(MouseEvent event) {
        FractalArtist.ZoomOption zo;
        if(rbZIn.isSelected()) {
            zo = FractalArtist.ZoomOption.ZoomIn;
        } else if(rbZOut.isSelected()) {
            zo = FractalArtist.ZoomOption.ZoomOut;
        } else {
            zo = FractalArtist.ZoomOption.NoZoom;
        }
        
        artist.recenter(event.getX(), event.getY(), zo);
        artist.drawScene();
    }
            
    @FXML
    private void onPalette(ActionEvent ae) {
        try {
           FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PaletteBuilder.fxml"));
           Parent root = loader.load();
           PaletteBuilderController pbc = loader.getController();
           pbc.tieToParent((Stage)arg1.getScene().getWindow(), artist.palette);
           Scene sc = new Scene(root);
           Stage st = new Stage(StageStyle.DECORATED);
           st.setTitle("Select Palette");
           st.setScene(sc);
           st.show();
           
        } catch(java.io.IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private FractalArtist artist;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        AlgorithmSelector algos = new AlgorithmSelector();
        algos.fractal.bind(which.valueProperty());
        Bindings.bindBidirectional(arg1.textProperty(),algos.param1, new NumberStringConverter());
        Bindings.bindBidirectional(arg2.textProperty(),algos.param2, new NumberStringConverter());
        Bindings.bindBidirectional(txtImDpth.textProperty(),algos.depth, new NumberStringConverter());
        Bindings.bindBidirectional(escVal.textProperty(),algos.escape, new NumberStringConverter());
   
        artist = new FractalArtist(algos);
        
        // bi-directional bindings to the UI for the scene params...
        txtCX.textProperty().bindBidirectional(artist.sceneX, new NumberStringConverter());
        txtCY.textProperty().bindBidirectional(artist.sceneY, new NumberStringConverter());
        txtWid.textProperty().bindBidirectional(artist.expanseX, new NumberStringConverter());
        txtHt.textProperty().bindBidirectional(artist.expanseY, new NumberStringConverter());
        txtImWid.textProperty().bindBidirectional(artist.imgWid, new NumberStringConverter());
        txtImHt.textProperty().bindBidirectional(artist.imgHt, new NumberStringConverter());
        drawing.imageProperty().bind(artist.image);
   
        artist.drawScene();
    }    

    
    
    @Override
    public void close() throws Exception {
        artist.close();
    }
    
}
