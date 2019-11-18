package com.issoft.rnd.ms1.service;

import com.issoft.rnd.ms1.service.echo.EchoService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

@Component
public class WSEchoHandler implements WebSocketHandler {

    private EchoService echoService;

    public WSEchoHandler(EchoService echoService) {
        this.echoService = echoService;
    }

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        return webSocketSession.send(
                webSocketSession.receive()
                        .map(WebSocketMessage::getPayloadAsText)
                        .flatMap(message -> {
                            int index = message.indexOf("||");
                            String id = message.substring(0, index);
                            return echoService.echo(message.substring(index + 2)).map(mes -> id + "||" + mes);
                        })
                        .map(webSocketSession::textMessage)
        );
    }

}
