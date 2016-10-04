/*
 * Copyright Richard Todd. I put the code under the
 * GPL v2.0.  See the LICENSE file in the repository.
 * for more information.
 */
package rwt.mandelbrot.algo;


/**
 *
 * @author richa
 */
public final class JuliaExpSet extends SimpleAlgo  {
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
       
       // scale answer to the 0 - paletteSize range...
       answer = (int)((double)paletteSize * answer / depth);
       if(answer >= paletteSize) answer = (paletteSize-1);

       
       return answer; 
    }

}
