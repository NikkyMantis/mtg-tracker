package com.nikkyev00.mtg_tracker;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

        @GetMapping("/hello")
        public String hello() {
            return "Hello, MTG world!";
        }
}
