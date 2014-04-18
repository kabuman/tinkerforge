package de.kabuman.tinkerforge.trafocontroller;

import de.kabuman.common.services.CountDownService;
import de.kabuman.common.services.CountDownServiceImpl;
import java.io.PrintStream;

// Referenced classes of package de.kabuman.tinkerforge.trafocontroller:
//            TrafoControllerAppl

public class TrafoControllerMain
{

    public TrafoControllerMain()
    {
    }

    public static void main(String args[])
    {
        CountDownService countDownService = new CountDownServiceImpl(5);
        Exception e;
        do
        {
            TrafoControllerAppl trafoController = new TrafoControllerAppl();
            e = trafoController.controllerLauncher(false);
            if(e == null)
                break;
            System.out.println((new StringBuilder("StrobeControllerMain:: Exception detected: ")).append(e).toString());
            countDownService.down();
        } while(!countDownService.isDown());
        if(e != null)
            e.printStackTrace();
    }

    static final int TRY_COUNTER = 5;
}
