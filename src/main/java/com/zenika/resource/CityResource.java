package com.zenika.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zenika.entity.City;
import com.zenika.event.ConsultationEvent;
import com.zenika.service.WeatherService;
import com.zenika.service.model.DailyWeather7Timer;
import io.quarkus.cache.CacheResult;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.hibernate.SessionFactory;
import org.jboss.resteasy.reactive.Cache;
import org.jboss.resteasy.reactive.RestPath;

import java.net.URI;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


@OpenAPIDefinition(info = @Info(title = "Api permettant de gérer des villes", version = "1.0.0"))
@Path("/city")
public class CityResource {

    private final WeatherService weatherService;
    private final Emitter<ConsultationEvent> consultationEventEmitter;

    public CityResource(WeatherService weatherService, @Channel("consultation-event") Emitter<ConsultationEvent> consultationEventEmitter) {
        this.weatherService = weatherService;
        this.consultationEventEmitter = consultationEventEmitter;
    }

    @Timed
    @GET
    public Collection<City> getAll() {
        return City.listAll();
    }


    @Timed
    @GET
    @RolesAllowed("admin")
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
    @Authenticated
    public List<DailyWeather7Timer.DataSeries> getWeather(@PathParam("name") String name) throws JsonProcessingException {
        consultationEventEmitter.send(new ConsultationEvent(name, Instant.now()));
        return weatherService.getWeather(name);
    }

    @Timed
    @POST
    @RolesAllowed("admin")
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
    @RolesAllowed("admin")
    @Path("/{name}")
    public void deleteCity(@RestPath String name) {
       City.delete("name", name);
    }
}
