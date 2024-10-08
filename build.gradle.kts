plugins {
  java
  application
  id("org.openjfx.javafxplugin") version "0.1.0"
  // id 'org.beryx.jlink' version '2.25.0'
}

repositories {
        mavenCentral()
}

version = "1.0.0"
group = "org.rwtodd"


dependencies {
    implementation("org.rwtodd:org.rwtodd.paldesign:1.0.0")
}


tasks.withType<JavaCompile>().configureEach {
    options.release = 21
}

javafx {
    version = "23"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.swing")
}


application {
    mainModule = "rwt.mandelbrot"
    mainClass = "rwt.mandelbrot.JFXMandelbrot"
}

// jlink {
//    options = ['--strip-debug', '--compress', '2', '--no-header-files', '--no-man-pages']
//    launcher {
//        name = 'jfxmandel'
//    }
// }
