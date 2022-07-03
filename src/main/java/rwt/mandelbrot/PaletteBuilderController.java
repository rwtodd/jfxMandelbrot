/*
 * Copyright Richard Todd. I put the code under the
 * GPL v2.0.  See the LICENSE file in the repository.
 * for more information.
 */
package rwt.mandelbrot;

import org.rwtodd.paldesign.PaletteDesigner;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.fxml.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


/**
 * FXML Controller class
 *
 * @author richard Todd
 */
public class PaletteBuilderController implements Initializable {

    @FXML private PaletteDesigner designer;
    
    private Stage myStage;
    private ObjectProperty<Color[]> fromParent;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // nothing to do at the moment.
    }        
    
    public void tieToParent(Stage whereIAm, ObjectProperty<Color[]> tgt) {
        myStage = whereIAm;
        fromParent = tgt;
        designer.setPalette(Optional.empty(), tgt.get());
    }
    
    @FXML private void btnApply(ActionEvent ae) {
        fromParent.set(designer.getPaletteColors());
    }
    
    @FXML private void btnClose(ActionEvent ae) {
        btnApply(ae);
        myStage.close();
    }

}
