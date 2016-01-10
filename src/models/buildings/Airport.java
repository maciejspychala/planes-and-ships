package models.buildings;

/**
 * Created by Maciej on 2015-10-17.
 */
public abstract class Airport extends Building {

    public Airport(double x, double y, String name) {
        super(x, y, name);
        setCapacity(3);
        setWhichTypeOfVehicleCouldIStore("samolotów");
    }
}
