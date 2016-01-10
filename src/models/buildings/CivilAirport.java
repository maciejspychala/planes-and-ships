package models.buildings;

import interfaces.MakeNewVehicle;
import interfaces.Passengers;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import models.Coords;
import models.Human;
import models.WorldHelper;
import models.vehicles.BaseVehicle;
import models.vehicles.PassengerPlane;

import java.util.ArrayList;

/**
 * Created by Maciej on 29.12.15.
 */
public class CivilAirport extends Airport implements MakeNewVehicle, Passengers {
    public CivilAirport(double x, double y, String name) {
        super(x, y, name);
    }

    @Override
    public Image getImage() {
        return WorldHelper.getCivilAirportImage().getImage();
    }

    @Override
    public BaseVehicle make(Pane pane) {
        BaseVehicle vehicle = new PassengerPlane(pane);
        vehicle.setRoute(WorldHelper.getRandRoute(CivilAirport.class, this));
        vehicle.setCoords(new Coords(getCoords()));
        vehicle.draw(pane);
        return vehicle;
    }

    @Override
    public boolean getOnBoard(Human human) {
        return false;
    }

    @Override
    public void unloadPassengers(Building building) {
    }

    @Override
    public ArrayList<Human> getPassangers() {
        return getPeople();
    }

    @Override
    public int getMaxCapacity() {
        return 0;
    }

    @Override
    public void movePassanger(Human human, Building building) {
        getPeople().remove(human);
        building.addHuman(human);
    }
}
