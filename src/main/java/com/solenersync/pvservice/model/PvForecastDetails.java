package com.solenersync.pvservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PvForecastDetails {

    private String time;
    private String date;
    @JsonProperty("G(i)")
    private float globalIrradiance;
    @JsonProperty("Gb(i)")
    private float directIrradiance;
    @JsonProperty("Gd(i)")
    private float diffuseIrradiance;
    @JsonProperty("Gcs(i)")
    private float clearSkyIrradiance;
    private float peakGlobalOutput;
    private int lowCloud;
    private int midCloud;
    private int highCloud;
    private int maxCloudCover;
    private float totalPowerOutput;


}
