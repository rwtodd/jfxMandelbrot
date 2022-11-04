/*
 * Copyright Richard Todd. I put the code under the
 * GPL v2.0.  See the LICENSE file in the repository.
 * for more information.
 */
package rwt.mandelbrot;

import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import rwt.mandelbrot.algo.JuliaExpSet;
import rwt.mandelbrot.algo.JuliaSquaredSet;
import rwt.mandelbrot.algo.JuliaZExpSet;
import rwt.mandelbrot.algo.MandelbrotSet;

/**
 * FXML Controller class for the Select Fractal Dialog
 *
 * @author Rchard Todd
 */
public class AlgorithmSelector {

    private PixelSupplier cached;
    
    final StringProperty fractal;
    final DoubleProperty param1;
    final DoubleProperty param2;
    final IntegerProperty depth; 
    final DoubleProperty escape;
    
    private synchronized void invalidateCache(Observable ob, Object o, Object n) {
        cached = null;
    }
   
    public AlgorithmSelector() {
        cached = null;
        fractal = new SimpleStringProperty("Mandelbrot Set");
        param1 = new SimpleDoubleProperty(0.0);
        param2 = new SimpleDoubleProperty(0.0);
        depth = new SimpleIntegerProperty(256);
        escape = new SimpleDoubleProperty(4.0);
        
        fractal.addListener(this::invalidateCache);
        param1.addListener(this::invalidateCache);
        param2.addListener(this::invalidateCache);
        depth.addListener(this::invalidateCache);
        escape.addListener(this::invalidateCache);
    }
    
    public synchronized PixelSupplier getSupplier() {
        if(cached != null) return cached;

        cached = switch (fractal.getValue()) {
            case "Julia Squared Set" -> new JuliaSquaredSet(param1.get(), param2.get(), depth.get(), escape.get());
            case "Julia ExpZ Set" -> new JuliaExpSet(param1.get(), param2.get(), depth.get(), escape.get());
            case "Julia Z*ExpZ Set" -> new JuliaZExpSet(param1.get(), param2.get(), depth.get(), escape.get());
            default -> new MandelbrotSet(depth.get(), escape.get());
        };
        
        return cached;
    }
    
}
