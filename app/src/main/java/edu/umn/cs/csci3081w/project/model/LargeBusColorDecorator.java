package edu.umn.cs.csci3081w.project.model;


import java.io.PrintStream;

public class LargeBusColorDecorator extends LargeBus implements VehicleColor {
    private final LargeBus bus;
    private int[] colors;

    public LargeBusColorDecorator(Vehicle vehicle) {
        super(vehicle.getId(), vehicle.getLine(), vehicle.getCapacity(), vehicle.getSpeed());
        this.colors = new int[] {122, 0, 25, 255};
        if (vehicle instanceof LargeBus bas) {
            this.bus = bas;
        } else {
            throw new IllegalArgumentException("vehicle does not implement Bus");
        }
    }

    @Override
    public void report(PrintStream out) {
        this.bus.report(out);
    }

    @Override
    public int getCurrentCO2Emission() {
        return this.bus.getCurrentCO2Emission();
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



