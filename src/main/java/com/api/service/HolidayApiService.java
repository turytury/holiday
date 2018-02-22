package com.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class HolidayApiService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${holiday.api.key}")
    private String apiKey;

    @Value("${holiday.api.url}")
    private String apiUrl;

    @Autowired
    RestTemplate restTemplate;

    public Map<String, Object> getHolidayByYear(String countryCode, String year) {

        Map<String, Object> resultMap = new HashMap();

        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl)
                    .queryParam("key", apiKey)
                    .queryParam("country", countryCode)
                    .queryParam("year", year);

            resultMap = getData(builder);

        } catch (HttpClientErrorException ex){
            log.error("country: " + countryCode + ", year: ", year);
            resultMap = readValue(ex.getResponseBodyAsString());
        }

        return resultMap;
    }

    public Map<String, Object> getHolidayByDate(String countryCode, String date) {

        Map<String, Object> resultMap = new HashMap();

        try {
            String[] dateArr = date.split("-");

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl)
                    .queryParam("key", apiKey)
                    .queryParam("country", countryCode)
                    .queryParam("year", dateArr[0])
                    .queryParam("month", dateArr[1])
                    .queryParam("day", dateArr[2]);

            resultMap = getData(builder);

        } catch (HttpClientErrorException ex){
            log.error("country: " + countryCode + ", date: ", date);
            resultMap = readValue(ex.getResponseBodyAsString());
        }

        return resultMap;
    }

    private Map<String, Object> getData(UriComponentsBuilder builder) throws HttpClientErrorException {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(builder.build().encode().toUriString(), String.class);

        return readValue(response);
    }

    private Map<String, Object> readValue(String content) {
        Map<String, Object> resultMap = new HashMap();
        try {
            ObjectMapper mapper = new ObjectMapper();
            resultMap = mapper.readValue(content, Map.class);
        } catch (IOException ex) {
            log.error(ex.getMessage());
            ex.printStackTrace();
        }
        return resultMap;
    }

    @Bean
    public RestTemplate rest() {
        return new RestTemplate();
    }

}
