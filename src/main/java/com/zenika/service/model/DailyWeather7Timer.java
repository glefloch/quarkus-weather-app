package com.zenika.service.model;

import java.util.ArrayList;
import java.util.List;

public class DailyWeather7Timer {
    public List<DataSeries> dataseries = new ArrayList<>();
    public static class DataSeries {
        public String date;
        public Temp2m temp2m;
        public String weather;
        public Double wind10_max;
    }
    public static class Temp2m {
        public Double min;
        public Double max;
    }
}