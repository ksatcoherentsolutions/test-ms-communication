package com.issoft.rnd.ms1.service;

import io.grpc.ServerBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GRPCServer {

    public GRPCServer(EchoHandler echoHandler) throws IOException {
        ServerBuilder.forPort(8888)
                .addService(echoHandler)
                .build()
                .start();
    }
}
