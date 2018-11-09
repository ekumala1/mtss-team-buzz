package dataanalysis;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import dataanalysis.Interfacer;
import corelogic.Bus;
import corelogic.Route;
import corelogic.Stop;
import java.util.List;
import java.util.ArrayList;

public class InterfacerTest {

    @Test(expected = IllegalArgumentException.class)
    public void constructorNullExceptionTest() {
        Interfacer testInterfacer = new Interfacer();
        Stop stop = null;
        testInterfacer.addStop(stop);
    }

    @Test
    public void constructorTest() {
        Interfacer testInterfacer = new Interfacer();
        testInterfacer.addStop(new Stop(1, "Harold Dr. and 5th", 0.4, 7.0, 1.0));
        assertEquals(testInterfacer.getStops().get(0).getName(), "Harold Dr. and 5th");
        assertEquals(testInterfacer.getStops().get(0).getX(), 0.4, 0.01);
        assertEquals(testInterfacer.getStops().get(0).getX(), 0.4, 0.01);
    }

    @Test
    public void simulationTest() {
        Interfacer test = new Interfacer();

        Stop stop1 = new Stop(0, "CULC", 0.4, 7.0, 1);
        Stop stop2 = new Stop(1, "CRC", 0.6, 1.5, 2);
        Stop stop3 = new Stop(2, "Student Center", 0.9, 4.3, 3);
        List<Stop> stops = new ArrayList<>();
        stops.add(stop1);
        stops.add(stop2);
        stops.add(stop3);

        Route route1 = new Route(0, stops, false);
        List<Route> routes = new ArrayList<>();
        routes.add(route1);

        Bus bus1 = new Bus(0, route1, 0, 10, 50, 50, 0);
        Bus bus2 = new Bus(1, route1, 1, 5, 50, 40, 0);
        List<Bus> buses = new ArrayList<>();
        buses.add(bus1);
        buses.add(bus2);

        // Initialize Dummy Simulation
        test.dummySimulationInit(buses, stops, routes);
        // At this moment, route effectiveness should be 52.5
        test.updateEffectiveness();
        assertEquals(52.5, test.getEffectiveness(), 0.1);
    }
}
