package com.microservices.demo.twitter.to.kafka.service.runner.impl;

import com.microservices.demo.twitter.to.kafka.service.config.TwitterToKafkaServiceConfigData;
import com.microservices.demo.twitter.to.kafka.service.exception.TwitterToKafkaServiceException;
import com.microservices.demo.twitter.to.kafka.service.runner.StreamRunner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import twitter4j.TwitterException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Primary
@Component
@Slf4j
@ConditionalOnExpression("${twitter-to-kafka-service.enable-v2-tweets} && not ${twitter-to-kafka-service.enable-mock-tweets}")
@RequiredArgsConstructor
public class TwitterV2KafkaStreamRunner implements StreamRunner {

    private final TwitterToKafkaServiceConfigData configData;
    private final TwitterV2StreamHelper helper;

    @Override
    public void start() throws TwitterException {
        String token = configData.getTwitterV2BearerToken();

        if (null == token) {
            throw new TwitterToKafkaServiceException("There was a problem getting your bearer token. " +
                    "Please make sure you set TWITTER_BEARER_TOKEN environment variable");
        }

        try {
            helper.setupRules(token, getRules());
            helper.connectStream(token);
        } catch (IOException | URISyntaxException e) {
            log.error("Error streaming tweets!", e);
            throw new TwitterToKafkaServiceException("Error streaming tweets!", e);
        }

    }

    private Map<String, String> getRules() {
        List<String> keywords = configData.getTwitterKeywords();
        Map<String, String> rules = new HashMap<>();
        for (String keyword : keywords) {
            rules.put(keyword, "Keyword: " + keyword);
        }
        log.info("Created filter for twitter stream for keywords: {}", keywords);
        return rules;
    }
}
