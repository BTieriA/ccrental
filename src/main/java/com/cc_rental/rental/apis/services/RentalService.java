package com.cc_rental.rental.apis.services;


import com.cc_rental.rental.apis.containers.RentalResultContainer;
import com.cc_rental.rental.apis.daos.RentalDao;
import com.cc_rental.rental.apis.enums.RentalGetResult;
import com.cc_rental.rental.apis.vos.AddRentalVo;
import com.cc_rental.rental.apis.vos.RentalVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;


@Service
public class RentalService {
    private final DataSource dataSource;
    private final RentalDao rentalDao;

    @Autowired
    public RentalService(DataSource dataSource, RentalDao rentalDao) {
        this.dataSource = dataSource;
        this.rentalDao = rentalDao;
    }

    public RentalGetResult addRental(AddRentalVo addRentalVo) throws SQLException {
        try (Connection connection = this.dataSource.getConnection()) {
            if (this.rentalDao.insertRental(connection, addRentalVo) == true){
                return RentalGetResult.SUCCESS;
            } else {
                return RentalGetResult.RENTAL_FAILED;
            }
        }
    }

    public RentalResultContainer getRental(AddRentalVo addRentalVo) throws SQLException {
        try (Connection connection= this.dataSource.getConnection()) {
            ArrayList<RentalVo> rentals = this.rentalDao.getRental(connection, addRentalVo);
            return new RentalResultContainer(RentalGetResult.SUCCESS, rentals);
        }
    }
}
