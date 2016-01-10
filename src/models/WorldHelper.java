package models;

import interfaces.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import models.buildings.*;
import models.vehicles.BaseVehicle;
import models.vehicles.Carrier;
import models.vehicles.PassengerPlane;
import models.vehicles.PassengerShip;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Maciej on 2015-10-18.
 */
public class WorldHelper {
    public static final int HEIGHT = 600;
    public static final int WIDTH = 800;
    public static final int SLEEP = 16;
    public static final double CONNECTION_DISTANCE = 7.0d;
    public static final double VEHICLE_RADIOUS = 5.0d;
    public static final double BUILDING_RADIOUS = 10.0d;
    public static final int DISTANCE_TO_BLOCK = 30;
    private static ImageView harborImage;
    private static ImageView planeImage;
    private static ImageView civilAirportImage;
    private static ImageView militaryAirportImage;
    private static ImageView crossingImage;
    private static ImageView boatImage;
    private static ImageView carrierImage;
    private static ImageView militaryPlaneImage;
    private static ArrayList<BaseVehicle> vehicles = new ArrayList<>();
    private static ArrayList<Building> buildings = new ArrayList<>();
    private static ArrayList<Human> humans = new ArrayList<>();
    private static ArrayList<Connection> connections = new ArrayList<>();
    private static Object objectToDisplay = null;
    private static VBox boxToRefresh = new VBox(5);
    private static VBox boxNoRefresh = new VBox(5);
    private static VBox boxNoRefreshRoute = new VBox(5);
    private static VBox boxNoRefreshElse = new VBox(5);
    private static boolean refresh;
    private static boolean run = true;
    private static AtomicInteger id = new AtomicInteger(45923);

    private static String companies[] = {"BiEmDablju", "SpaceX", "Bear", "PUT"};
    private static String names[] = {"Maciej", "Bogdan", "Wacław", "Elżbieta", "Agnieszka", "Krzysztof", "Weronika", "Partycja", "Princess Consuela"};
    private static String surnames[] = {"Zając", "Witos", "Krawczyk", "Bananahammock", "Richie"};
    private static String weapons[] = {"JDAM", "Hellfire", "Penguin"};


    public static ArrayList<BaseVehicle> getVehicles() {
        return vehicles;
    }

    public static ArrayList<Building> getBuildings() {
        return buildings;
    }

    /**
     * Drawing world on pane and world starts to live.
     * @param pane pane on which world is being drawn
     *
     */
    public static void generateWorld(Pane pane) {
        onlyAtBeggining(pane);
        everyTime(pane);


    }

    /**Starts all threads, draw save and load button, preparing info boxes
     * @param pane
     */
    private static void everyTime(Pane pane) {
        for (Connection connection : connections) {
            connection.draw(pane);
        }
        for (Building building : buildings) {
            building.draw(pane);
        }

        for (BaseVehicle vehicle : vehicles) {
            vehicle.draw(pane);
            Thread thread = new Thread(vehicle);
            thread.setDaemon(true);
            thread.start();
        }

        for (Human human : humans) {
            Thread thread = new Thread(human);
            thread.setDaemon(true);
            thread.start();
        }

        clearBoxes();
        boxNoRefresh.getChildren().clear();
        boxToRefresh.setLayoutX(480);
        boxToRefresh.setLayoutY(20);
        boxNoRefresh.setLayoutX(640);
        boxNoRefresh.setLayoutY(20);
        boxNoRefresh.getChildren().addAll(boxNoRefreshRoute, boxNoRefreshElse);
        pane.getChildren().addAll(boxNoRefresh, boxToRefresh, legend());

        Button save = new Button("zapisz");
        save.setTranslateX(150);
        save.setTranslateY(520);
        Button load = new Button("wczytaj");
        load.setTranslateX(150);
        load.setTranslateY(550);
        save.setOnAction(event -> {
            ObjectOutputStream out = null;
            try {
                out = new ObjectOutputStream(new FileOutputStream("save.lol"));
                out.writeObject(vehicles);
                out.writeObject(buildings);
                out.writeObject(humans);
                out.writeObject(connections);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    assert out != null;
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        load.setOnAction(event -> {
            ObjectInputStream in = null;
            try {
                in = new ObjectInputStream(new FileInputStream("save.lol"));
                if (in != null) {
                    run = false;
                    stopAllThreads();
                    objectToDisplay = null;
                    Thread.sleep(1000);
                    pane.getChildren().clear();
                    vehicles = (ArrayList<BaseVehicle>) in.readObject();
                    buildings = (ArrayList<Building>) in.readObject();
                    humans = (ArrayList<Human>) in.readObject();
                    connections = (ArrayList<Connection>) in.readObject();
                    for(Building building : buildings){
                        if(building instanceof Crossing){
                            building.getVehicles().clear();
                        }
                    }
                    everyTime(pane);
                }
            } catch (IOException | ClassNotFoundException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                try {
                    assert in != null;
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });
        pane.getChildren().addAll(save, load);
        run = true;
    }

    /**
     * Stopping all threads despite main
     */
    private static void stopAllThreads() {
        for (BaseVehicle vehicle : vehicles) {
            vehicle.setRun(false);
        }

        for (Human human : humans) {
            human.setRun(false);
        }
    }

    /**Drawing world on pane, creating buildings, vehicles and humans
     * @param pane
     */
    private static void onlyAtBeggining(Pane pane) {
        harborImage = new ImageView(new Image("/harbor.png"));
        civilAirportImage = new ImageView(new Image("/civilAirport.png"));
        militaryAirportImage = new ImageView(new Image("/militaryAirport.png"));
        planeImage = new ImageView(new Image("/plane.png"));
        crossingImage = new ImageView(new Image("/crossing.png"));
        boatImage = new ImageView(new Image("/boat.png"));
        boatImage = new ImageView(new Image("/boat.png"));
        boatImage = new ImageView(new Image("/boat.png"));
        carrierImage = new ImageView(new Image("/carrier.png"));
        militaryPlaneImage = new ImageView(new Image("/militaryPlane.png"));

        buildings.add(new CivilAirport(90, 80, "Poznan"));
        buildings.add(new CivilAirport(140, 265, "Wiry"));
        buildings.add(new CivilAirport(270, 470, "Glasgow"));
        buildings.add(new CivilAirport(370, 80, "Kornik"));
        buildings.add(new CivilAirport(580, 500, "Ajndholen"));
        buildings.add(new CivilAirport(650, 330, "Niu Jork"));
        buildings.add(new AirCrossing(210, 170, "Rondo 1050 lecia"));
        buildings.add(new AirCrossing(480, 410, "Rondo Armii Krajowej"));

        buildings.add(new Harbour(70, 200, "Seatle"));
        buildings.add(new Harbour(200, 280, "Lizbona"));
        buildings.add(new Harbour(310, 180, "Gdynia"));
        buildings.add(new Harbour(320, 420, "Astana"));
        buildings.add(new Harbour(530, 340, "Ajndholen"));

        buildings.add(new WaterCrossing(120, 380, "Rondo Macieja"));
        buildings.add(new WaterCrossing(400, 290, "Rondo Zdrowotności"));


        buildings.add(new MilitaryAirport(429, 500, "Sandomierz"));
        buildings.add(new MilitaryAirport(670, 430, "Częstochowa"));
        buildings.add(new MilitaryAirport(40, 130, "Elbląg"));
        buildings.add(new MilitaryAirport(180, 50, "Radom"));


        connections.add(new Connection(buildings.get(0), buildings.get(6)));
        connections.add(new Connection(buildings.get(1), buildings.get(6)));
        connections.add(new Connection(buildings.get(3), buildings.get(6)));
        connections.add(new Connection(buildings.get(2), buildings.get(7)));
        connections.add(new Connection(buildings.get(4), buildings.get(7)));
        connections.add(new Connection(buildings.get(5), buildings.get(7)));
        connections.add(new Connection(buildings.get(7), buildings.get(6)));


        connections.add(new Connection(buildings.get(8), buildings.get(13)));
        connections.add(new Connection(buildings.get(9), buildings.get(13)));
        connections.add(new Connection(buildings.get(11), buildings.get(13)));
        connections.add(new Connection(buildings.get(10), buildings.get(14)));
        connections.add(new Connection(buildings.get(12), buildings.get(14)));
        connections.add(new Connection(buildings.get(13), buildings.get(14)));

        connections.add(new Connection(buildings.get(15), buildings.get(7)));
        connections.add(new Connection(buildings.get(16), buildings.get(7)));
        connections.add(new Connection(buildings.get(17), buildings.get(6)));
        connections.add(new Connection(buildings.get(18), buildings.get(6)));

        vehicles.add(new PassengerPlane(pane));
        vehicles.add(new PassengerPlane(pane));
        vehicles.add(new PassengerPlane(pane));
        vehicles.add(new PassengerPlane(pane));
        vehicles.add(new PassengerPlane(pane));
        vehicles.add(new PassengerPlane(pane));

        int howMany = 6;
        for (int i = 0; i < howMany; i++) {
            vehicles.get(i).setCoords(new Coords(buildings.get(i).getCoords()));
            vehicles.get(i).setRoute(buildings.get(i), i > 0 ? buildings.get(i - 1) : buildings.get(5), i < 5 ? buildings.get(i + 1) : buildings.get(0));
        }

        vehicles.add(new PassengerShip());
        vehicles.add(new PassengerShip());
        vehicles.add(new Carrier());
        vehicles.add(new PassengerShip());
        vehicles.add(new PassengerShip());

        for (int i = 6; i < 11; i++) {
            vehicles.get(i).setCoords(new Coords(buildings.get(i + 2).getCoords()));
            vehicles.get(i).setRoute(buildings.get(i + 2), i + 2 > 8 ? buildings.get(i + 1) : buildings.get(12), i < 10 ? buildings.get(i + 3) : buildings.get(8));
        }

        createPeople();

    }

    public static ImageView getHarborImage() {
        return harborImage;
    }

    public static ImageView getPlaneImage() {
        return planeImage;
    }

    public static ImageView getCrossingImage() {
        return crossingImage;
    }

    public static ImageView getCivilAirportImage() {
        return civilAirportImage;
    }

    public static ImageView getMilitaryAirportImage() {
        return militaryAirportImage;
    }

    public static ImageView getBoatImage() {
        return boatImage;
    }

    public static ImageView getCarrierImage() {
        return carrierImage;
    }

    public static ImageView getMilitaryPlaneImage() {
        return militaryPlaneImage;
    }

    /**Moving vehicles on pane, manages info boxes
     * @param pane
     */
    public static void live(Pane pane) {
        if (run) {
            for (BaseVehicle vehicle : vehicles) {
                vehicle.relocate();
            }
            if (objectToDisplay != null) {
                display();
                displayNoRefresh(pane);
            } else {
                clearBoxes();
            }
        }
    }

    /**
     * clear info boxes
     */
    private static void clearBoxes() {
        boxNoRefreshElse.getChildren().clear();
        boxNoRefreshRoute.getChildren().clear();
        boxToRefresh.getChildren().clear();
    }

    /**Displaying things which don't have to be refreshed every frame
     * @param pane
     */
    private static void displayNoRefresh(Pane pane) {
        if (refresh || ((Display) objectToDisplay).shouldRefreshInfoBox()) {
            boxNoRefreshElse.getChildren().clear();
            boxNoRefreshElse.getChildren().addAll((((Display) objectToDisplay).getNoRefresh()));
            if (objectToDisplay instanceof MakeNewVehicle) {
                boxNoRefreshElse.getChildren().add(makeVehicleButton(pane));
            }
            if (objectToDisplay instanceof Passengers) {
                boxNoRefreshElse.getChildren().addAll(passengersLayout());
            }
            if (objectToDisplay instanceof MakeCarrier) {
                boxNoRefreshElse.getChildren().addAll(makeCarrierButton(pane));
            }
        }
        if (refresh) {
            boxNoRefreshRoute.getChildren().clear();
            if (objectToDisplay instanceof Route && !(objectToDisplay instanceof Carrier)) {
                boxNoRefreshRoute.getChildren().add(routeLauout());
            }
        }
        refresh = false;
    }


    /**Search for the shortest route
     * @param from where route begins
     * @param to where route ends
     * @return
     */
    public synchronized static ArrayList<Building> getRoute(Building from, Building to) {
        nullifyVisited();
        ArrayList<Building> route = new ArrayList<>();
        for (Building building : from.getRouteTo(to)) {
            route.add(building);
        }
        return route;

    }


    /**
     * Nullify data in buildings needed to calculate shortest road
     */
    public synchronized static void nullifyVisited() {
        for (Building building : buildings) {
            building.setVisited(false);
            building.setRoute(new ArrayList<>());
        }
    }

    /**Return distance between to Coords
     * @param start
     * @param end
     * @return
     */
    public synchronized static double distanceBetween(Coords start, Coords end) {
        return Math.sqrt(Math.pow(start.getX() - end.getX(), 2) + Math.pow(start.getY() - end.getY(), 2));
    }

    /**
     * @param object
     */
    public static void setObjectToDisplay(Display object) {
        objectToDisplay = object;
        refresh = true;
    }

    /**Create HBox from two values
     * @param name
     * @param value
     * @return
     */
    public static HBox createHBox(String name, String value) {
        Label label = new Label(name + ':');
        Label item = new Label(value);
        HBox hb = new HBox(8);
        hb.getChildren().addAll(label, item);
        return hb;
    }

    /**Create HBox from two values
     * @param name
     * @param i
     * @return
     */
    public static HBox createHBox(String name, int i) {
        return createHBox(name, Integer.toString(i));
    }



    public static HBox createHBox(ImageView image, String name) {
        HBox hBox = new HBox(8);
        hBox.getChildren().addAll(new ImageView(image.getImage()), new Label(name));
        return hBox;
    }

    /**
     * display info boxes
     */
    private static void display() {
        boxToRefresh.getChildren().clear();
        boxToRefresh.getChildren().addAll((((Display) objectToDisplay).getInfo()));

    }

    /**Return rand route
     * @param c which buildings route could include
     * @param start from where route should start, could be null
     * @return
     */
    public static ArrayList<Building> getRandRoute(Class<?> c, Building start) {
        ArrayList<Building> possibleBuildings = new ArrayList<>();
        ArrayList<Building> route = new ArrayList<>();
        for (Building building : buildings) {
            if (c.isInstance(building) && !(building instanceof Crossing)) {
                possibleBuildings.add(building);
            }
        }
        if (start != null) {
            route.add(start);
            possibleBuildings.remove(start);
        }
        int howMany = new Random().nextInt(possibleBuildings.size() - 3) + 3;
        for (int i = 0; i < howMany; i++) {
            int which = new Random().nextInt(possibleBuildings.size());
            route.add(possibleBuildings.get(which));
            possibleBuildings.remove(which);
        }
        return route;
    }

    /**Return rand route including buildings where humans could stop
     * @return
     */
    private static ArrayList<Building> getRandRouteForHuman() {
        ArrayList<Building> possibleBuildings = new ArrayList<>();
        ArrayList<Building> route = new ArrayList<>();
        for (Building building : buildings) {
            if (!(building instanceof Crossing) && !(building instanceof MilitaryAirport)) {
                possibleBuildings.add(building);
            }
        }
        int howMany = new Random().nextInt(possibleBuildings.size() - 3) + 3;
        for (int i = 0; i < howMany; i++) {
            int which = new Random().nextInt(possibleBuildings.size());
            route.add(possibleBuildings.get(which));
            possibleBuildings.remove(which);
        }
        return route;
    }

    public static void deleteObjectToDisplay() {
        objectToDisplay = null;
    }

    /**Returning all buildings which are instance of @param c
     * @param c
     * @return
     */
    public static ArrayList<Building> getAllBuildingsLike(Class<?> c) {
        ArrayList<Building> possibleBuildings = new ArrayList<>();
        for (Building building : buildings) {
            if (c.isInstance(building) && !(building instanceof Crossing)) {
                possibleBuildings.add(building);
            }
        }
        return possibleBuildings;
    }

    /**Return building from list list nearest to coords
     * @param list
     * @param coords
     * @return
     */
    public static Building buildingNearestTo(ArrayList<Building> list, Coords coords) {
        Double min = 99999999.9d;
        Building nearest = null;
        for (Building building : list) {
            if (distanceBetween(building.getCenterCoords(), coords) < min) {
                nearest = building;
                min = distanceBetween(building.getCenterCoords(), coords);
            }
        }

        return nearest;
    }

    /**add make new vehicle button to pane
     * @param pane
     * @return
     */
    private static Button makeVehicleButton(Pane pane) {
        Button button = new Button("stwórz pojazd");
        button.setOnAction(event -> {
            BaseVehicle vehicle = ((MakeNewVehicle) objectToDisplay).make(pane);
            Thread thread = new Thread(vehicle);
            thread.setDaemon(true);
            thread.start();
            vehicles.add(vehicle);
            createPeople();
        });
        return button;
    }

    /**add make new carrier button to pane
     * @param pane
     * @return
     */
    private static Button makeCarrierButton(Pane pane) {
        Button button = new Button("stwórz lotniskowiec");
        button.setOnAction(event -> {
            BaseVehicle vehicle = ((MakeCarrier) objectToDisplay).makeNewCarrier(pane);
            Thread thread = new Thread(vehicle);
            thread.setDaemon(true);
            thread.start();
            vehicles.add(vehicle);
        });
        return button;
    }

    /**add passengers list to pane
     * @return
     */
    private static ListView passengersLayout() {
        ListView<Human> listView = new ListView<>();
        listView.setPrefSize(150, 100);
        ObservableList<Human> items = FXCollections.observableArrayList(((Passengers) objectToDisplay).getPassangers());
        listView.setItems(items);
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            objectToDisplay = newValue;
            refresh = true;
        });
        return listView;
    }

    /**add route list to pane
     * @return
     */
    private static VBox routeLauout() {
        VBox vBox = new VBox(8);
        ListView<Building> listView = new ListView<>();
        listView.setEditable(true);
        ArrayList<Building> listToChoose = getAllBuildingsLike(((Route) objectToDisplay).getWhichClass());
        listView.setPrefSize(150, 100);
        ObservableList<Building> items = FXCollections.observableArrayList(((Route) objectToDisplay).getRoute());
        listView.setItems(items);
        ObservableList names =
                FXCollections.observableArrayList();
        names.addAll(listToChoose);
        listView.setCellFactory(ComboBoxListCell.forListView(names));
        HBox hBox = new HBox(4);
        Button add = new Button("+");
        add.setOnAction(event -> listView.getItems().add(listToChoose.get(0)));
        Button delete = new Button("-");
        delete.setOnAction(event -> listView.getItems().remove(listView.getItems().size() - 1));
        Button save = new Button("zapisz");
        save.setOnAction(event -> {
            ArrayList<Building> toSave = new ArrayList<Building>();
            for (Building building : listView.getItems()) {
                if (toSave.size() > 0 && toSave.get(toSave.size() - 1) != building) {
                    toSave.add(building);
                } else if (toSave.size() == 0) {
                    toSave.add(building);
                }
            }
            if (toSave.size() > 0 && toSave.get(0) == toSave.get(toSave.size() - 1)) {
                toSave.remove(0);
            }

            if (toSave.size() > 1) {
                ((Route) objectToDisplay).changeRoute(toSave);
                listView.setItems(FXCollections.observableArrayList(toSave));
            } else {
                listView.getItems().clear();
                listView.setItems(FXCollections.observableArrayList(((Route) objectToDisplay).getRoute()));
            }
        });
        hBox.getChildren().addAll(add, delete, save);
        vBox.getChildren().addAll(listView, hBox);
        return vBox;
    }

    public static String getRandomCompany() {
        return companies[new Random().nextInt(companies.length)];
    }

    public static String getRandomName() {
        return names[new Random().nextInt(names.length)];
    }

    public static String getRandomSurname() {
        return surnames[new Random().nextInt(surnames.length)];
    }

    public static String getRandomWeapon() {
        return weapons[new Random().nextInt(weapons.length)];
    }

    /**
     * adding random number of people (5,10) in random buildings
     */
    private static void createPeople() {
        int howMany = new Random().nextInt(11) + 5;
        for (int i = 0; i < howMany; i++) {
            Human human = new Human(getRandRouteForHuman());
            Thread thread = new Thread(human);
            thread.setDaemon(true);
            thread.start();
            humans.add(human);
        }
    }

    public synchronized static int getID(){
        return id.getAndIncrement();
    }

    /**Return map legend
     * @return
     */
    private static VBox legend() {
        ArrayList<HBox> hboxes = new ArrayList<>();
        hboxes.add(createHBox(boatImage, "łódź"));
        hboxes.add(createHBox(carrierImage, "lotniskowiec"));
        hboxes.add(createHBox(planeImage, "samolot pasażerski"));
        hboxes.add(createHBox(militaryPlaneImage, "samolot wojskowy"));
        hboxes.add(createHBox(civilAirportImage, "lotnisko cywilne"));
        hboxes.add(createHBox(militaryAirportImage, "lotnisko wojskowe"));
        hboxes.add(createHBox(harborImage, "port"));
        hboxes.add(createHBox(crossingImage, "skrzyżowanie"));


        VBox vbox = new VBox(8);
        vbox.setTranslateX(10);
        vbox.setTranslateY(370);
        vbox.getChildren().addAll(hboxes);
        return vbox;
    }

    public enum HumanState implements Serializable {IN_PORT, FLYING}

    public enum VehicleState implements Serializable {IN_AIR, CROSSING, LAND, TAKE_OFF, WAIT, DELETE, TO_THE_RESCUE, ALARMA, RECOVER}
}
