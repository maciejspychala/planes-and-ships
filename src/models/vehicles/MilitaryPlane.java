package models.vehicles;

import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import models.WorldHelper;
import models.buildings.MilitaryAirport;

import java.util.ArrayList;

/**
 * Created by Maciej on 2015-10-17.
 */
public class MilitaryPlane extends Plane {
    private String weapon;

    public MilitaryPlane(Pane pane) {
        super();
    }

    public void setWeapon(String weapon) {
        this.weapon = weapon;
    }

    @Override
    public Image getImage() {
        return WorldHelper.getMilitaryPlaneImage().getImage();
    }

    @Override
    public Class<?> getWhichClass() {
        return MilitaryAirport.class;
    }

    @Override
    public ArrayList<HBox> getInfo() {
        ArrayList<HBox> hBoxes = super.getInfo();
        hBoxes.add(WorldHelper.createHBox("uzbrojenie", weapon));
        return hBoxes;
    }
}
