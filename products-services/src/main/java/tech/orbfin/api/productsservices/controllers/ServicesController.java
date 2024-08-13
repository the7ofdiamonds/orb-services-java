package tech.orbfin.api.productsservices.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tech.orbfin.api.productsservices.model.response.ResponseServices;

import tech.orbfin.api.productsservices.services.Services;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
@Component
public class ServicesController {
    public final Services services;
    @GetMapping("/")
    public ResponseEntity<ResponseServices> services() throws Exception {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(services.all());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseServices.builder()
                            .errorMessage(e.getMessage())
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build());
        }
    }
}
