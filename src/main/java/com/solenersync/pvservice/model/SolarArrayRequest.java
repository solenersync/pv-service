package com.solenersync.pvservice.model;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SolarArrayRequest {

    private Integer userId;
    private float lat;
    private float lon;
    private float peakpower;
    private float loss;
    private float angle;
    private float aspect;
    private int month;
    private Mounting mounting;

}