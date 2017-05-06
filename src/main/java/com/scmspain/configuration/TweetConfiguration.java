package com.scmspain.configuration;

import com.scmspain.controller.TweetController;
import com.scmspain.controller.validation.TweetValidator;
import com.scmspain.persistence.TweetPersistence;
import com.scmspain.services.TweetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

@Configuration
public class TweetConfiguration {

    @Bean
    public TweetPersistence getTweetPersistence(EntityManager entityManager){
        return new TweetPersistence(entityManager);
    }

    @Bean
    public TweetService getTweetService(TweetPersistence tweetPersistence, MetricWriter metricWriter) {
        return new TweetService(tweetPersistence, metricWriter);
    }

    @Bean
    public TweetController getTweetConfiguration(TweetService tweetService) {
        return new TweetController(tweetService);
    }
}
