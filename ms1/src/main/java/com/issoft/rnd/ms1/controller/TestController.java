package com.issoft.rnd.ms1.controller;

import com.issoft.rnd.ms1.service.echo.EchoService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
public class TestController {

    private EchoService echoService;

    @GetMapping("/test/{value}")
    public Mono<String> test(@PathVariable String value) {
        return echoService.echo(value);
    }

}
