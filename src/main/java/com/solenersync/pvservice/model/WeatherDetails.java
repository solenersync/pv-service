package com.solenersync.pvservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class WeatherDetails {

    private String date;
    private String time;
    private int lowCloud;
    private int midCloud;
    private int highCloud;
    private int maxCloudCover;

}
