package com.issoft.rnd.ms2.service.rsocket;

import reactor.core.publisher.Mono;

public interface RSocketClientService {
    Mono<String> request(String route, String data, Class<String> clazz);
}
