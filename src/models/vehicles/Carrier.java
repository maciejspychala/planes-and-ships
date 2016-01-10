package models.vehicles;

import interfaces.MakeNewVehicle;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import models.Coords;
import models.GpsRandom;
import models.WorldHelper;
import models.buildings.Building;
import models.buildings.Harbour;
import models.buildings.MilitaryAirport;

import java.util.ArrayList;

/**
 * Created by Maciej on 2015-10-17.
 */
public class Carrier extends Ship implements MakeNewVehicle {
    private String weapon;

    public Carrier() {
        super();
        weapon = WorldHelper.getRandomWeapon();
        setDelay(0);
        setGps(new GpsRandom(this));
    }

    @Override
    public Image getImage() {
        return WorldHelper.getCarrierImage().getImage();
    }

    @Override
    public BaseVehicle make(Pane pane) {
        MilitaryPlane vehicle = new MilitaryPlane(pane);
        vehicle.setRoute(WorldHelper.getRandRoute(MilitaryAirport.class, null));
        vehicle.setCoords(new Coords(getCoords()));
        vehicle.setState(WorldHelper.VehicleState.ALARMA);
        vehicle.setWeapon(weapon);
        vehicle.draw(pane);

        setDelay(0);
        return vehicle;
    }

    @Override
    public ArrayList<HBox> getInfo() {
        ArrayList<HBox> hBoxes = super.getInfo();
        hBoxes.add(WorldHelper.createHBox("Typ Broni", weapon));
        return hBoxes;
    }

    @Override
    public ArrayList<Building> getRoute() {
        ArrayList<Building> newRoute = new ArrayList<>();
        newRoute.add(super.getRoute().get(0));
        newRoute.add(super.getRoute().get(1));
        return newRoute;
    }

    @Override
    public Class<?> getWhichClass() {
        return Harbour.class;
    }
}
