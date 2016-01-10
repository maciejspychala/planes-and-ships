package models.buildings;

import interfaces.MakeCarrier;
import interfaces.MakeNewVehicle;
import interfaces.Passengers;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import models.Coords;
import models.Human;
import models.WorldHelper;
import models.vehicles.BaseVehicle;
import models.vehicles.Carrier;
import models.vehicles.PassengerShip;

import java.util.ArrayList;

/**
 * Created by Maciej on 2015-10-18.
 */
public class Harbour extends Building implements MakeNewVehicle, Passengers, MakeCarrier {


    public Harbour(double x, double y, String name) {
        super(x, y, name);
        setCapacity(4);
        setWhichTypeOfVehicleCouldIStore("łodzi");
    }

    @Override
    public Image getImage() {
        return WorldHelper.getHarborImage().getImage();
    }

    @Override
    public BaseVehicle make(Pane pane) {
        BaseVehicle vehicle = new PassengerShip();
        vehicle.setRoute(WorldHelper.getRandRoute(Harbour.class, this));
        vehicle.setCoords(new Coords(getCoords()));
        vehicle.draw(pane);
        return vehicle;
    }

    @Override
    public BaseVehicle makeNewCarrier(Pane pane) {
        BaseVehicle vehicle = new Carrier();
        vehicle.setRoute(WorldHelper.getRandRoute(Harbour.class, this));
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
