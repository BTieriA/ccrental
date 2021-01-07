package com.cc_rental.rental.apis.vos;

import java.sql.Date;
import java.sql.Time;

public class RentalVo {
    private final int rentalIndex;
    private final int branchIndex;
    private final int carIndex;
    private final String carName;
    private final String carType;

    public RentalVo(int rentalIndex, int branchIndex, int carIndex, String carName, String carType) {
        this.rentalIndex = rentalIndex;
        this.branchIndex = branchIndex;
        this.carIndex = carIndex;
        this.carName = carName;
        this.carType = carType;
    }

    public int getRentalIndex() {
        return rentalIndex;
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
}
