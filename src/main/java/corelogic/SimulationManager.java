package corelogic;

import java.util.*;
import dataanalysis.Interfacer;

public class SimulationManager {
    static private Interfacer dataAnalysis;

    private static HashMap<Integer, Bus> buses;
    private static HashMap<Integer, Stop> stops;
    private static HashMap<Integer, Route> routes;

    public static HashMap<Integer, Bus> getBuses() { return buses; }
    public static HashMap<Integer, Stop> getStops() { return stops; }
    public static HashMap<Integer, Route> getRoutes() { return routes; }

    private static int simTime;
    private static boolean running;

    public static int getSimTime() { return simTime; }

    private static Timer timer;
    private static int interval;
    private static float fastForwardMultiplier;
    private static boolean isFast = false;

    private static final double LATITUDE_TO_MILES = 69;
    private static final double LATITUDE_OF_ORIGIN = 33.7; //Aprox latitude of Atlanta
    private static final double LONGITUDE_TO_MILES = Math.cos(LATITUDE_OF_ORIGIN) * LATITUDE_TO_MILES;

    static class Run extends TimerTask {
        public void run() {
            if (running) {
                tick();
            }
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please include a path to a simulation file");
            return;
        }
        initSim(args[0], 1000, 5);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            scanner.nextLine();
            togglePlay();
        }
    }

    /**
     * Simulate one tick on every simulation entity until a bus arrives at a stop
     */
    public static void MoveNextBus() {
        while (!tick()) {}
    }

    /**
     * Toggles automatic ticking of simulation
     */
    public static void togglePlay() {
        running = !running;
        if (running) {
            timer = new Timer();
            timer.schedule(new Run(), 0, interval);
        } else {
            timer.cancel();
        }
    }

    /**
     * Toggles fast forward mode
     */
    public static void toggleFastForward() {
        interval = isFast ? interval : (int) (interval * fastForwardMultiplier);
    }

    /**
     * Simulates one time unit of simulation
     * @return true if a bus reached a stop, otherwise false
     */
    public static boolean tick() {
        boolean busArrived = false;
        ++simTime;
        System.out.println("Simtime: " + simTime);
        for (Stop stop : stops.values()) {
            int num = stop.tick();
            //System.out.println(stop.getName() + ": Spawned " + num + " passengers");
        }
        for (Bus bus : buses.values()) {
            boolean busArrivedNow = bus.tick(simTime);
            //System.out.println("Bus " + bus.getId() + " is at " + bus.getCurrentStop().getName());
            busArrived = busArrivedNow || busArrived;
        }

        dataAnalysis.updateEffectiveness();
        return busArrived;
    }

    /**
     * Initializes the simulation with the given file path and tick interval
     * @param path Path to simulation file
     * @param interval Interval in milliseconds to tick
     * @param fastForwardMultiplier Multiplier for fast forward mode
     */
    public static void initSim(String path, int interval, float fastForwardMultiplier) {
        initSim(path, interval, fastForwardMultiplier, true);
    }

    static void initSim(String path, int interval, float fastForwardMultiplier, boolean shouldConvert) {
        buses = new HashMap<>();
        stops = new HashMap<>();
        routes = new HashMap<>();
        simTime = 0;

        dataAnalysis = new Interfacer(buses, stops, routes, "DataAnalysis.DOT");
        FileManager.importScenario(path, "scenarios/test_evening_distribution.csv", buses, stops, routes, simTime);

        if (shouldConvert) {
            //Convert all the input values in terms of miles per minute
            for (Stop s : stops.values()) {
                s.x *= LONGITUDE_TO_MILES;
                s.y *= LATITUDE_TO_MILES;
            }

            for (Bus b : buses.values()) {
                b.speed /= 60;
                b.calculateArrival(simTime);
            }
        }

        SimulationManager.interval = interval;
        SimulationManager.fastForwardMultiplier = fastForwardMultiplier;
    }

    /**
     * Exports current simulation into .DOT file for Data Analysis
     */
    public static void takeSnapshot() {
        dataAnalysis.createGraph();
    }
}