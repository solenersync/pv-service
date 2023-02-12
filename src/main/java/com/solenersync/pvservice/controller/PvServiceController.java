package com.solenersync.pvservice.controller;

import com.solenersync.pvservice.model.PvDetails;
import com.solenersync.pvservice.model.SolarArrayRequest;
import com.solenersync.pvservice.service.PvIrradianceService;
import jakarta.websocket.DeploymentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/pv")
@RestController
public class PvServiceController {

    @Autowired
    PvIrradianceService pvIrradianceService;

    @PostMapping("/daily")
    public List<PvDetails> getHourlyPv(@RequestBody SolarArrayRequest request) throws DeploymentException {
        return pvIrradianceService.getPvIrradiance(request);
    }
}
