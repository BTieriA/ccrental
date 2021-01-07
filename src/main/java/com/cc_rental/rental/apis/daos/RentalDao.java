package com.cc_rental.rental.apis.daos;

import com.cc_rental.rental.apis.vos.AddRentalVo;
import com.cc_rental.rental.apis.vos.CountVo;
import com.cc_rental.rental.apis.vos.RentalVo;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Repository
public class RentalDao {
    // Rental 추가
    public boolean insertRental(Connection connection, AddRentalVo addRentalVo) throws SQLException {
        String query = "" +
                "INSERT INTO `cc_rental`.`rentals`(`rental_from_date`,\n" +
                "                                  `rental_from_time`,\n" +
                "                                  `rental_to_date`,\n" +
                "                                  `rental_to_time`,\n" +
                "                                  `branch_index`)\n" +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setDate(1, addRentalVo.getRental_from_date());
            preparedStatement.setTime(2, addRentalVo.getRental_from_time());
            preparedStatement.setDate(3, addRentalVo.getRental_to_date());
            preparedStatement.setTime(4, addRentalVo.getRental_to_time());
            preparedStatement.setInt(5, addRentalVo.getBranch_index());
            preparedStatement.execute();
        }
        return true;
    }

    // Retal 현황
    public ArrayList<CountVo> getRental(Connection connection, AddRentalVo addRentalVo) throws SQLException {
        CountVo countVo = null;
        ArrayList<CountVo> counts = new ArrayList<>();
        String query = "" +
                "SELECT `count`.`branch_index` AS `branchIndex`,\n" +
                "       `count`.`car_index`    AS `carIndex`,\n" +
                "       `car`.`car_name`       AS `carName`,\n" +
                "       `car`.`car_class`       AS `carType`,\n" +
                "       `count`.`car_count`    AS `totalCar`,\n" +
                "       (SELECT COUNT(`rental_index`)\n" +
                "        FROM `cc_rental`.`rentals` AS `rental_in`\n" +
                "        WHERE `rental_in`.`branch_index` = `count`.`branch_index`\n" +
                "          AND `rental_in`.`car_index` = `count`.`car_index`\n" +
                "          AND CAST(CONCAT(`rental_in`.`rental_from_date`, ' ', `rental_in`.`rental_from_time`) AS DATETIME) <= NOW()\n" +
                "          AND CAST(CONCAT(`rental_in`.`rental_to_date`, ' ', `rental_in`.`rental_to_time`) AS DATETIME) >=\n" +
                "              NOW())          AS `usingCar`\n" +
                "FROM `cc_rental`.`counts` AS `count`\n" +
                "         INNER JOIN `cc_rental`.`rentals` AS `rental`\n" +
                "                    ON `count`.`branch_index` = `rental`.`branch_index` AND `count`.`car_index` = `rental`.`car_index`\n" +
                "         LEFT JOIN `cc_rental`.`cars` AS `car`\n" +
                "                   ON `count`.`car_index` = `car`.`car_index`\n" +
                "GROUP BY `count`.`count_index`";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, addRentalVo.getBranch_index());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    countVo = new CountVo(
                            resultSet.getInt("branchIndex"),
                            resultSet.getInt("carIndex"),
                            resultSet.getString("carName"),
                            resultSet.getString("carType"),
                            resultSet.getInt("totalCar"),
                            resultSet.getInt("usingCar")
                    );
                    rentals.add(rentalVo);
                }
            }
        }


//    public ArrayList<RentalVo> getRental(Connection connection, AddRentalVo addRentalVo) throws SQLException {
//        RentalVo rentalVo = null;
//        ArrayList<RentalVo> rentals = new ArrayList<>();
//        String query = "" +
//                "SELECT `rental`.`rental_index`,\n" +
//                "       `selectBranch`.`branch_index`,\n" +
//                "       `selectBranch`.`car_index`,\n" +
//                "       `selectBranch`.`car_name`      AS `carName`,\n" +
//                "       `selectBranch`.`car_class`     AS `carType`\n" +
//                "FROM `cc_rental`.`rentals` AS `rental`,\n" +
//                "     (SELECT `rental`.rental_index,\n" +
//                "             `count`.`branch_index`,\n" +
//                "             `count`.`car_index`,\n" +
//                "             `cars`.`car_name`,\n" +
//                "             `cars`.`car_class` \n" +
//                "      FROM `cc_rental`.`counts` AS `count`\n" +
//                "               INNER JOIN `cc_rental`.`rentals` AS `rental` ON `count`.branch_index = `rental`.branch_index\n" +
//                "               LEFT JOIN `cars` ON count.car_index = cars.car_index\n" +
//                "      WHERE `count`.`branch_index` = ?) AS `selectBranch`\n" +
//                "WHERE `selectBranch`.car_index = 1\n" +
//                "  AND CAST(CONCAT(`rental`.`rental_from_date`, ' ', `rental`.`rental_from_time`) AS DATETIME) <= NOW()\n" +
//                "  AND CAST(CONCAT(`rental`.`rental_to_date`, ' ', `rental`.`rental_to_time`) AS DATETIME) >= NOW()";
//
//        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
//            preparedStatement.setInt(1, addRentalVo.getBranch_index());
//            try (ResultSet resultSet = preparedStatement.executeQuery()) {
//                while (resultSet.next()) {
//                    rentalVo = new RentalVo(
//                            resultSet.getInt("rentalIndex"),
//                            resultSet.getInt("branchIndex"),
//                            resultSet.getInt("carIndex"),
//                            resultSet.getString("carName"),
//                            resultSet.getString("carType")
//                    );
//                    rentals.add(rentalVo);
//                }
//            }
//        }
//        return rentals;
//    }
    }