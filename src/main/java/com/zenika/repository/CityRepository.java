package com.zenika.repository;

import com.zenika.entity.City;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class CityRepository implements PanacheRepository<City> {

    public Optional<City> findByName(String name) {
        return find("name", name).firstResultOptional();
    }

}
