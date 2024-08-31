package tech.orbfin.api.productsservices.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Component;

import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/services")
    public ResponseEntity<ResponseServices> servicesAll() throws Exception {
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

    @GetMapping("/services/type/{type}")
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

    @GetMapping("/services/{id}")
    public ResponseEntity<ResponseService> service(@PathVariable("id") Long id) throws Exception {
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

    @PostMapping("/services/{id}/request")
    public ResponseEntity<ResponseServiceRequest> serviceRequest(@RequestBody RequestServices request) throws Exception {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(services.requestServices(request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseServiceRequest.builder()
                            .errorMessage(e.getMessage())
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build());
        }
    }
}
