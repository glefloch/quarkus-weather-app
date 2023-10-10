package com.zenika.event;

import jakarta.persistence.criteria.CriteriaBuilder;

import java.time.Instant;

public class ConsultationEvent {

    public String city;
    public Instant instant;

    public ConsultationEvent(String city, Instant instant) {
        this.city = city;
        this.instant = instant;
    }

}
