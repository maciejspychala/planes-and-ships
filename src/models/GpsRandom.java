package models;

import models.buildings.Harbour;
import models.vehicles.BaseVehicle;

/**
 * Created by Maciej on 09.01.2016.
 */
public class GpsRandom extends Gps {
    public GpsRandom(BaseVehicle vehicle) {
        super(vehicle);
    }

    /**
     * randomize route of Carrier
     */
    @Override
    protected void land() {
        super.land();
        getRoute().set(1, WorldHelper.getRandRoute(Harbour.class, getRoute().get(0)).get(1));
    }
}
