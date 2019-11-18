package com.issoft.rnd.ms2.service.rsocket;

import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.metadata.WellKnownMimeType;
import io.rsocket.transport.netty.client.TcpClientTransport;
import org.springframework.http.MediaType;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Mono;

//@Component
public class RSocketClientServiceImpl2 implements RSocketClientService {

    private RSocketRequester rSocketRequester;

    public RSocketClientServiceImpl2(RSocketStrategies rSocketStrategies) {
        connect(rSocketStrategies);
    }

    private void connect(RSocketStrategies rSocketStrategies) {
        RSocket rSocket = RSocketFactory
                .connect()
                .dataMimeType(MediaType.APPLICATION_CBOR_VALUE)
                .metadataMimeType(WellKnownMimeType.MESSAGE_RSOCKET_COMPOSITE_METADATA.getString())
                .transport(TcpClientTransport.create(7000))
                .start()
                .block();
        this.rSocketRequester = RSocketRequester.wrap(rSocket, MediaType.APPLICATION_CBOR, MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_COMPOSITE_METADATA.getString()), rSocketStrategies);
    }

    @Override
    public Mono<String> request(String route, String data, Class<String> clazz) {
        System.out.println(rSocketRequester.dataMimeType() + " " + rSocketRequester.metadataMimeType());
        return rSocketRequester.route(route).data(data).retrieveMono(clazz);
    }
}
