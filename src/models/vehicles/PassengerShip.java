package models.vehicles;

import interfaces.Passengers;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import models.Human;
import models.PassengerModule;
import models.WorldHelper;
import models.buildings.Building;
import models.buildings.Harbour;

import java.util.ArrayList;

/**
 * Created by Maciej on 2015-10-17.
 */
public class PassengerShip extends Ship implements Passengers{
    private String owner;
    private PassengerModule passengerModule = new PassengerModule();

    public PassengerShip() {
        super();
        owner = WorldHelper.getRandomCompany();
    }

    @Override
    public Image getImage() {
        return WorldHelper.getBoatImage().getImage();
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
    public ArrayList<HBox> getInfo() {
        ArrayList<HBox> hBoxes = super.getInfo();
        hBoxes.add(WorldHelper.createHBox("właściciel", owner));
        return hBoxes;
    }

    @Override
    public Class<?> getWhichClass() {
        return Harbour.class;
    }

}
