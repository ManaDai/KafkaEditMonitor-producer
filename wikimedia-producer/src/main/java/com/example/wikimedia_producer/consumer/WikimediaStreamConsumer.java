package com.example.wikimedia_producer.consumer;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.wikimedia_producer.producer.WikimediaProducer;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WikimediaStreamConsumer {

    private final WebClient webClient;
    private final WikimediaProducer producer;

    public WikimediaStreamConsumer(WebClient.Builder webClientBuilder, WikimediaProducer producer) {
        this.webClient = webClientBuilder
                .baseUrl("https://stream.wikimedia.org/v2")
                .build();
        this.producer = producer;
    }


    public void consumeStreamAndPublish() {
        webClient.get()
                .uri("/stream/recentchange")
                .retrieve()
                .bodyToFlux(String.class)
                .filter(this::isWikipediaEvent) // フィルタリングを追加
                .subscribe(producer::sendMessage);
    }

    private boolean isWikipediaEvent(String message) {
        //日本語だと少なすぎるかも...wikipediaの編集に絞っている
        // return message.contains("\"domain\":\"ja.wikipedia.org\"");
        return message.contains("\"domain\":\"en.wikipedia.org\"");
        //         message.contains("\"domain\":\"de.wikipedia.org\"") ||
                // 他のWikipediaのドメインも必要に応じて追加

                //  message.contains("\"domain\":\"wikipedia.org\""); wikipedia全般のときの設定
    }
}