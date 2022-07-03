module rwt.mandelbrot {
   requires java.desktop;
   requires javafx.fxml;
   requires javafx.controls;
   requires javafx.swing;
   requires transitive javafx.graphics;

   requires org.rwtodd.paldesign;
   opens rwt.mandelbrot to javafx.fxml;
   exports rwt.mandelbrot to javafx.graphics;
}
