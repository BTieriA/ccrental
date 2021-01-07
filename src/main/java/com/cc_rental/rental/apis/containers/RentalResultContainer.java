package com.cc_rental.rental.apis.containers;

import com.cc_rental.rental.apis.enums.RentalGetResult;
import com.cc_rental.rental.apis.vos.RentalVo;

import java.util.ArrayList;

public class RentalResultContainer {
    private final RentalGetResult rentalGetResult;
    private final ArrayList<RentalVo> rentals;

    public RentalResultContainer(RentalGetResult rentalGetResult) {
        this(rentalGetResult, null);
    }

    public RentalResultContainer(RentalGetResult rentalGetResult, ArrayList<RentalVo> rentals) {
        this.rentalGetResult = rentalGetResult;
        this.rentals = rentals;
    }

    public RentalGetResult getRentalGetResult() {
        return rentalGetResult;
    }

    public ArrayList<RentalVo> getRentals() {
        return rentals;
    }
}

