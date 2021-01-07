package com.ccrental.composite.rental.apis.daos;

import com.ccrental.composite.rental.apis.vos.AddRentalVo;
import com.ccrental.composite.rental.apis.vos.InputVo;
import com.ccrental.composite.rental.apis.vos.CarVo;
import com.ccrental.composite.rental.apis.vos.CountVo;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Repository
public class RentalDao {
    // -------------------------------------------------------------------------------------------- get
    // 예약전 각 렌트카 총수
    public ArrayList<CarVo> totalCarList(Connection connection, InputVo inputVo) throws SQLException {
        CarVo carVo = null;
        ArrayList<CarVo> cars = new ArrayList<>();
        String query = "" +
                "SELECT `count`.`branch_index` AS `branchIndex`,\n" +
                "       `count`.`car_index`    AS `carIndex`,\n" +
                "       `cars`.`car_name`      AS `carName`,\n" +
                "       `cars`.`car_class`     AS `carType`,\n" +
                "       `count`.`car_count`    AS `totalCar`\n" +
                "FROM `cc_rental`.`counts` AS `count`\n" +
                "         INNER JOIN `cc_rental`.`cars` ON `count`.`car_index` = `cars`.`car_index`\n" +
                "WHERE `count`.`branch_index` = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, inputVo.getBranch_index());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    carVo = new CarVo(
                            resultSet.getInt("branchIndex"),
                            resultSet.getInt("carIndex"),
                            resultSet.getString("carName"),
                            resultSet.getString("carType"),
                            resultSet.getInt("totalCar")
                    );
                    cars.add(carVo);
                }
            }
            return cars;
        }
    }

    // ONLY FULL GROUP BY 제거
    public void removeGroupBy(Connection connection) throws SQLException {
        String query = "SET sql_mode=(SELECT REPLACE(@@sql_mode, 'ONLY_FULL_GROUP_BY', ''))";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.execute();
        }
    }

    // 예약전 각 사용중인 렌트카
    public ArrayList<CarVo> usingCarList(Connection connection, InputVo inputVo) throws SQLException {
        CarVo carVo = null;
        ArrayList<CarVo> cars = new ArrayList<>();

        String query = "" +
                "SELECT `counts`.`branch_index` AS `branchIndex`,\n" +
                "       `counts`.`car_index`    AS `carIndex`,\n" +
                "       `car`.`car_name`       AS `carName`,\n" +
                "       `car`.`car_class`       AS `carType`,\n" +
                "       count(`rental_in`.`rental_index`) AS `usingCar`\n" +
                "FROM `cc_rental`.`counts`\n" +
                "         INNER JOIN `cc_rental`.`cars` AS `car` on `counts`.`car_index` = `car`.`car_index`\n" +
                "         LEFT JOIN `cc_rental`.`rentals` AS `rental_in` on `car`.`car_index` = `rental_in`.`car_index`\n" +
                "    AND CAST(CONCAT(?, ' ', ?) AS DATETIME) <= NOW()\n" +
                "    AND CAST(CONCAT(?, ' ', ?) AS DATETIME) >= NOW()\n" +
                "WHERE `counts`.`branch_index` = ?\n" +
                "GROUP BY `car`.`car_index`";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setDate(1, inputVo.getRental_from_date());
            preparedStatement.setTime(2, inputVo.getRental_from_time());
            preparedStatement.setDate(3, inputVo.getRental_to_date());
            preparedStatement.setTime(4, inputVo.getRental_to_time());
            preparedStatement.setInt(5, inputVo.getBranch_index());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    carVo = new CarVo(
                            resultSet.getInt("branchIndex"),
                            resultSet.getInt("carIndex"),
                            resultSet.getString("carName"),
                            resultSet.getString("carType"),
                            resultSet.getInt("usingCar")
                    );
                    cars.add(carVo);
                }
            }
            return cars;
        }
    }

    // ------------------------------------------------------------------------------------------- add
    // Rental 추가
    public void insertRental(Connection connection, AddRentalVo addRentalVo) throws SQLException {
        String query = "" +
                "INSERT INTO `cc_rental`.`rentals`(`rental_from_date`,\n" +
                "                                  `rental_from_time`,\n" +
                "                                  `rental_to_date`,\n" +
                "                                  `rental_to_time`,\n" +
                "                                  `branch_index`,\n" +
                "                                  `car_index`)\n" +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setDate(1, addRentalVo.getRental_from_date());
            preparedStatement.setTime(2, addRentalVo.getRental_from_time());
            preparedStatement.setDate(3, addRentalVo.getRental_to_date());
            preparedStatement.setTime(4, addRentalVo.getRental_to_time());
            preparedStatement.setInt(5, addRentalVo.getBranch_index());
            preparedStatement.setInt(6, addRentalVo.getCar_index());
            preparedStatement.execute();
        }
    }

    // 렌트카 총수
    public int totalCar(Connection connection, AddRentalVo addRentalVo) throws SQLException {
        int count;
        String query = "" +
                "SELECT car_count AS `totalCar` FROM `cc_rental`.counts AS `count`\n" +
                "WHERE `count`.`branch_index` = ?\n" +
                "  AND `count`.`car_index` = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, addRentalVo.getBranch_index());
            preparedStatement.setInt(2, addRentalVo.getCar_index());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                count = resultSet.getInt("totalCar");
            }
        }
        return count;
    }

    // 사용중인 렌트카
    public int usingCar(Connection connection, AddRentalVo addRentalVo) throws SQLException {
        int count;
        String query = "" +
                "SELECT COUNT(`rental_index`) AS `usingCar`\n" +
                "FROM `cc_rental`.`rentals` AS `rental_in`,\n" +
                "     `cc_rental`.`counts` AS `count`\n" +
                "WHERE `count`.`branch_index` = ?\n" +
                "  AND `count`.`car_index`= ?\n" +
                "  AND CAST(CONCAT(?, ' ', ?) AS DATETIME) <= NOW()\n" +
                "  AND CAST(CONCAT(?, ' ', ?) AS DATETIME) >= NOW()";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, addRentalVo.getBranch_index());
            preparedStatement.setInt(2, addRentalVo.getCar_index());
            preparedStatement.setDate(3, addRentalVo.getRental_from_date());
            preparedStatement.setTime(4, addRentalVo.getRental_from_time());
            preparedStatement.setDate(5, addRentalVo.getRental_to_date());
            preparedStatement.setTime(6, addRentalVo.getRental_to_time());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                count = resultSet.getInt("usingCar");
            }
        }
        return count;
    }

    // Retal 선택 차 리스트 (예약후)
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
                "        WHERE `rental_in`.`branch_index` = ?\n" +
                "          AND `count`.`car_index` = `rental_in`.`car_index`\n" +
                "          AND CAST(CONCAT(`rental_in`.`rental_from_date`, ' ', `rental_in`.`rental_from_time`) AS DATETIME) <= NOW()\n" +
                "          AND CAST(CONCAT(`rental_in`.`rental_to_date`, ' ', `rental_in`.`rental_to_time`) AS DATETIME) >=\n" +
                "              NOW())          AS `usingCar`\n" +
                "FROM `cc_rental`.`counts` AS `count`\n" +
                "         INNER JOIN `cc_rental`.`rentals` AS `rental`\n" +
                "                    ON `count`.`branch_index` = `rental`.`branch_index` AND `count`.`car_index` = ?\n" +
                "         LEFT JOIN `cc_rental`.`cars` AS `car`\n" +
                "                   ON `count`.`car_index` = `car`.`car_index`\n" +
                "GROUP BY `count`.`count_index`";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, addRentalVo.getBranch_index());
            preparedStatement.setInt(2, addRentalVo.getCar_index());
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
                    counts.add(countVo);
                }
            }
            return counts;
        }
    }

    // 자동차 리스트 (예약전)
//    public ArrayList<CountVo> carList(Connection connection, InputVo inputVo) throws SQLException {
//        CountVo countVo = null;
//        ArrayList<CountVo> counts = new ArrayList<>();
//        String query = "" +
//                "SELECT `count`.`branch_index` AS `branchIndex`,\n" +
//                "       `count`.`car_index`    AS `carIndex`,\n" +
//                "       `car`.`car_name`       AS `carName`,\n" +
//                "       `car`.`car_class`      AS `carType`,\n" +
//                "       `count`.`car_count`    AS `totalCar`,\n" +
//                "       (SELECT COUNT(`rental_index`)\n" +
//                "        FROM `cc_rental`.`rentals` AS `rental_in`\n" +
//                "        WHERE `rental_in`.`branch_index` = ?\n" +
//                "          AND `rental_in`.car_index = `count`.`car_index`\n" +
//                "          AND CAST(CONCAT(`rental_in`.`rental_from_date`, ' ', `rental_in`.`rental_from_time`) AS DATETIME) <= NOW()\n" +
//                "          AND CAST(CONCAT(`rental_in`.`rental_to_date`, ' ', `rental_in`.`rental_to_time`) AS DATETIME) >=\n" +
//                "              NOW())          AS `usingCar`\n" +
//                "FROM `cc_rental`.`counts` AS `count`\n" +
//                "         INNER JOIN `cc_rental`.`rentals` AS `rental`\n" +
//                "                    ON `count`.`branch_index` = `rental`.`branch_index`" +
//                "         LEFT JOIN `cc_rental`.`cars` AS `car`\n" +
//                "                   ON `count`.`car_index` = `car`.`car_index`\n" +
//                "GROUP BY `count`.`count_index`";
//
//        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
//            preparedStatement.setInt(1, inputVo.getBranch_index());
//            try (ResultSet resultSet = preparedStatement.executeQuery()) {
//                while (resultSet.next()) {
//                    countVo = new CountVo(
//                            resultSet.getInt("branchIndex"),
//                            resultSet.getInt("carIndex"),
//                            resultSet.getString("carName"),
//                            resultSet.getString("carType"),
//                            resultSet.getInt("totalCar"),
//                            resultSet.getInt("usingCar")
//                    );
//                    counts.add(countVo);
//                }
//            }
//            return counts;
//        }
//    }
}