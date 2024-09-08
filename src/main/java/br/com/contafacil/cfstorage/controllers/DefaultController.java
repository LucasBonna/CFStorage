package br.com.contafacil.cfstorage.controllers;

import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
public class DefaultController {

    @GetMapping("/")
    public ResponseEntity<Map<String, String>> getStatus() {
        Map<String, String> status = Collections.singletonMap("status", "OK");

        return ResponseEntity.ok(status);
    }
}
