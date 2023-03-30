package com.solenersync.pvservice.model;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeatherRequest {

    private float lat;
    private float lon;

}