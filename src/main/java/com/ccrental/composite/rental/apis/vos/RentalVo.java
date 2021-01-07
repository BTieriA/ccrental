package com.ccrental.composite.rental.apis.vos;

public class RentalVo {
    private final int branchIndex;
    private final int carIndex;
    private final String carName;
    private final String carType;
    private final boolean isAvailable;

    public RentalVo(int branchIndex, int carIndex, String carName, String carType, boolean isAvailable) {
        this.branchIndex = branchIndex;
        this.carIndex = carIndex;
        this.carName = carName;
        this.carType = carType;
        this.isAvailable = isAvailable;
    }

    public int getBranchIndex() {
        return branchIndex;
    }

    public int getCarIndex() {
        return carIndex;
    }

    public String getCarName() {
        return carName;
    }

    public String getCarType() {
        return carType;
    }

    public boolean isAvailable() {
        return isAvailable;
    }
}
