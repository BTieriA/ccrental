package com.cc_rental.rental.apis.controllers;

import com.cc_rental.common.utillity.Converter;
import com.cc_rental.rental.apis.containers.RentalResultContainer;
import com.cc_rental.rental.apis.enums.RentalGetResult;
import com.cc_rental.rental.apis.services.RentalService;
import com.cc_rental.rental.apis.vos.AddRentalVo;
import com.cc_rental.rental.apis.vos.RentalVo;
import com.cc_rental.rental.utilities.Constant;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;


@RestController
@RequestMapping(
        value = "/apis/rental/",
        method = {RequestMethod.GET, RequestMethod.POST},
        produces = MediaType.APPLICATION_JSON_VALUE)
public class RentalController {
    private final RentalService rentalService;

    @Autowired
    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @RequestMapping(value = "get")
    public String getRental(HttpServletRequest request, HttpServletResponse response,
                            @RequestParam(name = "from_date", defaultValue = "") String strFromDate,
                            @RequestParam(name = "from_time", defaultValue = "") String strFromTime,
                            @RequestParam(name = "to_date", defaultValue = "") String strToDate,
                            @RequestParam(name = "to_time", defaultValue = "") String strToTime,
                            @RequestParam(name = "branch", defaultValue = "") int branchIndex
    ) throws SQLException, ParseException {
//       시간입력
        Date fromDate = Converter.dateParsing(strFromDate);
        Time fromTime = Converter.timeParsing(strFromTime);
        Date toDate = Converter.dateParsing(strToDate);
        Time toTime = Converter.timeParsing(strToTime);
        AddRentalVo addRentalVo = new AddRentalVo(fromDate, fromTime, toDate, toTime, branchIndex);
        RentalGetResult rentalGetResult = this.rentalService.addRental(addRentalVo);
        JSONObject jsonResponse = new JSONObject();

//        출력
        RentalResultContainer rentalResultContainer = this.rentalService.getRental(addRentalVo);
        if (rentalResultContainer.getRentalGetResult() == rentalGetResult) {
            JSONArray jsonRentals = new JSONArray();
            for(RentalVo rental : rentalResultContainer.getRentals()) {
                JSONObject jsonRental = new JSONObject();
                jsonRental.put("carName", rental.getCarName());
                jsonRental.put("carType", rental.getCarType());
                jsonRental.put("available", "Available");
                jsonRentals .put(jsonRental);
            }
            jsonResponse.put("rentals", jsonRentals);
        }
        return jsonResponse.toString(4);
    }
}