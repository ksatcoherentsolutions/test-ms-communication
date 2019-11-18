package com.issoft.rnd.ms2.service;

import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.FutureMono;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WSClientService {

    private static final Logger LOG = LoggerFactory.getLogger(WSClientService.class);

    private String uriString = "http://localhost:8081/ws/feed";

    private Map<String, CompletableFuture<String>> storage = new ConcurrentHashMap<>();

    private WebSocketSession session;

    public WSClientService() {
        connect().subscribe();
    }

    private Mono<Void> connect() {
        return new ReactorNettyWebSocketClient().execute(UriComponentsBuilder.fromUriString(uriString).build().toUri(), webSocketSession -> {
            session = webSocketSession;

            return webSocketSession.receive().map(WebSocketMessage::getPayloadAsText)
                    .doOnNext(message -> {
                        val index = message.indexOf("||");
                        storage.remove(message.substring(0, index)).complete(message.substring(index + 2));
                    }).then();
        }).doOnError(exception ->
                Mono.just(exception.getMessage())
                        .log()
                        .delayElement(Duration.ofSeconds(3))
                        .then(connect())
                        .subscribe()
        );
    }

    public Mono<String> send(Flux<String> input) {
        val completableFuture = new CompletableFuture<String>();
        val id = UUID.randomUUID().toString();
        storage.put(id, completableFuture);
        return session.send(input.map(value -> session.textMessage(id + "||" + value)))
                .then(FutureMono.fromFuture(completableFuture));
    }

    public Mono<String> echo(Flux<String> input) {
        return Mono.create(sink -> {
            new ReactorNettyWebSocketClient().execute(UriComponentsBuilder.fromUriString(uriString).build().toUri(), webSocketSession ->
                    webSocketSession.send(input.map(webSocketSession::textMessage)).and(
                            webSocketSession.receive()
                                    .map(WebSocketMessage::getPayloadAsText)
                                    .doOnNext(sink::success)
                                    .then(webSocketSession.close())
                    )
            ).subscribe();
        });
    }

}
