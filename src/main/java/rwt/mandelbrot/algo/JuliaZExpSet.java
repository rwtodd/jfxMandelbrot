/*
 * Copyright Richard Todd. I put the code under the
 * GPL v2.0.  See the LICENSE file in the repository.
 * for more information.
 */
package rwt.mandelbrot.algo;

/**
 * Z * e^Z + C  Julia ZExp set
 * @author richa
 */
public final class JuliaZExpSet extends SimpleAlgo {
    final double addX;
    final double addY;
    final int depth;
    final double escape;
    
    public JuliaZExpSet(double xloc, double yloc, int d, double e) {
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
          final double expr = tmp*Math.cos(cy);
          final double expi = tmp*Math.sin(cy);
          final double oldcx = cx;
          // cx cy   ecx   ecy
          //  cx*ecx - cy*ecy real   cx*ecy + cy*ecx
          cx = (oldcx*expr) - (cy*expi) + addX;
          cy = (oldcx*expi) + (cy*expr) + addY;
          --answer; 
       }
       
       // scale answer to the 0 - paletteSize range...
       answer = (int)((double)paletteSize * answer / depth);
       if(answer >= paletteSize) answer = (paletteSize-1);

       return answer; 
    }

}
