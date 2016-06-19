/*
 * Copyright Richard Todd. I put the code under the
 * GPL v2.0.  See the LICENSE file in the repository.
 * for more information.
 */
package rwt.mandelbrot.algo;

import rwt.mandelbrot.PixelSupplier;

/**
 * Implements the standard mandelbrot set:  x^2 + location.
 * @author Richard Todd
 */
public final class MandelbrotSet implements PixelSupplier {

    @Override
    public int colorPixel(final double x, final double y) {
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
    
}
