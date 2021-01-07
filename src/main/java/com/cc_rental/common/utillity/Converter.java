package com.cc_rental.common.utillity;

import com.cc_rental.common.vos.UserVo;

import javax.servlet.http.HttpServletRequest;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Converter {
    private Converter(){}

    public static void setUserVo(HttpServletRequest request, UserVo userVo) {
        request.getSession().setAttribute("UserVo", userVo);
    }

    public static UserVo getUserVo(HttpServletRequest request) {
        Object userVoObject = request.getSession().getAttribute("UserVo");
        UserVo userVo = null;
        if (userVoObject instanceof UserVo) {
            userVo = (UserVo) userVoObject;
        }
        return userVo;
    }

    public static Date dateParsing(String str) {
        return Date.valueOf(str);
    }

    public static Time timeParsing(String str) throws ParseException {
//        return  Time.valueOf(str);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        long longTime = simpleDateFormat.parse(str).getTime();
        return new Time (longTime);
    }
}