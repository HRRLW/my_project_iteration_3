package edu.umn.cs.csci3081w.project.model;


import java.io.PrintStream;

public class DieselTrainColorDecorator extends DieselTrain implements VehicleColor {
    private final DieselTrain train;
    private int[] colors;

    public DieselTrainColorDecorator(Vehicle vehicle) {
        super(vehicle.getId(), vehicle.getLine(), vehicle.getCapacity(), vehicle.getSpeed());
        this.colors = new int[] { 255, 204, 51, 255 };
        if (vehicle instanceof DieselTrain rain) {
            this.train = rain;
        } else {
            throw new IllegalArgumentException("vehicle does not implement Bus");
        }
    }

    @Override
    public void report(PrintStream out) {
        this.train.report(out);
    }

    @Override
    public int getCurrentCO2Emission() {
        return this.train.getCurrentCO2Emission();
    }

    @Override
    public int[] getColor() {
        return this.colors;
    }

    @Override
    public void setColor(int[] rgba) {
        this.colors = rgba;
    }
}


