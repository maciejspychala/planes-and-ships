package models.vehicles;

import interfaces.Passengers;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import models.Human;
import models.PassengerModule;
import models.WorldHelper;
import models.buildings.Building;
import models.buildings.CivilAirport;

import java.util.ArrayList;

/**
 * Created by Maciej on 2015-10-12.
 */
public class PassengerPlane extends Plane implements Passengers {
    private PassengerModule passengerModule = new PassengerModule();

    public PassengerPlane(Pane pane) {
        super();
    }

    @Override
    public Image getImage() {
        return WorldHelper.getPlaneImage().getImage();
    }

    @Override
    public boolean getOnBoard(Human human) {
        return passengerModule.addPassenger(human);
    }

    @Override
    public void unloadPassengers(Building building) {
        passengerModule.unloadPassengers(building);
    }

    @Override
    public ArrayList<Human> getPassangers() {
        return passengerModule.getPassengers();
    }

    @Override
    public int getMaxCapacity() {
        return passengerModule.getMaxPassengers();
    }

    @Override
    public void movePassanger(Human human, Building building) {

    }

    @Override
    public Class<?> getWhichClass() {
        return CivilAirport.class;
    }
}

