package com.api.controller;

import com.api.service.HolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api")
public class HolidayController {

    @Autowired
    HolidayService holidayService;

    @RequestMapping("/upcoming")
    public ResponseEntity upcoming(@RequestParam(value="countryCode1") String countryCode1,
                                   @RequestParam(value="countryCode2") String countryCode2,
                                   @RequestParam(value="date") String date) {

        return holidayService.getSameUpComingHoliday(countryCode1, countryCode2, date);
    }
}
