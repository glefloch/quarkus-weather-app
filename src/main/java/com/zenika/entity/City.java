package com.zenika.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class City extends PanacheEntityBase {

    @Id
    public String name;
    public Double longitude;
    public Double latitude;

    public City() {}

    public City(String name) {
        this.name = name;
    }

}
