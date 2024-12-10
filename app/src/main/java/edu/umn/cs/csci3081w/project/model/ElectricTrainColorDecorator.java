package edu.umn.cs.csci3081w.project.model;


import java.io.PrintStream;

public class ElectricTrainColorDecorator extends ElectricTrain implements VehicleColor {
    private final ElectricTrain train;
    private int[] colors;

    public ElectricTrainColorDecorator(Vehicle vehicle) {
        super(vehicle.getId(), vehicle.getLine(), vehicle.getCapacity(), vehicle.getSpeed());
        this.colors = new int[] { 60, 179, 113, 255 };
        if (vehicle instanceof ElectricTrain rain) {
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


