/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rwt.mandelbrot;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

/**
 * FXML Controller class for the Select Fractal Dialog
 *
 * @author Rchard Todd
 */
public class SelectFractalController implements Initializable {

    @FXML
    private javafx.scene.control.ChoiceBox<String> which;
    
    @FXML
    private javafx.scene.control.TextField arg1;

    @FXML
    private javafx.scene.control.TextField arg2;

    private Stage myStage;
    
    void setStage(Stage s) { myStage = s; }
    
    
    @FXML
    private void okBtn(ActionEvent event) {
        if(myStage != null) {
            myStage.close();
        }
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    public PixelSupplier createSet() {
        PixelSupplier answer = null;
        double arg1d = 0.0;
        double arg2d = 0.0;
        
        try {
           arg1d = Double.valueOf(arg1.getText());
           arg2d = Double.valueOf(arg2.getText());
        } catch(Exception e) {
            e.printStackTrace();;
        }
        
        switch(which.getValue()) {
            case "Julia Squared Set":
                answer = new JuliaSquaredSet(arg1d, arg2d);
                break;
            case "Julia Exp Set":
                answer = new JuliaExpSet(arg1d, arg2d);
                break;
            default:
                answer = new MandelbrotSet();
                break;
        }
        
        return answer;
    }
    
}
