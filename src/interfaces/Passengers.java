package interfaces;

import models.Human;
import models.buildings.Building;

import java.util.ArrayList;

/**
 * Created by Maciej on 06.01.16.
 */
public interface Passengers {
    boolean getOnBoard(Human human);

    /**unload passengers which current destination is building
     * @param building
     */
    void unloadPassengers(Building building);
    ArrayList<Human> getPassangers();
    int getMaxCapacity();

    /**move passanger from his current location to building
     * @param human
     * @param building
     */
    void movePassanger(Human human, Building building);

}
