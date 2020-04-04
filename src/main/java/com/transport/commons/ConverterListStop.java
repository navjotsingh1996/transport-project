package com.transport.commons;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.transport.services.invoicing.models.Stop;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Local;
import org.json.JSONObject;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Converter
@Slf4j
public class ConverterListStop implements AttributeConverter<List<Stop>, String> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Stop> attribute) {
        List<String> stopJsonString = new ArrayList<>();
        attribute.forEach(stop -> {
            try {
                stopJsonString.add(URLEncoder.encode(mapper.writeValueAsString(stop),
                        java.nio.charset.StandardCharsets.UTF_8.toString()));
            } catch (Exception e) {
                log.error("Unable to stringify stop to DB", e);
            }
        });
        return String.join(",", stopJsonString);
    }

    private String getDate(JSONObject date) throws Exception {
        String year = Integer.toString(date.getInt("year"));
        String month = Integer.toString(date.getInt("monthValue"));
        String day = Integer.toString(date.getInt("dayOfMonth"));

        if (month.length() == 1) {
            month = '0' + month;
        }
        if (day.length() == 1) {
            day = '0' + day;
        }

        return year + '-' + month + '-' + day;
    }

    @Override
    public List<Stop> convertToEntityAttribute(String dbData) {
        List<String> stopJsonString = Arrays.asList(dbData.split(","));
        List<Stop> realData = new ArrayList<>();
        stopJsonString.forEach(stop -> {
            try {
                JSONObject str = new JSONObject(URLDecoder.decode(stop,
                        java.nio.charset.StandardCharsets.UTF_8.toString()));
                JSONObject dateStr = str.getJSONObject("date");
                String dateFormatted = getDate(dateStr);
                log.error(dateFormatted);
                LocalDate date = LocalDate.parse(dateFormatted);
                Stop realStop = new Stop(date, str.getString("name"), str.getString("city"),
                        str.getString("state"), str.getInt("zip"), Stop.StopType.valueOf(str.getString("type")));
                realData.add(realStop);
            } catch (Exception e) {
                log.error("Unable to unstringify stop from DB", e);

            }
        });
        return realData;

    }

}
