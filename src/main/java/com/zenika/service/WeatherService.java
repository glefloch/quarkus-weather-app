package com.zenika.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenika.entity.City;
import com.zenika.service.model.DailyWeather7Timer;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class WeatherService {

    private final WeatherClient weatherClient;
    private final ObjectMapper objectMapper;

    public WeatherService(@RestClient WeatherClient weatherClient, ObjectMapper objectMapper) {
        this.weatherClient = weatherClient;
        this.objectMapper = objectMapper;
    }


    public List<DailyWeather7Timer.DataSeries> getWeather(String name) throws JsonProcessingException {
        Optional<City> city = City.find("name", name).firstResultOptional();
        if (city.isEmpty()){
            return null;
        }
        String weather = weatherClient.getWeather(city.get().latitude, city.get().longitude);
        Log.info("Weather Client response "+ weather);

        DailyWeather7Timer dailyWeather7Timer = objectMapper.readValue(weather, DailyWeather7Timer.class);
        return dailyWeather7Timer.dataseries;
    }

}
