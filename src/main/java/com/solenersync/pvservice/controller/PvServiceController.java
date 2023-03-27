package com.solenersync.pvservice.controller;

import com.solenersync.pvservice.model.PvDetails;
import com.solenersync.pvservice.model.SolarArrayRequest;
import com.solenersync.pvservice.service.PvIrradianceService;
import jakarta.websocket.DeploymentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/api/v1/pv")
@RestController
public class PvServiceController {

    private final PvIrradianceService pvIrradianceService;

    public PvServiceController(PvIrradianceService pvIrradianceService) {
        this.pvIrradianceService = pvIrradianceService;
    }

    @PostMapping("/daily")
    public ResponseEntity<List<PvDetails>> getHourlyPv(@RequestBody SolarArrayRequest request) throws DeploymentException {
        log.info("Retrieving solar forecast for user {} ",request.getUserId());
        return pvIrradianceService.getPvIrradiance(request).map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.badRequest().build());
    }
}
