package com.issoft.rnd.ms1.service.echo;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class DelayedEchoServiceImpl implements EchoService {

    @Override
    public Mono<String> echo(String value) {
        return Mono.delay(Duration.ofSeconds(3)).then(Mono.just("delayed echo " + value));
    }
}
