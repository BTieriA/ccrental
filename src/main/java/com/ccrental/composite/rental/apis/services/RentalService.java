package com.ccrental.composite.rental.apis.services;


import com.ccrental.composite.rental.apis.containers.CarListContainer;
import com.ccrental.composite.rental.apis.containers.RentalResultContainer;
import com.ccrental.composite.rental.apis.daos.RentalDao;
import com.ccrental.composite.rental.apis.enums.RentalGetResult;
import com.ccrental.composite.rental.apis.vos.*;
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

    // 예약전
    public CarListContainer getCarList(InputVo inputVo) throws SQLException {
        try (Connection connection = this.dataSource.getConnection()) {

            ArrayList<RentalVo> cars = new ArrayList<>();

            this.rentalDao.removeGroupBy(connection);
            ArrayList<CarVo> totalCarList = this.rentalDao.totalCarList(connection, inputVo);
            ArrayList<CarVo> usingCarList = this.rentalDao.usingCarList(connection, inputVo);

            for (CarVo totalCar : totalCarList) {
                for (CarVo usingCar : usingCarList) {
                    if (totalCar.getCarIndex() == usingCar.getCarIndex()) {
                        if (totalCar.getCarCount() > usingCar.getCarCount()) {
                            cars.add(new RentalVo(
                                    inputVo.getBranch_index(),
                                    totalCar.getCarIndex(),
                                    totalCar.getCarName(),
                                    totalCar.getCarType(),
                                    true)
                            );
                            break;
                        } else {
                            cars.add(new RentalVo(
                                    inputVo.getBranch_index(),
                                    usingCar.getCarIndex(),
                                    usingCar.getCarName(),
                                    usingCar.getCarType(),
                                    false)
                            );
                            break;
                        }
                    }
                }
            }
            return new CarListContainer(RentalGetResult.SUCCESS, cars);
        }
    }

    // 예약후
    public RentalResultContainer getRental(AddRentalVo addRentalVo) throws SQLException {
        try (Connection connection = this.dataSource.getConnection()) {
            ArrayList<CountVo> counts = this.rentalDao.getRental(connection, addRentalVo);
            int totalCar = this.rentalDao.totalCar(connection, addRentalVo);
            int usingCar = this.rentalDao.usingCar(connection, addRentalVo);
            if (totalCar > usingCar) {
                this.rentalDao.insertRental(connection, addRentalVo);
                return new RentalResultContainer(RentalGetResult.SUCCESS, counts);
            } else {
                return new RentalResultContainer(RentalGetResult.SOLDOUT, counts);
            }
        }
    }


}
