package models.vehicles;

import javafx.scene.layout.HBox;
import models.WorldHelper;
import models.buildings.Building;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Maciej on 2015-10-12.
 */
public abstract class Plane extends BaseVehicle {
    private int staffNumber = new Random().nextInt(3)+2;
    private double maxFuel = 2500.0d;
    private double fuel = maxFuel;

    public Plane() {
        super();
    }



    private double getFuel() {
        return fuel;
    }

    @Override
    public void land(Building building) {
        super.land(building);
        refill();
    }

    @Override
    public void calculateNextPosition() {
        super.calculateNextPosition();
        fuel -= getVelocity();
    }

    private void refill() {
        fuel = maxFuel;
    }

    @Override
    public ArrayList<HBox> getInfo() {
        ArrayList<HBox> hBoxes = super.getInfo();
        hBoxes.add(new HBox(WorldHelper.createHBox("paliwo", (int) this.getFuel())));
        hBoxes.add(new HBox(WorldHelper.createHBox("liczba personelu", staffNumber)));
        return hBoxes;
    }

}
