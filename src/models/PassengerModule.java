package models;

import models.buildings.Building;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Maciej on 2015-10-12.
 */
public class PassengerModule implements Serializable {
    private int maxPassengers = new Random().nextInt(20) + 10;
    private ArrayList<Human> passengers = new ArrayList<>();

    public int getMaxPassengers() {
        return maxPassengers;
    }

    public ArrayList<Human> getPassengers() {
        return passengers;
    }

    /**unload passengers which current destination is building
     * @param building
     */
    public synchronized void unloadPassengers(Building building) {
        for (int i = passengers.size() - 1; i >= 0; i--) {
            if (passengers.get(i).shouldILeave(building)) {
                building.addHuman(passengers.get(i));
                passengers.remove(i);
            }
        }
    }

    public synchronized boolean addPassenger(Human human) {
        if (passengers.size() >= maxPassengers) {
            return false;
        } else {
            passengers.add(human);
            return true;
        }
    }

}
