package com.issoft.rnd.ms1.controller;

import com.issoft.rnd.ms1.service.echo.EchoService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
@AllArgsConstructor
public class RTestController {

    private EchoService echoService;

    @MessageMapping("test")
    Mono<String> test(String value) {
        return echoService.echo(value);
    }
}
