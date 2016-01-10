package interfaces;

import javafx.scene.layout.Pane;
import models.vehicles.BaseVehicle;

/**
 * Created by Maciej on 06.01.16.
 */
public interface MakeNewVehicle {
    BaseVehicle make(Pane pane);
}
