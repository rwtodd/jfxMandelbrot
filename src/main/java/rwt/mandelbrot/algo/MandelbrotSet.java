/*
 * Copyright Richard Todd. I put the code under the
 * GPL v2.0.  See the LICENSE file in the repository.
 * for more information.
 */
package rwt.mandelbrot.algo;

/**
 * Implements the standard mandelbrot set:  x^2 + location.
 * @author Richard Todd
 */
public final class MandelbrotSet extends SimpleAlgo {
    private final int depth;
    private final double escape;
    
    @Override
    public int colorPixel(final double x, final double y) {
       int answer = depth - 1;
       double  cx = x;
       double  cy = y;
       while(( cx*cx+cy*cy < escape) && (answer > 0)) {
          final double tmp  = cx*cy;
          cx = cx*cx - cy*cy + x;
          cy = tmp+tmp + y;
          --answer; 
       }
       
       // scale answer to the 0 - paletteSize range...
       answer = (int)((double)paletteSize * answer / depth);
       if(answer >= paletteSize) answer = (paletteSize-1);
       
       return answer; 
    }
   
    public MandelbrotSet(int d, double e) {
        if(d < 1) d = 255;
        depth = d;
        escape = e;
    } 
}
