package models;

import interfaces.Display;
import interfaces.Passengers;
import interfaces.Route;
import javafx.scene.layout.HBox;
import models.buildings.Building;
import models.vehicles.BaseVehicle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Maciej on 2015-10-12.
 */
public class Human implements Runnable, Display, Route, Serializable {
    private boolean run = true;
    private String name;
    private String surname;
    private String pesel;
    private boolean shouldRefresh = false;
    private boolean first = false;
    private ArrayList<Building> route = new ArrayList<>();
    private ArrayList<Building> routeToBe = new ArrayList<>();
    private WorldHelper.HumanState state = WorldHelper.HumanState.IN_PORT;
    private int delay = 5000;
    private boolean exitNow = false;
    private int tourist = new Random().nextInt(1) + 1 ;

    public Human(ArrayList<Building> buildings) {
        route.addAll(buildings);
        route.get(0).addHuman(this);
        name = WorldHelper.getRandomName();
        surname = WorldHelper.getRandomSurname();
        pesel = Integer.toString(new Random().nextInt(400000) + 600000) + Integer.toString(new Random().nextInt(100000));
    }

    @Override
    public void changeRoute(ArrayList<Building> route) {
        routeToBe = route;
        exitNow = true;
    }

    public ArrayList<Building> getRoute() {
        return route;
    }

    @Override
    public Class<?> getWhichClass() {
        return Building.class;
    }


    @Override
    public void run() {
        while (run) {
            switch (state) {
                case IN_PORT:
                    if(first){
                        try {
                            Thread.sleep(delay*tourist);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        first = false;
                    }
                    if (exitNow) {
                        route = routeToBe;
                        exitNow = false;
                    }
                    if (!route.get(0).getClass().isInstance(route.get(1))) {
                        ((Passengers) route.get(0)).movePassanger(this, route.get(1));
                        shouldRefresh=true;
                        route.add(route.get(0));
                        route.remove(0);

                        try {
                            Thread.sleep(delay*tourist);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        shouldRefresh = true;
                    }
                    for (BaseVehicle vehicle : route.get(0).getVehicles()) {
                        if (vehicle.getRoute().contains(route.get(1))) {
                            if (jumpIn(vehicle)) {
                                first = true;
                                state = WorldHelper.HumanState.FLYING;
                                route.get(0).imLeaving(this);
                                break;
                            }
                        }
                    }
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case FLYING:
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    /**if it's possible enter to the vehicle
     * @param vehicle
     * @return
     */
    private boolean jumpIn(BaseVehicle vehicle) {
        if (vehicle instanceof Passengers) {
            shouldRefresh = true;
            return ((Passengers) vehicle).getOnBoard(this);
        } else {
            return false;
        }
    }


    /**check if human should leave in the building
     * @param building
     * @return
     */
    public boolean shouldILeave(Building building) {
        if (building == route.get(1) || exitNow) {
            route.add(route.get(0));
            route.remove(0);

            if (exitNow) {
                route = routeToBe;
                exitNow = false;

            }
            state = WorldHelper.HumanState.IN_PORT;
            first = true;
            shouldRefresh = true;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return name + ' ' + surname;
    }

    /**get info to display in info box
     * @return
     */
    @Override
    public ArrayList<HBox> getInfo() {
        ArrayList<HBox> hBoxes = new ArrayList<>();
        hBoxes.add(WorldHelper.createHBox("imię", name));
        hBoxes.add(WorldHelper.createHBox("nazwisko", surname));
        hBoxes.add(WorldHelper.createHBox("pesel", pesel));
        hBoxes.add(WorldHelper.createHBox("cel", route.get(1).toString()));
        String stateText = state == WorldHelper.HumanState.IN_PORT ? "czekam w " + route.get(0).toString() : "w podróży";
        hBoxes.add(WorldHelper.createHBox("stan", stateText));
        hBoxes.add(WorldHelper.createHBox("podróż", tourist==1 ? "biznesowa" : "turystyczna"));
        return hBoxes;
    }

    @Override
    public ArrayList<HBox> getNoRefresh() {
        return new ArrayList<>();
    }

    @Override
    public boolean shouldRefreshInfoBox() {
        if (shouldRefresh) {
            shouldRefresh = false;
            return true;
        }
        return false;
    }

    public void setRun(boolean run) {
        this.run = run;
    }

    @Override
    public void setShouldRefreshInfoBox(boolean shouldRefreshInfoBox) {
        shouldRefresh = shouldRefreshInfoBox;
    }
}
