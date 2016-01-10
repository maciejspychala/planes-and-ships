package models.vehicles;

import interfaces.Display;
import interfaces.Draw;
import interfaces.Passengers;
import interfaces.Route;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import models.Coords;
import models.Gps;
import models.WorldHelper;
import models.buildings.Building;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public abstract class BaseVehicle implements Draw, Runnable, Display, Route, Serializable {
    private int id = WorldHelper.getID();
    private int delay = new Random().nextInt(3000) + 3000;
    private WorldHelper.VehicleState state = WorldHelper.VehicleState.TAKE_OFF;
    private Gps gps = new Gps(this);
    private boolean askIfCouldLand = true;
    private boolean shouldUnblock = false;

    private double angle;
    private double xSign;
    private double ySign;
    private double currentXMovement;
    private double currentYMovement;
    private double velocity = 0.5 + Math.random();
    private transient ImageView imageView;
    private boolean run = false;
    private boolean shouldRefresh = false;

    protected double getVelocity() {
        return velocity;
    }

    public Coords getCoords() {
        return gps.getCoords();
    }

    public void setCoords(Coords coords) {
        this.gps.setCoords(new Coords(coords));
    }


    private void live() throws InterruptedException {
        switch (state) {
            case IN_AIR:
                move();
                Thread.sleep(WorldHelper.SLEEP);
                break;
            case CROSSING:
                gps.crossing();
                calculateEstimatedDirection();
                setState(WorldHelper.VehicleState.IN_AIR);
                break;
            case LAND:
                land(gps.getCurrentBuilding());
                setState(WorldHelper.VehicleState.WAIT);
                shouldRefresh = true;
                break;
            case TAKE_OFF:
                gps.prepareToStart();
                calculateEstimatedDirection();
                setState(WorldHelper.VehicleState.IN_AIR);
                gps.getCurrentBuilding().getVehicles().remove(this);
                shouldRefresh = true;
                break;
            case WAIT:
                Thread.sleep(delay);
                setState(WorldHelper.VehicleState.TAKE_OFF);
                break;
            case DELETE:
                Thread.sleep(WorldHelper.SLEEP);
                break;
            case TO_THE_RESCUE:
                calculateNextPosition();
                checkIfArrived();
                Thread.sleep(WorldHelper.SLEEP);
                break;
            case ALARMA:
                gps.searchForNearest();
                calculateEstimatedDirection();
                setState(WorldHelper.VehicleState.TO_THE_RESCUE);
                break;
            case RECOVER:
                gps.recover();
                setState(WorldHelper.VehicleState.TAKE_OFF);
                break;
        }
    }

    private void calculateEstimatedDirection() {
        xSign = gps.getNextX() - gps.getX();
        ySign = gps.getNextY() - gps.getY();
    }

    public void move() {
        if (gps.canIMove()) {
            calculateNextPosition();
            shouldBlockNextBuilding();
            shouldUnblockPreviousBuilding();
            checkIfArrived();
        }
    }

    /**
     * calculate where vehicle should be on the next frame
     */
    public void calculateNextPosition() {
        angle = Math.atan2(gps.getNextX() - this.gps.getX(), ((gps.getNextY() - WorldHelper.HEIGHT) * -1) -
                ((this.getCoords().getY() - WorldHelper.HEIGHT) * -1));
        angle -= Math.PI / 2;
        currentXMovement = velocity * Math.cos(angle);
        currentYMovement = velocity * Math.sin(angle);

        this.getCoords().setX(this.getCoords().getX() + currentXMovement);
        this.getCoords().setY(this.getCoords().getY() + currentYMovement);
    }

    private void shouldBlockNextBuilding() {
        if (WorldHelper.distanceBetween(getCoords(), gps.getNextTransitBuilding().getCenterCoords()) < WorldHelper.DISTANCE_TO_BLOCK) {
            while (askIfCouldLand && !gps.getNextTransitBuilding().couldIEnter(this)) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            askIfCouldLand = false;
        } else {
            askIfCouldLand = true;
        }
    }

    private void shouldUnblockPreviousBuilding() {
        if (shouldUnblock && WorldHelper.distanceBetween(getCoords(), gps.getRouteForNextBuilding().get(0).getCenterCoords()) > WorldHelper.DISTANCE_TO_BLOCK) {
            shouldUnblock = false;
            gps.getRouteForNextBuilding().get(0).unblock(this);
        }
    }

    private void checkIfArrived() {
        if (currentXMovement * xSign <= 0 || currentYMovement * ySign <= 0) {
            gps.arrived();
            shouldUnblock = true;
        }
    }

    protected void setDelay(int delay) {
        this.delay = delay;
    }

    private ImageView getImageView() {
        return imageView;
    }

    public double getAngle() {
        return angle;
    }

    public void setState(WorldHelper.VehicleState state) {
        this.state = state;
    }

    public void setRoute(ArrayList<Building> buildings) {
        gps.setRoute(buildings);
    }

    /**
     * land in the building and unload passengers
     *
     * @param building
     */
    public void land(Building building) {
        building.getVehicles().add(this);
        if (this instanceof Passengers) {
            ((Passengers) this).unloadPassengers(gps.getCurrentBuilding());
        }
    }

    public ArrayList<Building> getRoute() {
        return gps.getRoute();
    }

    public void setRoute(Building... buildings) {
        for (Building building : buildings) {
            gps.getRoute().add(building);
        }
    }

    @Override
    public void run() {
        while (!run) {
            try {
                this.live();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void relocate() {
        this.getImageView().setX(this.getCoords().getX() - WorldHelper.VEHICLE_RADIOUS);
        this.getImageView().setY(this.getCoords().getY() - WorldHelper.VEHICLE_RADIOUS);
        this.getImageView().setRotate(Math.toDegrees(getAngle()));
    }

    @Override
    public ArrayList<HBox> getInfo() {
        ArrayList<HBox> hBoxes = new ArrayList<>();
        hBoxes.add(WorldHelper.createHBox("prędkość max", Integer.toString((int) (velocity * 100))));
        hBoxes.add(WorldHelper.createHBox("X", (int) getCoords().getX()));
        hBoxes.add(WorldHelper.createHBox("Y", (int) getCoords().getY()));
        hBoxes.add(WorldHelper.createHBox("cel", gps.getNextStopBuilding().toString()));
        hBoxes.add(WorldHelper.createHBox("id", id));
        if (this instanceof Passengers) {
            hBoxes.add(WorldHelper.createHBox("Max pasażerów", ((Passengers) this).getMaxCapacity()));
            hBoxes.add(WorldHelper.createHBox("Aktualnie pasażerów", ((Passengers) this).getPassangers().size()));
        }
        return hBoxes;
    }

    @Override
    public ArrayList<HBox> getNoRefresh() {
        ArrayList<HBox> hBoxes = new ArrayList<>();
        hBoxes.add(new HBox(eraseButton()));
        hBoxes.add(new HBox(alarmaButton()));
        return hBoxes;
    }

    private Button alarmaButton() {
        Button button = new Button("ALARMA!");
        button.setOnAction(event -> alarma());
        return button;
    }

    private Button eraseButton() {
        Button button = new Button("usuń");
        button.setOnAction(event -> erase());
        return button;
    }

    /**
     * delete vehicle from the world
     */
    private void erase() {
        setState(WorldHelper.VehicleState.DELETE);
        WorldHelper.deleteObjectToDisplay();
        if (gps.getRouteForNextBuilding().size() > 1) {
            gps.getRouteForNextBuilding().get(0).unblock(this);
            gps.getRouteForNextBuilding().get(1).unblock(this);
        }
        gps.getCurrentConnection().removeVehicle(gps.getRouteForNextBuilding().get(0), this);
        gps.getCurrentBuilding().unblock(this);
        gps.getCurrentBuilding().getVehicles().remove(this);
        gps.getNextStopBuilding().unblock(this);
        gps.getNextStopBuilding().getVehicles().remove(this);
        WorldHelper.getVehicles().remove(this);
        imageView.resize(0, 0);
        imageView.relocate(1000, 1000);
        gps = null;
        run = true;
    }

    public void changeRoute(ArrayList<Building> route) {
        gps.setRouteToBe(route);
    }

    private void alarma() {
        if (gps.getRouteForNextBuilding().size() > 1) {
            gps.getRouteForNextBuilding().get(0).unblock(this);
            gps.getRouteForNextBuilding().get(1).unblock(this);
        }
        gps.getCurrentConnection().removeVehicle(gps.getRouteForNextBuilding().get(0), this);
        gps.getCurrentBuilding().unblock(this);
        gps.getCurrentBuilding().getVehicles().remove(this);
        gps.getNextStopBuilding().unblock(this);
        gps.getNextStopBuilding().getVehicles().remove(this);
        setState(WorldHelper.VehicleState.ALARMA);
    }

    public boolean itWasAlarm() {
        return state == WorldHelper.VehicleState.TO_THE_RESCUE;
    }

    @Override
    public boolean shouldRefreshInfoBox() {
        if (shouldRefresh) {
            shouldRefresh = false;
            return true;
        }
        return false;
    }

    protected void setGps(Gps gps) {
        this.gps = gps;
    }

    @Override
    public void draw(Pane pane) {
        this.imageView = new ImageView(this.getImage());
        imageView.setOnMouseClicked(event -> WorldHelper.setObjectToDisplay(BaseVehicle.this));
        this.imageView.setX(this.getCoords().getX() - WorldHelper.VEHICLE_RADIOUS);
        this.imageView.setY(this.getCoords().getY() - WorldHelper.VEHICLE_RADIOUS);
        pane.getChildren().add(imageView);
    }

    public void setRun(boolean run) {
        this.run = run;
    }

    @Override
    public void setShouldRefreshInfoBox(boolean shouldRefreshInfoBox) {
        shouldRefresh = shouldRefreshInfoBox;
    }
}