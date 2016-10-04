/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rwt.mandelbrot.algo;

import rwt.mandelbrot.PixelSupplier;

/**
 * A base class to help with implementations of PixelSupplier
 * @author rtodd
 */
public abstract class SimpleAlgo implements PixelSupplier {
    
    protected int paletteSize;
    
    @Override
    public void setColorDepth(int d) {
        paletteSize = d;
    }
    
}
