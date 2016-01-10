package models.buildings;

import interfaces.Display;
import interfaces.Draw;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import models.Connection;
import models.Coords;
import models.Human;
import models.WorldHelper;
import models.vehicles.BaseVehicle;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Maciej on 2015-10-12.
 */
public abstract class Building implements Draw, Display, Serializable {
    private final Coords coords;
    private int capacity = 1;
    private String name;
    private ArrayList<BaseVehicle> vehicles = new ArrayList<>();
    private ArrayList<BaseVehicle> blocked = new ArrayList<>();
    private ArrayList<Connection> connections = new ArrayList<>();
    private ArrayList<Building> neighbors = new ArrayList<>();
    private ArrayList<Building> route = new ArrayList<>();
    private ArrayList<Human> people = new ArrayList<>();
    private double distance = 0;
    private Coords center;
    private transient ImageView imageView;
    private boolean visited = false;
    private boolean shouldRefresh = false;
    private String whatDoIHave = "";

    public Building(double x, double y, String name) {
        this.coords = new Coords(x, y);
        this.center = new Coords(x + WorldHelper.BUILDING_RADIOUS, y + WorldHelper.BUILDING_RADIOUS);
        this.name = name;
    }

    public Coords getCoords() {
        return coords;
    }

    public synchronized ArrayList<BaseVehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(ArrayList<BaseVehicle> vehicles) {
        this.vehicles = vehicles;
    }

    public ArrayList<Connection> getConnections() {
        return connections;
    }

    public Coords getCenter() {
        return center;
    }

    public ArrayList<Building> getNeighbors() {
        return neighbors;
    }

    private boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    private ArrayList<Building> getRoute() {
        return route;
    }

    public void setRoute(ArrayList<Building> route) {
        this.route = route;
    }

    private double getDistance() {
        return distance;
    }

    private void setDistance(double distance) {
        this.distance = distance;
    }

    /**Calculate route from itself to destination
     * @param destination
     * @return
     */
    public synchronized ArrayList<Building> getRouteTo(Building destination) {
        setVisited(true);
        double min = 9999999.0d;
        double distanceToBe = 9999999.0d;
        if (this != destination) {
            for (Building building : neighbors) {
                if (!building.isVisited()) {
                    building.getRouteTo(destination);
                    if (building.getDistance() < min) {
                        route = building.getRoute();
                        distanceToBe = building.getDistance() + WorldHelper.distanceBetween(this.getCoords(), building.getCoords());
                    }
                }
            }
        } else {
            distanceToBe = 0.0d;
        }
        this.setDistance(distanceToBe);
        route.add(0, this);
        return route;
    }

    @Override
    public String toString() {
        return name;
    }

    public Coords getCenterCoords() {
        Coords centerCords = new Coords(coords);
        centerCords.setX(centerCords.getX() + WorldHelper.BUILDING_RADIOUS);
        centerCords.setY(centerCords.getY() + WorldHelper.BUILDING_RADIOUS);
        return centerCords;
    }

    /**Check if vehicle could enter this building, if yes, enter.
     * @param baseVehicle
     * @return
     */
    public synchronized boolean couldIEnter(BaseVehicle baseVehicle) {
        if (blocked.size() < capacity) {
            blocked.add(baseVehicle);
            return true;
        }
        return false;
    }

    public synchronized void unblock(BaseVehicle baseVehicle) {
        blocked.remove(baseVehicle);
    }

    public ArrayList<Human> getPeople() {
        return people;
    }

    /**remove human from this building
     * @param human
     */
    public void imLeaving(Human human) {
        people.remove(human);
        shouldRefresh = true;
    }

    public void addHuman(Human human) {
        people.add(human);
        shouldRefresh = true;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public void draw(Pane pane) {
        this.imageView = new ImageView(this.getImage());
        this.imageView.setX(this.getCoords().getX());
        this.imageView.setY(this.getCoords().getY());
        imageView.setOnMouseClicked(event -> WorldHelper.setObjectToDisplay(Building.this));
        Text text = new Text(getCoords().getX(), getCoords().getY() - 3, name);
        text.setFont(Font.font(java.awt.Font.MONOSPACED, 10));
        pane.getChildren().add(imageView);
        pane.getChildren().add(text);
    }

    @Override
    public ArrayList<HBox> getInfo() {
        ArrayList<HBox> hBoxes = new ArrayList<>();
        hBoxes.add(WorldHelper.createHBox("nazwa", name));
        hBoxes.add(WorldHelper.createHBox("X", (int) getCoords().getX()));
        hBoxes.add(WorldHelper.createHBox("Y", (int) getCoords().getY()));
        if (!(this instanceof Crossing)) {
            hBoxes.add(WorldHelper.createHBox(whatDoIHave, vehicles.size()));
        }
        return hBoxes;
    }

    @Override
    public ArrayList<HBox> getNoRefresh() {
        return new ArrayList<>();
    }

    public void setWhichTypeOfVehicleCouldIStore(String whatDoIHave) {
        this.whatDoIHave = whatDoIHave;
    }

    @Override
    public boolean shouldRefreshInfoBox() {
        if(shouldRefresh){
            shouldRefresh = false;
            return true;
        }
        return false;
    }

    @Override
    public void relocate() {
    }
}
