package com.solenersync.pvservice.controller;

import com.solenersync.pvservice.model.PvDetails;
import com.solenersync.pvservice.model.SolarArrayRequest;
import com.solenersync.pvservice.service.PvIrradianceService;
import jakarta.websocket.DeploymentException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/pv")
@RestController
public class PvServiceController {

//    @Autowired
    private final PvIrradianceService pvIrradianceService;

    public PvServiceController(PvIrradianceService pvIrradianceService) {
        this.pvIrradianceService = pvIrradianceService;
    }

    @CrossOrigin
    @PostMapping("/daily")
    public ResponseEntity<List<PvDetails>> getHourlyPv(@RequestBody SolarArrayRequest request) throws DeploymentException {
        return pvIrradianceService.getPvIrradiance(request).map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
