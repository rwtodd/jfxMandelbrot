/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rwt.mandelbrot;

/**
 *
 * @author richa
 */
@FunctionalInterface
public interface PixelSupplier {
    int colorPixel(final double x, final double y);
}
