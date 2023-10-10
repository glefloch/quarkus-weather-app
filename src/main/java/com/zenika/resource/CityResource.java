package com.zenika.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zenika.entity.City;
import com.zenika.service.WeatherService;
import com.zenika.service.model.DailyWeather7Timer;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.hibernate.SessionFactory;
import org.jboss.resteasy.reactive.Cache;
import org.jboss.resteasy.reactive.RestPath;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


@OpenAPIDefinition(info = @Info(title = "Api permettant de gérer des villes", version = "1.0.0"))
@Path("/city")
public class CityResource {

    private final WeatherService weatherService;

    public CityResource(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @Timed
    @GET
    public Collection<City> getAll() {
        return City.listAll();
    }


    @Timed
    @GET
    @Path("/{name}")
    public Response getByName(@PathParam("name") String name) {
        Optional<PanacheEntityBase> result = City.find("name", name).firstResultOptional();
        if (result.isPresent()) {
            return Response.ok(result.get()).build();
        }
        return Response.status(404).build();
    }


    @Timed
    @GET
    @Path("/{name}/weather")
    @Cache
    public List<DailyWeather7Timer.DataSeries> getWeather(@PathParam("name") String name) throws JsonProcessingException {
        return weatherService.getWeather(name);
    }

    @Timed
    @POST
    @Transactional
    public Response createCity(City city) {
        Optional<PanacheEntityBase> baseCity = City.find("name", city.name).firstResultOptional();
        if (baseCity.isEmpty()) {
            City.persist(city);
            return Response.created(URI.create("/city/" + city.name)).build();
        }
        return Response.status(409).build();
    }

    @DELETE
    @Transactional
    @Path("/{name}")
    public void deleteCity(@RestPath String name) {
       City.delete("name", name);
    }
}
