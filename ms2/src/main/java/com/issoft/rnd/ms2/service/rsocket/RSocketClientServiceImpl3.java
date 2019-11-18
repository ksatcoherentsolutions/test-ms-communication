package com.issoft.rnd.ms2.service.rsocket;

import io.rsocket.RSocketFactory;
import io.rsocket.client.LoadBalancedRSocketMono;
import io.rsocket.client.filter.RSocketSupplier;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.metadata.WellKnownMimeType;
import io.rsocket.transport.netty.client.TcpClientTransport;
import org.springframework.http.MediaType;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;

//@Component
public class RSocketClientServiceImpl3 implements RSocketClientService {

    private RSocketRequester rSocketRequester;

    public RSocketClientServiceImpl3(RSocketStrategies rSocketStrategies) {
        connect(rSocketStrategies);
    }

    private void connect(RSocketStrategies rSocketStrategies) {
        LoadBalancedRSocketMono.create(Flux.just(Collections.singleton(new RSocketSupplier(() ->
                        RSocketFactory
                                .connect()
                                .dataMimeType(MediaType.APPLICATION_CBOR_VALUE)
                                .metadataMimeType(WellKnownMimeType.MESSAGE_RSOCKET_COMPOSITE_METADATA.getString())
                                .frameDecoder(PayloadDecoder.ZERO_COPY)
                                .transport(TcpClientTransport.create("localhost", 7000))
                                .start()
                                .doOnNext(rSocket -> {
                                    this.rSocketRequester = RSocketRequester.wrap(rSocket, MediaType.APPLICATION_CBOR, MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_COMPOSITE_METADATA.getString()), rSocketStrategies);
                                })
                )))
        ).subscribe();
    }


    @Override
    public Mono<String> request(String route, String data, Class<String> clazz) {
        return rSocketRequester.route(route).data(data).retrieveMono(clazz);
    }


}
