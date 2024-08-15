package tech.orbfin.api.productsservices.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import tech.orbfin.api.productsservices.model.request.RequestService;
import tech.orbfin.api.productsservices.model.request.RequestServices;
import tech.orbfin.api.productsservices.model.response.ResponseService;
import tech.orbfin.api.productsservices.model.response.ResponseServiceRequest;
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

    @GetMapping("/type/{type}")
    public ResponseEntity<ResponseServices> servicesByType(@PathVariable("type") String type) throws Exception {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(services.byType(type));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseServices.builder()
                            .errorMessage(e.getMessage())
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseService> service(@PathVariable("id") String id) throws Exception {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(services.byID(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseService.builder()
                            .errorMessage(e.getMessage())
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build());
        }
    }

    @PostMapping("/")
    public ResponseEntity<ResponseServices> servicesBy(@RequestBody RequestServices request) throws Exception {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(services.by(request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseServices.builder()
                            .errorMessage(e.getMessage())
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build());
        }
    }

    @PostMapping("/request/{id}")
    public ResponseEntity<ResponseServiceRequest> request(@PathVariable("id") String id, @RequestBody RequestService request) throws Exception {
        try {
            log.info(String.valueOf(request.getId()));
            log.info(String.valueOf(request.getDate()));
            log.info(String.valueOf(request.getTime()));
            log.info(String.valueOf(request.getAddress()));
            log.info(String.valueOf(request.getCoordinates()));
            return ResponseEntity.status(HttpStatus.CREATED).body(services.request(request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseServiceRequest.builder()
                            .errorMessage(e.getMessage())
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build());
        }
    }
}
