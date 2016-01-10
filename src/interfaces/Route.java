package interfaces;

import models.buildings.Building;

import java.util.ArrayList;

/**
 * Created by Maciej on 09.01.2016.
 */
public interface Route {
    void changeRoute(ArrayList<Building> route);
    ArrayList<Building> getRoute();

    /**get class of buildings which could be on the new route
     * @return
     */
    Class<?> getWhichClass();
}
