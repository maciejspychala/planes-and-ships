package interfaces;

import javafx.scene.layout.Pane;
import models.vehicles.BaseVehicle;

/**
 * Created by Maciej on 09.01.2016.
 */
public interface MakeCarrier {
    BaseVehicle makeNewCarrier(Pane pane);
}
