package tech.orbfin.api.productsservices.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Component;

import org.springframework.web.bind.annotation.*;

import tech.orbfin.api.productsservices.model.request.RequestService;
import tech.orbfin.api.productsservices.model.response.ResponseServiceRequest;

import tech.orbfin.api.productsservices.services.Providers;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Component
public class ProvidersController {
    public final Providers providers;

    @PostMapping(value = "/request/{id}", consumes = "application/json")
    public ResponseEntity<ResponseServiceRequest> request(@PathVariable("id") Integer id, @RequestBody RequestService request) throws Exception {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(providers.request(request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseServiceRequest.builder()
                            .errorMessage(e.getMessage())
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build());
        }
    }
}
