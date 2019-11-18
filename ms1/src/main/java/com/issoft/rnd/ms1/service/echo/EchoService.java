package com.issoft.rnd.ms1.service.echo;

import reactor.core.publisher.Mono;

public interface EchoService {

    Mono<String> echo(String value);
}
