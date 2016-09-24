/*
 * Copyright Richard Todd. I put the code under the
 * GPL v2.0.  See the LICENSE file in the repository.
 * for more information.
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
    final int depth;
    final double escape;
    
    public JuliaExpSet(double xloc, double yloc, int d, double e) {
        addX = xloc;
        addY = yloc;
        if(d < 1) d = 255;
        depth = d;
        escape = e;
    }
    
    @Override
    public int colorPixel(final double x, final double y) {
       int answer = depth - 1;
       double  cx = x;
       double  cy = y;
       while(( cx*cx+cy*cy < escape) && (answer > 0)) {
          final double tmp  = Math.exp(cx);
          cx = tmp * Math.cos(cy) + addX;
          cy = tmp * Math.sin(cy) + addY;
          --answer; 
       }
       
       // scale answer to the 0 - 255 range...
       answer = (int)(256.0 * answer / depth);
       if(answer > 255) answer = 255;
       
       return answer; 
    }

}
