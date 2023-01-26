package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

import static java.util.concurrent.CompletableFuture.runAsync;

@Component
public class DemoRunner implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(
            DemoRunner.class
    );

    @Autowired
    WebClient.Builder webClientBuilder;

    @Override
    public void run(String... args) throws Exception {
        //ExecutorService exe = Executors.newFixedThreadPool(5);
        ExecutorService exe = new ForkJoinPool(7);
        runAsync(() -> {
            try {
                final var webClient = webClientBuilder
                        .baseUrl("http://doesntmatter/")
                        .build();
                HttpServiceProxyFactory
                        .builder(WebClientAdapter.forClient(webClient))
                        .build()
                        .createClient(ExampleApi.class);
                LOGGER.info("Client created");
            } catch (Exception e) {
                LOGGER.error("Failed to create client", e);
            }
        }, exe).get();
    }
}
