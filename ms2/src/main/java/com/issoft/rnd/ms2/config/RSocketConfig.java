package com.issoft.rnd.ms2.config;

import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.metadata.WellKnownMimeType;
import io.rsocket.transport.ClientTransport;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.client.WebsocketClientTransport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.util.MimeTypeUtils;

import java.net.URI;

@Configuration
public class RSocketConfig {

    //@Bean
    RSocketRequester rSocketRequester(RSocketStrategies rSocketStrategies) {
        //ClientTransport clientTransport = WebsocketClientTransport.create(URI.create("ws://localhost:8081/rsocket"));
        return RSocketRequester.builder()
                .rsocketStrategies(rSocketStrategies)
                //.connect(clientTransport)
                .connectTcp("localhost", 7000)
                .cache()
                .block();
    }

    //@Bean
    RSocket rSocket() {
        return RSocketFactory
                .connect()
                .dataMimeType(MediaType.APPLICATION_CBOR_VALUE)
                .metadataMimeType(WellKnownMimeType.MESSAGE_RSOCKET_COMPOSITE_METADATA.getString())
                .transport(TcpClientTransport.create(7000))
                .start()
                .block();
    }

    //@Bean
    RSocketRequester rSocketRequester2(RSocketStrategies rSocketStrategies) {
        return RSocketRequester.wrap(rSocket(), MediaType.APPLICATION_CBOR, MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_COMPOSITE_METADATA.getString()), rSocketStrategies);
    }
}
