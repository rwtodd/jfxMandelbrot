/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rwt.mandelbrot.algo;

import rwt.mandelbrot.PixelSupplier;

/**
 *
 * @author richa
 */
public final class JuliaExpSet implements PixelSupplier  {
    final double addX;
    final double addY;
    
    public JuliaExpSet(double xloc, double yloc) {
        addX = xloc;
        addY = yloc;
    }
    
    @Override
    public int colorPixel(final double x, final double y) {
       int answer = 255;
       double  cx = x;
       double  cy = y;
       while(( cx*cx+cy*cy < 4.0) && (answer > 0)) {
          final double tmp  = Math.exp(cx);
          cx = tmp * Math.cos(cy) + addX;
          cy = tmp * Math.sin(cy) + addY;
          --answer; 
       }
       return answer; 
    }

}
