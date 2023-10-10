package com.zenika.service;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "weather_service")
public interface WeatherClient {


    @GET
    String getWeather(@QueryParam("lat") Double latitude, @QueryParam("lon") Double longitude);

}
