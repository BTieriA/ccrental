package com.ccrental.composite.rental.apis.controllers;

import com.ccrental.composite.common.utillity.Constant;
import com.ccrental.composite.common.utillity.Converter;
import com.ccrental.composite.rental.apis.containers.CarListContainer;
import com.ccrental.composite.rental.apis.containers.RentalResultContainer;
import com.ccrental.composite.rental.apis.enums.RentalGetResult;
import com.ccrental.composite.rental.apis.services.RentalService;
import com.ccrental.composite.rental.apis.vos.AddRentalVo;
import com.ccrental.composite.rental.apis.vos.CountVo;
import com.ccrental.composite.rental.apis.vos.RentalVo;
import com.ccrental.composite.rental.apis.vos.InputVo;
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
                            @RequestParam(name = "branch", defaultValue = "") String strBranch
    ) throws SQLException, ParseException {
        // 시간입력
        Date fromDate = Converter.dateParsing(strFromDate);
        Time fromTime = Converter.timeParsing(strFromTime);
        Date toDate = Converter.dateParsing(strToDate);
        Time toTime = Converter.timeParsing(strToTime);
        int branchIndex = Converter.stringToInt(strBranch);
        InputVo inputVo = new InputVo(fromDate, fromTime, toDate, toTime, branchIndex);

        // 출력
        JSONObject jsonResponse = new JSONObject();
        CarListContainer carListContainer = this.rentalService.getCarList(inputVo);
        JSONArray jsonCars = new JSONArray();
        if (carListContainer.getRentalGetResult() == RentalGetResult.SUCCESS) {
            for(RentalVo rental : carListContainer.getCars()) {
                JSONObject jsonRental = new JSONObject();
                jsonRental.put("carName", rental.getCarName());
                jsonRental.put("carType", rental.getCarType());
                if(rental.isAvailable() == true){
                    jsonRental.put("available", Constant.result.JSON_ENTRY_RESULT_OK);
                } else {
                    jsonRental.put("available", Constant.result.JSON_ENTRY_RESULT_FAIL);
                }
                jsonCars.put(jsonRental);
            }
            jsonResponse.put("result",carListContainer.getRentalGetResult().name().toLowerCase());
            jsonResponse.put("cars", jsonCars);
        }
        return jsonResponse.toString(4);
    }

    @RequestMapping(value = "add")
    public String getRental(HttpServletRequest request, HttpServletResponse response,
                            @RequestParam(name = "from_date", defaultValue = "") String strFromDate,
                            @RequestParam(name = "from_time", defaultValue = "") String strFromTime,
                            @RequestParam(name = "to_date", defaultValue = "") String strToDate,
                            @RequestParam(name = "to_time", defaultValue = "") String strToTime,
                            @RequestParam(name = "branch", defaultValue = "") String strBranch,
                            @RequestParam(name = "car", defaultValue = "") String strCar
    ) throws SQLException, ParseException {
        // 시간입력
        Date fromDate = Converter.dateParsing(strFromDate);
        Time fromTime = Converter.timeParsing(strFromTime);
        Date toDate = Converter.dateParsing(strToDate);
        Time toTime = Converter.timeParsing(strToTime);
        int branchIndex = Converter.stringToInt(strBranch);
        int carIndex = Converter.stringToInt(strCar);
        AddRentalVo addRentalVo = new AddRentalVo(fromDate, fromTime, toDate, toTime, branchIndex, carIndex);

        // 출력
        JSONObject jsonResponse = new JSONObject();
        RentalResultContainer rentalResultContainer = this.rentalService.getRental(addRentalVo);
        JSONArray jsonCounts = new JSONArray();
        if (rentalResultContainer.getRentalGetResult() == RentalGetResult.SUCCESS) {
            for(CountVo count : rentalResultContainer.getCounts()) {
                JSONObject jsonRental = new JSONObject();
                jsonRental.put("carName", count.getCarName());
                jsonRental.put("carType", count.getCarType());
                jsonRental.put("available", Constant.result.JSON_ENTRY_RESULT_OK);
                jsonCounts.put(jsonRental);
            }
            jsonResponse.put("result",rentalResultContainer.getRentalGetResult().name().toLowerCase());
            jsonResponse.put("cars", jsonCounts);
        } else if (rentalResultContainer.getRentalGetResult() == RentalGetResult.SOLDOUT) {
            for(CountVo count : rentalResultContainer.getCounts()) {
                JSONObject jsonRental = new JSONObject();
                jsonRental.put("carName", count.getCarName());
                jsonRental.put("carType", count.getCarType());
                jsonRental.put("available", Constant.result.JSON_ENTRY_RESULT_FAIL);
                jsonCounts.put(jsonRental);
            }
            jsonResponse.put("result",rentalResultContainer.getRentalGetResult().name().toLowerCase());
            jsonResponse.put("cars", jsonCounts);
        }
        return jsonResponse.toString(4);
    }
}