package com.redis.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.redis.dto.JwtResponse;
import com.redis.entity.Overalldata;
import com.redis.handler.DataNotFoundException;
import com.redis.service.Logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Middleware controller (GET-only):
 * - GET /basepath/overallexampledatabytable?id=&passw=   -> fetches data (your existing flow)
 * - GET /basepath/login?id=&passw=                       -> validates & returns JwtResponse
 */
@CrossOrigin
@RestController
@RequestMapping("/basepath")
public class Tokencontroller {

    @Autowired
    private Logic employeeService;

    // ---- Existing endpoint (kept as-is, returns data list) ----
    @GetMapping("/overallexampledatabytable")
    public ResponseEntity<Object> getsomedata(@RequestParam int id, @RequestParam String passw) {
        try {
            List<Overalldata> data = employeeService.fetchSomeDataFromDBAndCache(id, passw);
            return ResponseEntity.ok(data);
        } catch (DataNotFoundException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("message", ex.getMessage());
            // Keeping your original NOT_FOUND behavior here
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    // ---- Login via GET (since you don't have POST access) -> returns JWT ----
    @GetMapping("/login")
    public ResponseEntity<Object> loginViaQuery(@RequestParam("id") int id,
                                                @RequestParam("passw") String passw) {
        try {
            // Fetching the JWT token for the user
            JwtResponse jwt = employeeService.loginAndIssueToken(id, passw);
            
            if (jwt == null) {
                // If JWT generation failed, return an error response
                Map<String, String> error = new HashMap<>();
                error.put("message", "JWT generation failed.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
            }

            // Prevent caching of auth responses
            return ResponseEntity.ok()
                    .header(HttpHeaders.CACHE_CONTROL, "no-store")
                    .body(jwt);
        } catch (DataNotFoundException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (Exception ex) {
            // General exception handling in case something unexpected happens
            Map<String, String> error = new HashMap<>();
            error.put("message", "An error occurred during login.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
