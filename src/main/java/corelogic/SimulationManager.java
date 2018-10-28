package corelogic;

import java.util.ArrayList;
import java.util.List;

public class SimulationManager {

    static private List<Bus> buses;
    static private List<Stop> stops;

    static private int simTime;

    public static void main(String[] args) {
        InitSim();
        System.out.println("Bus 1 is at stop " + buses.get(0).getCurrentStop().getName());
        MoveNextBus();
        System.out.println("Bus 1 is at stop " + buses.get(0).getCurrentStop().getName());
        MoveNextBus();
        System.out.println("Bus 1 is at stop " + buses.get(0).getCurrentStop().getName());
    }

    /**
     * Simulate one tick on every simulation entity until a bus arrives at a stop
     */
    private static void MoveNextBus() {
        while (!Tick()) {}
    }

    private static boolean Tick() {
        boolean busArrived = false;
        ++simTime;
        for (Stop stop : stops)
            stop.Tick();
        for (Bus bus : buses)
            busArrived = bus.Tick(simTime) || busArrived;
        return busArrived;
    }

    private static void InitSim() {
        buses = new ArrayList<>();
        stops = new ArrayList<>();
        simTime = 0;

        //Sample init
        stops.add(new Stop("Downtown", 0, -10));
        stops.add(new Stop("Midtown", 0, 0));
        List<Stop> routeStops = new ArrayList<>(stops);
        Route route = new Route(routeStops, false);
        buses.add(new Bus(route, 0, 5, simTime));
    }
}