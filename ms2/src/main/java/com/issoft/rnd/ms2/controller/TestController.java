package com.issoft.rnd.ms2.controller;

import com.issoft.rnd.ms2.service.GRPCClientService;
import com.issoft.rnd.ms2.service.WSClientService;
import com.issoft.rnd.ms2.service.rsocket.RSocketClientService;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@RestController
public class TestController {

    private WebClient webClient;

    private GRPCClientService grpcClientService;

    private WSClientService wsClientService;

    private RSocketClientService rSocketClientService;

    private CloseableHttpAsyncClient apacheClient;

    public TestController(
            WebClient.Builder webClientBuilder,
            GRPCClientService grpcClientService,
            WSClientService wsClientService,
            RSocketClientService rSocketClientService,
            CloseableHttpAsyncClient apacheClient
    ) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8081").build();
        this.grpcClientService = grpcClientService;
        this.wsClientService = wsClientService;
        this.rSocketClientService = rSocketClientService;
        this.apacheClient = apacheClient;
    }

    @GetMapping("/test1")
    public Mono<String> test1() {
        return webClient
                .get().uri("/test/HTTP")
                .retrieve().bodyToMono(String.class);
    }

    @GetMapping("/test2")
    public Mono<String> test2() {
        return grpcClientService.echo("GRPC");
    }

    @GetMapping("/test3")
    public Mono<String> test3() {
        return wsClientService.send(Flux.just("WSocket"));
    }

    @GetMapping("/test4")
    public Mono<String> test4() {
        return rSocketClientService.request("test", "RSocket", String.class);
    }

    @GetMapping("/test5")
    public Mono<String> test1x() {
        return Mono.fromCompletionStage(sendRequestWithApacheHttpClient("Apache_HTTP"));
    }

    @GetMapping("/ptest1")
    public Mono<Long> ptest1(@RequestParam(defaultValue = "100") int count) {
        long start = System.currentTimeMillis();
        return Flux.range(0, count)
                .flatMap(counter -> {
                    return webClient
                            .get().uri("http://localhost:8081/test/" + counter)
                            .retrieve().bodyToMono(String.class);
                }, 1000).then(Mono.fromCallable(() -> System.currentTimeMillis() - start)).log();
    }

    @GetMapping("/ptest2")
    public Mono<Long> ptest2(@RequestParam(defaultValue = "100") int count) {
        long start = System.currentTimeMillis();
        return Flux.range(0, count)
                .flatMap(counter -> {
                    return grpcClientService.echo(counter + "");
                }).then(Mono.fromCallable(() -> System.currentTimeMillis() - start)).log();
    }

    @GetMapping("/ptest3")
    public Mono<Long> ptest3(@RequestParam(defaultValue = "100") int count) {
        long start = System.currentTimeMillis();
        return Flux.range(0, count)
                .flatMap(counter -> {
                    return wsClientService.send(Flux.just(counter + ""));
                }).then(Mono.fromCallable(() -> System.currentTimeMillis() - start)).log();
    }

    @GetMapping("/ptest4")
    public Mono<Long> ptest4(@RequestParam(defaultValue = "100") int count) {
        long start = System.currentTimeMillis();
        return Flux.range(0, count)
                .flatMap(counter -> {
                    return rSocketClientService.request("test", counter + "", String.class);
                }).then(Mono.fromCallable(() -> System.currentTimeMillis() - start)).log();
    }

    @GetMapping("/ptest5")
    public Mono<Long> ptest5(@RequestParam(defaultValue = "100") int count) {
        long start = System.currentTimeMillis();
        return Flux.range(0, count)
                .flatMap(counter -> {
                    return Mono.fromCompletionStage(sendRequestWithApacheHttpClient(counter + ""));
                }).then(Mono.fromCallable(() -> System.currentTimeMillis() - start)).log();
    }

    private CompletableFuture<String> sendRequestWithApacheHttpClient(String value) {
        CompletableFuture<HttpResponse> cf = new CompletableFuture<>();
        HttpUriRequest request = new HttpGet("http://localhost:8081/test/" + value);
        apacheClient.execute(request, new HttpResponseCallback(cf));
        return cf.thenApply(response -> {
            try {
                return EntityUtils.toString(response.getEntity());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).exceptionally(Throwable::toString);
    }

    static class HttpResponseCallback implements FutureCallback<HttpResponse> {

        private CompletableFuture<HttpResponse> cf;

        HttpResponseCallback(CompletableFuture<HttpResponse> cf) {
            this.cf = cf;
        }

        @Override
        public void failed(Exception ex) {
            cf.completeExceptionally(ex);
        }

        @Override
        public void completed(HttpResponse result) {
            cf.complete(result);
        }

        @Override
        public void cancelled() {
            cf.completeExceptionally(new Exception("Cancelled by http async client"));
        }
    }
}
