/*
 * Copyright Richard Todd. I put the code under the
 * GPL v2.0.  See the LICENSE file in the repository.
 * for more information.
 */
package rwt.mandelbrot;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.*;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;

/**
 *
 * @author richa
 */
public class MainSceneController implements Initializable, AutoCloseable {
        
    @FXML
    private Canvas drawing;
    
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
    
    private Color[] palette;
       
    @FXML
    private void onReset(ActionEvent event) {
       sceneX.set(0.0);
       sceneY.set(0.0);
       expanseX.set(2.0);
       expanseY.set(2.0);
       drawScene();
    }
   
    @FXML
    private javafx.scene.control.ChoiceBox<String> which;
    
    @FXML
    private javafx.scene.control.TextField arg1;

    @FXML
    private javafx.scene.control.TextField arg2;
    
    @FXML
    private void onRedraw(ActionEvent event) {
        drawScene();    
    }
    
    private AlgorithmSelector algos;
    
    @FXML
    private void onSave(ActionEvent event) {
       FileChooser fileChooser = new FileChooser();
       fileChooser.setTitle("Save GIF File");
       fileChooser.getExtensionFilters().add(new ExtensionFilter("GIF Files",".GIF"));
       fileChooser.setInitialFileName("mandel.gif");
       File file = fileChooser.showSaveDialog(drawing.getScene().getWindow());
       if(file == null) return;

       try {
            Image im = drawing.snapshot(null,null);
            java.awt.image.BufferedImage bim = javafx.embed.swing.SwingFXUtils.fromFXImage(im, null);       
            javax.imageio.ImageIO.write(bim, "gif", file);
       } catch(java.io.IOException e) {
           System.err.println(e.toString());
       }
    }
    
    @FXML
    private void msClick(MouseEvent event) {
        // determine a new center...
        double pctX = event.getX()/ drawing.getWidth();
        double pctY = event.getY()/ drawing.getHeight();
        sceneX.set(sceneX.get() + (expanseX.get()*(pctX - 0.5)));
        sceneY.set(sceneY.get() + (expanseY.get()*(pctY - 0.5)));
        
        // now possibly rescale ...
        if(rbZIn.isSelected()) {
            expanseX.set(expanseX.get() * 0.5);
            expanseY.set(expanseY.get() * 0.5);
        }else if (rbZOut.isSelected()) {
            expanseX.set(expanseX.get() * 2.0);
            expanseY.set(expanseY.get() * 2.0);
        }
        
        drawScene();
    }
    
    private DoubleProperty sceneX;
    private DoubleProperty sceneY;
    private DoubleProperty expanseX;
    private DoubleProperty expanseY;
        
    private void displayScenePart(Image im, double x, double y) {
        final GraphicsContext gc = drawing.getGraphicsContext2D();
        gc.drawImage(im, x, y);
    }
    
    private ExecutorService threadpool;
    private List<Future<?>> futures;
    
    private Image drawImagePart(final double startX, final double expX, 
                                final double startY, final double expY, 
                                final int wid, final int ht,
                                final PixelSupplier ps) {
        final WritableImage image = new WritableImage(wid, ht);
        final PixelWriter pw = image.getPixelWriter();
        
        for(int y = 0; y < ht; ++y) {
            final double ylevel = startY + y*expY/ht;
            for(int x = 0; x < wid; ++x) {
                int c = ps.colorPixel(startX + x*expX/wid, ylevel);
                pw.setColor(x, y, palette[c]);
            }
        }
        return image;
    }
    
    private void drawScene() {
        // cancel any work we might be doing...
        for(Future<?> f : futures) {
            f.cancel(true);
        }
        futures.clear();
        
        final double expX = expanseX.get()*0.5;
        final double expY = expanseY.get()*0.5;
        final double sceneULX = sceneX.get() - expX;
        final double sceneULY = sceneY.get() - expY;
        final int wid = (int)(drawing.getWidth()/2.0);
        final int ht = (int)(drawing.getHeight()/2.0);
        final PixelSupplier supplier = algos.getSupplier();
        
        // submit new work...
        for(int ypct = 0; ypct < 2; ypct++) {
            final int _ypct = ypct;
            for(int xpct = 0; xpct < 2; xpct++) {
                final int _xpct = xpct; 
                Future<?> f = threadpool.submit(() -> {
                      Image i = drawImagePart(sceneULX + _xpct*expX, 
                                              expX,
                                              sceneULY + _ypct*expY,
                                              expY,
                                              wid,
                                              ht,
                                              supplier);
                      
                      Platform.runLater(() -> { 
                           displayScenePart(i,_xpct*wid,_ypct*ht); 
                      });        
                });
                futures.add(f);
            }
        }
                
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        sceneX = new SimpleDoubleProperty(0.0);
        sceneY = new SimpleDoubleProperty(0.0);
        expanseX = new SimpleDoubleProperty(2.0);
        expanseY = new SimpleDoubleProperty(2.0);
    
        // bi-directional bindings to the UI for the scene params...
        txtCX.textProperty().bindBidirectional(sceneX, new NumberStringConverter());
        txtCY.textProperty().bindBidirectional(sceneY, new NumberStringConverter());
        txtWid.textProperty().bindBidirectional(expanseX, new NumberStringConverter());
        txtHt.textProperty().bindBidirectional(expanseY, new NumberStringConverter());
        
        futures = new ArrayList<>();
        threadpool = Executors.newFixedThreadPool(4);
        palette = new Color[256];
        for(int idx = 0; idx < 256; ++idx) {
            palette[idx] = Color.grayRgb(idx);
        }
        algos = new AlgorithmSelector(which.valueProperty(), arg1.textProperty(), arg2.textProperty());
        drawScene();
    }    

    @Override
    public void close() throws Exception {
        if(threadpool != null) {
            threadpool.shutdown();
        }
    }
    
}
