package models.buildings;

import javafx.scene.image.Image;
import models.WorldHelper;

/**
 * Created by Maciej on 29.12.15.
 */
public class MilitaryAirport extends Airport {
    public MilitaryAirport(double x, double y, String name) {
        super(x, y, name);
    }

    @Override
    public Image getImage() {
        return WorldHelper.getMilitaryAirportImage().getImage();
    }

}
