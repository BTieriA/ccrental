package com.ccrental.composite.rental.apis.vos;

import java.sql.Date;
import java.sql.Time;

public class InputVo {
    private final Date rental_from_date;
    private final Time rental_from_time;
    private final Date rental_to_date;
    private final Time rental_to_time;
    private final int branch_index;

    public InputVo(Date rental_from_date, Time rental_from_time, Date rental_to_date, Time rental_to_time, int branch_index) {
        this.rental_from_date = rental_from_date;
        this.rental_from_time = rental_from_time;
        this.rental_to_date = rental_to_date;
        this.rental_to_time = rental_to_time;
        this.branch_index = branch_index;
    }

    public Date getRental_from_date() {
        return rental_from_date;
    }

    public Time getRental_from_time() {
        return rental_from_time;
    }

    public Date getRental_to_date() {
        return rental_to_date;
    }

    public Time getRental_to_time() {
        return rental_to_time;
    }

    public int getBranch_index() {
        return branch_index;
    }
}