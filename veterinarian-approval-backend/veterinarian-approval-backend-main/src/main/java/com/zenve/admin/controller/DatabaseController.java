package com.zenve.admin.controller;

import com.zenve.admin.dto.DatabaseInfoResponse;
import com.zenve.admin.service.DatabaseInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/database")
public class DatabaseController {

    private final DatabaseInfoService databaseInfoService;

    public DatabaseController(DatabaseInfoService databaseInfoService) {
        this.databaseInfoService = databaseInfoService;
    }

    @GetMapping
    public DatabaseInfoResponse info() {
        return databaseInfoService.getInfo();
    }
}
