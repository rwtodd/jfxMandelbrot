/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rwt.mandelbrot.algo;
import rwt.mandelbrot.PixelSupplier;

/**
 * Implements the standard mandlebrot set:  x^2 + location.
 * The number of iterations (depth) is variable in this one.
 * @author Richard Todd
 */
public final class MandelVarDepth implements PixelSupplier {
    private final int depth;
    
    @Override
    public int colorPixel(final double x, final double y) {
       int answer = depth - 1;
       double  cx = x;
       double  cy = y;
       while(( cx*cx+cy*cy < 4.0) && (answer > 0)) {
          final double tmp  = cx*cy;
          cx = cx*cx - cy*cy + x;
          cy = tmp+tmp + y;
          --answer; 
       }
       // scale answer to the 0 - 255 range...
       answer = (int)(256.0 * answer / depth);
       if(answer > 255) answer = 255;
       
       return answer; 
    }
   
    public MandelVarDepth(int d) {
        if(d < 1) d = 255;
        depth = d;
    }
}
