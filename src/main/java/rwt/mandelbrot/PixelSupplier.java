/*
 * Copyright Richard Todd. I put the code under the
 * GPL v2.0.  See the LICENSE file in the repository.
 * for more information.
 */
package rwt.mandelbrot;

/**
 *
 * @author richa
 */
public interface PixelSupplier {
    int colorPixel(final double x, final double y);
    void setColorDepth(int d);
}
