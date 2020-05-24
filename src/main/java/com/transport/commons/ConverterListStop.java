package com.transport.commons;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transport.services.invoicing.models.Stop;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.net.URLDecoder;
import java.net.URLEncoder;
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
                throw new IllegalStateException("Unable to write stop data for stop: " + stop);
            }
        });
        return String.join(",", stopJsonString);
    }

    @Override
    public List<Stop> convertToEntityAttribute(String dbData) {
        List<String> stopJsonString = Arrays.asList(dbData.split(","));
        List<Stop> realData = new ArrayList<>();
        stopJsonString.forEach(stop -> {
            try {
                JSONObject str = new JSONObject(URLDecoder.decode(stop,
                        java.nio.charset.StandardCharsets.UTF_8.toString()));
                String streetAddress = "";
                if (str.has("streetAddress")) {
                    streetAddress = str.getString("streetAddress");
                }
                Stop realStop = new Stop(str.getLong("date"), str.getString("name"), str.getString("city"),
                        str.getString("state"), streetAddress, str.getInt("zip"),
                        Stop.StopType.valueOf(str.getString("type")));
                realData.add(realStop);
            } catch (Exception e) {
                log.error("Unable to unstringify stop from DB", e);
                throw new IllegalStateException("Unable to read stop data");
            }
        });
        return realData;

    }

}
