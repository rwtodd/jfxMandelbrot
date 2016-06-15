/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rwt.mandelbrot;

import rwt.mandelbrot.algo.*;
import javafx.beans.Observable;
import javafx.util.converter.NumberStringConverter;
import javafx.beans.property.StringProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * FXML Controller class for the Select Fractal Dialog
 *
 * @author Rchard Todd
 */
public class AlgorithmSelector {

    private PixelSupplier cached;
    
    private final StringProperty fractal;
    private final DoubleProperty param1;
    private final DoubleProperty param2;
    
    private synchronized void invalidateCache(Observable ob, Object o, Object n) {
        cached = null;
    }
   
    public AlgorithmSelector(Property<String> choice, Property<String> arg1, Property<String> arg2) {
        cached = null;
        fractal = new SimpleStringProperty();
        param1 = new SimpleDoubleProperty();
        param2 = new SimpleDoubleProperty();
        
        fractal.bindBidirectional(choice);
        Bindings.bindBidirectional(arg1,param1, new NumberStringConverter());
        Bindings.bindBidirectional(arg2,param2, new NumberStringConverter());
        
        fractal.addListener(this::invalidateCache);
        param1.addListener(this::invalidateCache);
        param2.addListener(this::invalidateCache);
        
    }
    
    public synchronized PixelSupplier getSupplier() {
        if(cached != null) return cached;
                
        switch(fractal.getValue()) {
            case "Julia Squared Set":
                cached = new JuliaSquaredSet(param1.get(), param2.get());
                break;
            case "Julia Exp Set":
                cached = new JuliaExpSet(param1.get(), param2.get());
                break;
            case "Mandelbrot VarDepth":
                cached = new MandelVarDepth((int)param1.get());
                break;
            default:
                cached = new MandelbrotSet();
                break;
        }
        
        return cached;
    }
    
}
