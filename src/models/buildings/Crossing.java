package models.buildings;

import javafx.scene.image.Image;
import models.WorldHelper;

/**
 * Created by Maciej on 2015-10-19.
 */
public abstract class Crossing extends Building {

    public Crossing(double x, double y, String name) {
        super(x, y, name);
    }


    @Override
    public Image getImage() {
        return WorldHelper.getCrossingImage().getImage();
    }
}
