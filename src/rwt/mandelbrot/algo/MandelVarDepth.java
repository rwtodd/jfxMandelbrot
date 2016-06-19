/*
 * Copyright Richard Todd. I put the code under the
 * GPL v2.0.  See the LICENSE file in the repository.
 * for more information.
 */
package rwt.mandelbrot.algo;
import rwt.mandelbrot.PixelSupplier;

/**
 * Implements the standard mandelbrot set:  x^2 + location.
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
