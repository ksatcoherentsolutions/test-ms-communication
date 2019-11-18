package com.issoft.rnd.ms1.service.echo;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

//@Component
public class EchoServiceImpl implements EchoService {

    @Override
    public Mono<String> echo(String value) {
        return Mono.just("echo " + value);
    }
}
