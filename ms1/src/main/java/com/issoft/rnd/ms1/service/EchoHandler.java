package com.issoft.rnd.ms1.service;

import com.issoft.rnd.ms1.service.echo.EchoService;
import com.issoft.rnd.proto.Echo;
import com.issoft.rnd.proto.EchoServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Component;

@Component
public class EchoHandler extends EchoServiceGrpc.EchoServiceImplBase {

    private EchoService echoService;

    public EchoHandler(EchoService echoService) {
        this.echoService = echoService;
    }

    @Override
    public void echo(Echo.EchoRequest request, StreamObserver<Echo.EchoResponse> responseObserver) {
        echoService.echo(request.getValue()).subscribe(value -> {
            responseObserver.onNext(Echo.EchoResponse.newBuilder().setValue(value).build());
            responseObserver.onCompleted();
        });
    }
}
