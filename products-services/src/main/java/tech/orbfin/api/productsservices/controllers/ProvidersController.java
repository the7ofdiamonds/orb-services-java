package tech.orbfin.api.productsservices.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Component;

import org.springframework.web.bind.annotation.*;

import tech.orbfin.api.productsservices.model.request.RequestProvider;
import tech.orbfin.api.productsservices.model.request.RequestProviders;

import tech.orbfin.api.productsservices.model.response.ResponseProviderRequest;
import tech.orbfin.api.productsservices.model.response.ResponseProvidersRequest;

import tech.orbfin.api.productsservices.services.Providers;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Component
public class ProvidersController {
    public final Providers providers;

    @PostMapping(value = "/providers", consumes = "application/json")
    public ResponseEntity<ResponseProvidersRequest> providers(@RequestBody RequestProviders request) throws Exception {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(providers.requestProviders(request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseProvidersRequest.builder()
                            .errorMessage(e.getMessage())
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build());
        }
    }

    @PostMapping(value = "/providers/{id}", consumes = "application/json")
    public ResponseEntity<ResponseProviderRequest> provider(@PathVariable("id") Integer id, @RequestBody RequestProvider request) throws Exception {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(providers.requestProvider(request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseProviderRequest.builder()
                            .errorMessage(e.getMessage())
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build());
        }
    }
}
