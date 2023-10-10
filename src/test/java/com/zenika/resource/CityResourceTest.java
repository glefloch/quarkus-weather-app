package com.zenika.resource;

import com.zenika.entity.City;
import com.zenika.service.WeatherClient;
import com.zenika.service.model.DailyWeather7Timer;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class CityResourceTest {

    @InjectMock
    @RestClient
    WeatherClient weatherClient;

    @Test
    public void shouldReturnAllCity() {
        var cities = given()
                .when()
                .get("/city")
                .then().statusCode(200)
                .extract().as(City[].class);
        Assertions.assertEquals(3, cities.length);
    }


    @Test
    @TestSecurity(user="test",roles={"admin"})
    public void shouldReturn404OnMissingCity() {
        given()
                .when().get("/city/test")
                .then().statusCode(404);
    }


    @Test
    @TestSecurity(user="test",roles={"admin"})
    public void shouldInsertNewCity() {

        given().body(new City("Rennes"))
                .contentType("application/json")
                .when().post("/city")
                .then()
                .statusCode(201);

        given().body(new City("Rennes"))
                .contentType("application/json")
                .when().post("/city")
                .then()
                .statusCode(409);

     //   given().when().delete("/city/Rennes").then().statusCode(204);
    }


    @Test
    @TestSecurity(user = "test", roles = {"user"})
    public void shouldReturnWeatherForecast() {

        Mockito.when(weatherClient.getWeather(Mockito.any(), Mockito.any())).thenReturn("""
                {
                        "product" : "civillight" ,
                        "init" : "2023101000" ,
                        "dataseries" : [
                        {
                                "date" : 20231010,
                                "weather" : "cloudy",
                                "temp2m" : {
                                        "max" : 25,
                                        "min" : 14
                                },
                                "wind10m_max" : 3
                        },
                        {
                                "date" : 20231011,
                                "weather" : "cloudy",
                                "temp2m" : {
                                        "max" : 23,
                                        "min" : 15
                                },
                                "wind10m_max" : 3
                        },
                        {
                                "date" : 20231012,
                                "weather" : "cloudy",
                                "temp2m" : {
                                        "max" : 22,
                                        "min" : 16
                                },
                                "wind10m_max" : 3
                        },
                        {
                                "date" : 20231013,
                                "weather" : "cloudy",
                                "temp2m" : {
                                        "max" : 26,
                                        "min" : 17
                                },
                                "wind10m_max" : 3
                        },
                        {
                                "date" : 20231014,
                                "weather" : "lightrain",
                                "temp2m" : {
                                        "max" : 17,
                                        "min" : 10
                                },
                                "wind10m_max" : 4
                        },
                        {
                                "date" : 20231015,
                                "weather" : "clear",
                                "temp2m" : {
                                        "max" : 11,
                                        "min" : 7
                                },
                                "wind10m_max" : 3
                        },
                        {
                                "date" : 20231016,
                                "weather" : "cloudy",
                                "temp2m" : {
                                        "max" : 12,
                                        "min" : 8
                                },
                                "wind10m_max" : 3
                        }
                        ]
                }
                """);

        DailyWeather7Timer.DataSeries[] dataSeries = given().when().get("/city/LILLE/weather")
                .then().statusCode(200)
                .extract().as(DailyWeather7Timer.DataSeries[].class);

        Assertions.assertEquals(7, dataSeries.length);

    }

}
