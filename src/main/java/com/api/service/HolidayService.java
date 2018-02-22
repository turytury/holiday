package com.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class HolidayService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    HolidayApiService holidayApiService;

    public ResponseEntity getSameUpComingHoliday(String countryCode1, String countryCode2, String date){

        Map<String, Object> resultMap = new HashMap();

        try {
            // Validate parameter
            if(StringUtils.isEmpty(countryCode1)) return getEmptyErrMsg("countryCode1");
            if(StringUtils.isEmpty(countryCode2)) return getEmptyErrMsg("countryCode2");
            if(StringUtils.isEmpty(date)) return getEmptyErrMsg("date");

            Date givenDate = sdf.parse(date);

            String[] dateArr = date.split("-");
            String year = dateArr[0];

            // Get holiday data for country 1
            Map<String, Object> country1Map = holidayApiService.getHolidayByYear(countryCode1, year);
            if(isError(country1Map)) return getErrorMsg(country1Map);

            // Get holiday data for country 2
            Map<String, Object> country2Map = holidayApiService.getHolidayByYear(countryCode2, year);
            if(isError(country2Map)) return getErrorMsg(country2Map);

            resultMap = findSameHoliday(givenDate, countryCode1, countryCode2, country1Map, country2Map);

            /** Assume that every country have the same New Year's Day */
            if(resultMap.isEmpty()) resultMap = findNewYearDay(year, countryCode1, countryCode2);

        } catch (ParseException ex){
            log.error(ex.getMessage());
            ex.printStackTrace();
            return getErrorMsg("400", "Date format must be yyyy-MM-dd");
        }

        return new ResponseEntity(resultMap, HttpStatus.OK);
    }

    private Map<String, Object> findSameHoliday(Date givenDate, String countryCode1, String countryCode2,
                                                Map<String, Object> map1, Map<String, Object> map2) throws ParseException{

        Map<String, Object> resultMap = new HashMap();
        LinkedHashMap<String, Object> holiday1Map = (LinkedHashMap<String, Object>) map1.get("holidays");
        LinkedHashMap<String, Object> holiday2Map = (LinkedHashMap<String, Object>) map2.get("holidays");

        for (String key : holiday1Map.keySet()) {
            Date holiday = sdf.parse(key);
            if(givenDate.before(holiday) && (holiday2Map.get(key) != null)){
                resultMap.put("date", key);
                resultMap.put(countryCode1, getHolidayName(holiday1Map.get(key)));
                resultMap.put(countryCode2, getHolidayName(holiday2Map.get(key)));
                break;
            }
        }

        return resultMap;
    }

    private Map<String, Object> findNewYearDay(String year, String countryCode1, String countryCode2) throws ParseException{
        Map<String, Object> resultMap = new HashMap();
        String newYearDate = Integer.parseInt(year) + 1 + "-01-01";

        Map<String, Object> newYear1Map = holidayApiService.getHolidayByDate(countryCode1, newYearDate);
        Map<String, Object> newYear2Map = holidayApiService.getHolidayByDate(countryCode2, newYearDate);

        // If Error, skip this method
        if(!isError(newYear1Map) && !isError(newYear2Map)) {
            resultMap.put("date", newYearDate);
            resultMap.put(countryCode1, getHolidayName(newYear1Map.get("holidays")));
            resultMap.put(countryCode2, getHolidayName(newYear2Map.get("holidays")));
        }

        return resultMap;
    }

    private String getHolidayName(Object obj){
        ArrayList<HashMap<String, Object>> holidayList = (ArrayList) obj;
        String name = holidayList.get(0).get("name").toString();

        if(holidayList.size() > 1){
            boolean isPublic;
            for(HashMap<String, Object> map : holidayList){
                isPublic = (Boolean) map.get("public");
                if(isPublic){
                    name = map.get("name").toString();
                    break;
                }
            }
        }
        return name;
    }

    private boolean isError(Map<String, Object> map){
        Integer statusCode = (Integer) map.get("status");
        if(statusCode == 200){
            return false;
        }
        return true;
    }

    private ResponseEntity getErrorMsg(Map<String, Object> map){
        return getErrorMsg(map.get("status").toString(), map.get("error").toString());
    }

    private ResponseEntity getErrorMsg(String status, String error){
        Map<String, Object> result = new HashMap();
        result.put("status", status);
        result.put("error", error);
        return new ResponseEntity(result, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity getEmptyErrMsg(String param){
        return getErrorMsg("400", param + " parameter is required.");
    }
}
