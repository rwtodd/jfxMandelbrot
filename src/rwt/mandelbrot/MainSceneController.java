/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.*;
import javafx.scene.control.RadioButton;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

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
    
    private Color[] palette;
    
    @FXML
    private void onReset(ActionEvent event) {
       sceneX =  -0.5;
       sceneY =  0.0;
       expanseX = 2.0;
       expanseY = 2.0;
       drawScene();
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
        sceneX = sceneX + (expanseX*(pctX - 0.5));
        sceneY = sceneY + (expanseY*(pctY - 0.5));
        
        // now possibly rescale ...
        if(rbZIn.isSelected()) {
            expanseX = expanseX * 0.5;
            expanseY = expanseY * 0.5;
        }else if (rbZOut.isSelected()) {
            expanseX = expanseX * 2.0;
            expanseY = expanseY * 2.0;
        }
        
        drawScene();
    }
    
    private double sceneX = -0.5;
    private double sceneY = 0.0;
    private double expanseX = 2.0;
    private double expanseY = 2.0;
        
    private final int computeMandel(final double x, final double y) {
       int answer = 255;
       double  cx = x;
       double  cy = y;
       while(( cx*cx+cy*cy < 4.0) && (answer > 0)) {
          final double tmp  = cx*cy;
          cx = cx*cx - cy*cy + x;
          cy = tmp+tmp + y;
          --answer; 
       }
       return answer; 
    }
    
    private void displayScenePart(Image im, double x, double y) {
        final GraphicsContext gc = drawing.getGraphicsContext2D();
        gc.drawImage(im, x, y);
    }
    
    private ExecutorService threadpool;
    private List<Future<?>> futures;
    
    private Image drawImagePart(final double startX, final double expX, 
                                final double startY, final double expY, 
                                final int wid, final int ht) {
        final WritableImage image = new WritableImage(wid, ht);
        final PixelWriter pw = image.getPixelWriter();
        
        for(int y = 0; y < ht; ++y) {
            final double ylevel = startY + y*expY/ht;
            for(int x = 0; x < wid; ++x) {
                int c = computeMandel(startX + x*expX/wid, ylevel);
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
        
        final double expX = expanseX*0.5;
        final double expY = expanseY*0.5;
        final double sceneULX = sceneX - expX;
        final double sceneULY = sceneY - expY;
        final int wid = (int)(drawing.getWidth()/2.0);
        final int ht = (int)(drawing.getHeight()/2.0);
                
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
                                              ht);
                      
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
        futures = new ArrayList<>();
        threadpool = Executors.newFixedThreadPool(4);
        palette = new Color[256];
        for(int idx = 0; idx < 256; ++idx) {
            palette[idx] = Color.grayRgb(idx);
        }
        drawScene();
    }    

    @Override
    public void close() throws Exception {
        if(threadpool != null) {
            threadpool.shutdown();
        }
    }
    
}
