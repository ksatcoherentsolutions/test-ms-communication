package com.issoft.rnd.ms2.service.rsocket;

import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class RSocketClientServiceImpl1 implements RSocketClientService {

    private Mono<RSocketRequester> rSocketRequesterMono;

    public RSocketClientServiceImpl1() {
        this.rSocketRequesterMono = RSocketRequester.builder()
                .connectTcp("localhost", 7000).retry(5).cache()
                .doOnError(throwable -> {
                    System.out.println("never happens");
                });
        //how I can catch disconnect and connect again?
    }

    @Override
    public Mono<String> request(String route, String data, Class<String> clazz) {
        return rSocketRequesterMono
                .flatMap(rSocketRequester -> rSocketRequester.route(route).data(data).retrieveMono(clazz));
    }

}
