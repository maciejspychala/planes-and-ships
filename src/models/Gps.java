package models;

import models.buildings.Building;
import models.vehicles.BaseVehicle;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Maciej on 28.12.15.
 */
public class Gps implements Serializable {
    private BaseVehicle vehicle;
    private Coords coords;
    private Coords nextStop;
    private Connection currentConnection;
    private ArrayList<Building> route = new ArrayList<>();
    private ArrayList<Building> routeToBe = new ArrayList<>();
    private ArrayList<Building> routeForNextBuilding = new ArrayList<>();
    private Building alarmBuilding;
    private boolean changeRoute = false;
    private boolean deleteLast = false;

    public Gps(BaseVehicle vehicle) {
        this.vehicle = vehicle;
    }

    /**
     * do all things necessary to take off
     */
    public void prepareToStart() {
        calculateRouteForNextStop();
        setCurrentConnection();
    }

    /**
     * get route to the next destination
     */
    private void calculateRouteForNextStop() {
        routeForNextBuilding.clear();
        WorldHelper.nullifyVisited();
        routeForNextBuilding = WorldHelper.getRoute(route.get(0), route.get(1));
    }

    /**
     * land and check if route has been changed
     */
    protected void land() {
        if (!deleteLast) {
            route.add(route.get(0));
        }
        deleteLast = false;
        route.remove(0);
        if (changeRoute) {
            if (routeToBe.contains(route.get(0))) {
                while (routeToBe.get(0) == route.get(0)) {
                    routeToBe.add(routeToBe.get(0));
                    routeToBe.remove(0);
                }
            } else {
                routeToBe.add(0, route.get(0));
                deleteLast = true;
            }
            route = routeToBe;
            changeRoute = false;
        }
    }


    /**
     * set connection on which vehicle should move
     */
    private void setCurrentConnection() {
        for (Connection connection : routeForNextBuilding.get(0).getConnections()) {
            if (connection.isThis(routeForNextBuilding.get(0), routeForNextBuilding.get(1))) {
                currentConnection = connection;
            }
        }
        coords = new Coords(currentConnection.getStart(routeForNextBuilding.get(0)));
        nextStop = new Coords(currentConnection.getEnd(routeForNextBuilding.get(1)));
        currentConnection.addVehicle(routeForNextBuilding.get(0), vehicle);
    }

    /**
     * check if vehicle reaches destination, if not set next building on route to the destination
     */
    public void arrived() {
        if (!vehicle.itWasAlarm()) {
            currentConnection.removeVehicle(routeForNextBuilding.get(0), vehicle);
            routeForNextBuilding.remove(0);
            if (routeForNextBuilding.size() <= 1) {
                land();
                vehicle.setState(WorldHelper.VehicleState.LAND);
            } else {
                vehicle.setState(WorldHelper.VehicleState.CROSSING);
            }
        } else {
            vehicle.setState(WorldHelper.VehicleState.RECOVER);
        }
    }

    public Coords getCoords() {
        return coords;
    }

    public void setCoords(Coords coords) {
        this.coords = coords;
    }

    public double getX() {
        return coords.getX();
    }

    public double getY() {
        return coords.getY();
    }

    public double getNextX() {
        return nextStop.getX();
    }

    public double getNextY() {
        return nextStop.getY();
    }

    public ArrayList<Building> getRoute() {
        return route;
    }

    public void setRoute(ArrayList<Building> route) {
        this.route = route;
    }

    public Building getNextTransitBuilding() {
        return routeForNextBuilding.get(1);
    }

    public Building getNextStopBuilding() {
        return route.get(1);
    }

    public ArrayList<Building> getRouteForNextBuilding() {
        return routeForNextBuilding;
    }

    public void crossing() {
        setCurrentConnection();
    }

    public Building getCurrentBuilding() {
        return route.get(0);
    }

    public Connection getCurrentConnection() {
        return currentConnection;
    }

    /**check on the current connection if there is some vehicle blocking way
     * @return
     */
    public boolean canIMove() {
        if (routeForNextBuilding.size() > 0 && currentConnection != null)
            return currentConnection.canIMove(routeForNextBuilding.get(0), vehicle);
        return true;
    }

    /**
     * search for the nearest building where vehicle could land
     */
    public void searchForNearest() {
        alarmBuilding = WorldHelper.buildingNearestTo(
                WorldHelper.getAllBuildingsLike(getCurrentBuilding().getClass()),
                getCoords());
        nextStop = alarmBuilding.getCoords();
    }

    /**
     * recover from emergency landing
     */
    public void recover() {
        if (route.contains(alarmBuilding)) {
            while (route.get(0) != alarmBuilding) {
                route.add(route.get(0));
                route.remove(0);
            }
        } else {
            route.add(0, alarmBuilding);
        }
    }

    /**set new route to vehicle
     * @param routeToBe
     */
    public void setRouteToBe(ArrayList<Building> routeToBe) {
        this.routeToBe = routeToBe;
        changeRoute = true;
    }


}
