package de.kabuman.tinkerforge.trafocontroller.services;

import com.tinkerforge.BrickDC;
import com.tinkerforge.BrickletRotaryPoti;
import de.kabuman.tinkerforge.services.StackService;
import de.kabuman.tinkerforge.services.VehicleService;

// Referenced classes of package de.kabuman.tinkerforge.trafocontroller.services:
//            TrafoService

public interface TrafoControllerService
{

    public abstract BrickDC getLok1PowerSource();

    public abstract BrickDC getLok2PowerSource();

    public abstract BrickletRotaryPoti getLok1PowerControl();

    public abstract BrickletRotaryPoti getLok2PowerControl();

    public abstract TrafoService getTrafoService();

    public abstract StackService getStackService();

    public abstract VehicleService getVehicleService();
}
