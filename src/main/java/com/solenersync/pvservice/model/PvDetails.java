package com.solenersync.pvservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PvDetails {

    private Integer month;
    private String time;
    @JsonProperty("G(i)")
    private float globalIrradiance;
    @JsonProperty("Gb(i)")
    private float directIrradiance;
    @JsonProperty("Gd(i)")
    private float diffuseIrradiance;
    @JsonProperty("Gcs(i)")
    private float clearSkyIrradiance;


}
