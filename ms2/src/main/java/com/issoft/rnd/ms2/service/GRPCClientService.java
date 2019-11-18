package com.issoft.rnd.ms2.service;

import com.issoft.rnd.proto.Echo;
import com.issoft.rnd.proto.EchoServiceGrpc;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import lombok.val;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class GRPCClientService {

    private EchoServiceGrpc.EchoServiceStub stub;

    public GRPCClientService() {
        val managedChannel = ManagedChannelBuilder.forAddress("localhost", 8888).usePlaintext().build();
        stub = EchoServiceGrpc.newStub(managedChannel);
    }

    public Mono<String> echo(String value) {
        return Mono.create(sink -> {
            val responseObserver = new StreamObserver<Echo.EchoResponse>() {
                @Override
                public void onNext(Echo.EchoResponse echoResponse) {
                    sink.success(echoResponse.getValue());
                }

                @Override
                public void onError(Throwable throwable) {

                }

                @Override
                public void onCompleted() {

                }
            };
            stub.echo(Echo.EchoRequest.newBuilder().setValue(value).build(), responseObserver);
        });
    }

}
