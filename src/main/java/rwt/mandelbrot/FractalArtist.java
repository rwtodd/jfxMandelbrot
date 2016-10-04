/*
 * Copyright Richard Todd. I put the code under the
 * GPL v2.0.  See the LICENSE file in the repository.
 * for more information.
 */
package rwt.mandelbrot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;


/**
 *
 * @author richa
 */
final class FractalArtist implements AutoCloseable {
    
    final DoubleProperty sceneX;
    final DoubleProperty sceneY;
    final DoubleProperty expanseX;
    final DoubleProperty expanseY;
    final IntegerProperty imgWid;
    final IntegerProperty imgHt;
    final ObjectProperty<Image> image;
    final ObjectProperty<Color[]> palette;

    private final int numThreads;
    
    private final ExecutorService threadpool;
    private final List<Future<?>> futures;
  
    private final AlgorithmSelector selector;
   
    FractalArtist(AlgorithmSelector as) {
        selector = as;
        sceneX = new SimpleDoubleProperty(0.0);
        sceneY = new SimpleDoubleProperty(0.0);
        expanseX = new SimpleDoubleProperty(2.0);
        expanseY = new SimpleDoubleProperty(2.0);
        imgWid = new SimpleIntegerProperty(600);
        imgHt = new SimpleIntegerProperty(600);
        image = new SimpleObjectProperty<>();
        
        numThreads = Runtime.getRuntime().availableProcessors();
        futures = new ArrayList<>();
        threadpool = Executors.newFixedThreadPool(numThreads);
        final Color[] defPalette = new Color[256];
        for(int idx = 0; idx < 256; ++idx) {
            defPalette[idx] = Color.grayRgb(idx);
        }    
        palette = new SimpleObjectProperty<>(defPalette);
        palette.addListener((ob, ov, nv) -> drawScene());
    }
    
    @Override
    public void close() throws Exception {
        if(threadpool != null) {
            threadpool.shutdown();
        }

    }
    
    public enum ZoomOption {
        ZoomIn, ZoomOut, NoZoom
    }    
    
    public void recenter(double x, double y, ZoomOption zo) {
        // determine a new center...
        double pctX = x  / image.getValue().getWidth();
        double pctY = y / image.getValue().getHeight();
        sceneX.set(sceneX.get() + (expanseX.get()*(pctX - 0.5)));
        sceneY.set(sceneY.get() - (expanseY.get()*(pctY - 0.5)));
        
        // now possibly rescale ...
        switch(zo) {
        case ZoomIn:
            expanseX.set(expanseX.get() * 0.5);
            expanseY.set(expanseY.get() * 0.5);
            break;
        case ZoomOut:
            expanseX.set(expanseX.get() * 2.0);
            expanseY.set(expanseY.get() * 2.0);
            break;
        }
    }
    

    public final void reset() {
       sceneX.set(0.0);
       sceneY.set(0.0);
       expanseX.set(2.0);
       expanseY.set(2.0); 
    }
    
     private void drawImagePart(final double ULX, final double expX, 
                                final double ULY, final double expY, 
                                final int wid, final int ht, final int startY, final int endY,
                                final PixelWriter pw, final PixelSupplier ps) {
        final Color[] pal = palette.get();
        for(int y = startY; y < endY; ++y) {
            final double ylevel = ULY - (y/(double)ht)*expY;
            for(int x = 0; x < wid; ++x) {
                int c = ps.colorPixel(ULX + (x/(double)wid)*expX, ylevel);
                pw.setColor(x, y, pal[c]);
            }
        }
    }
    
    
    public final void drawScene() {
        // cancel any work we might be doing...
        for(Future<?> f : futures) {
            f.cancel(true);
        }
        futures.clear();
        
        final double expX = expanseX.get();
        final double expY = expanseY.get();
        final double sceneULX = sceneX.get() - 0.5*expX;
        final double sceneULY = sceneY.get() + 0.5*expY;
        final int wid = imgWid.get(); 
        final int ht = imgHt.get(); 
        final PixelSupplier supplier = selector.getSupplier();
        supplier.setColorDepth(palette.get().length);
        
        final WritableImage newimage = new WritableImage(wid, ht);
        final PixelWriter pw = newimage.getPixelWriter();
         
        final int divSize = ht / numThreads;
        
        final java.util.concurrent.atomic.AtomicInteger ai = new java.util.concurrent.atomic.AtomicInteger(numThreads);
        
        // submit new work...
        for(int div = 0; div <numThreads; div++) {
            final int startline = div*divSize;
            final int endline = (div == (numThreads - 1)) ? ht : (div+1)*divSize;
                      
            Future<?> f = threadpool.submit(() -> {
                      drawImagePart(sceneULX, 
                                              expX,
                                              sceneULY,
                                              expY,
                                              wid,
                                              ht,
                                              startline,
                                              endline,
                                              pw,
                                              supplier);
                      
                      if(ai.decrementAndGet() == 0) {
                          image.set(newimage);
                      }
                });
                futures.add(f);
            }
              
    }
  
}
