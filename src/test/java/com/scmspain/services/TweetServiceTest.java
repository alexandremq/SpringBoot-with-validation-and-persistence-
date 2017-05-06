package com.scmspain.services;

import com.scmspain.entities.Tweet;
import com.scmspain.persistence.TweetPersistence;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TweetServiceTest {
    private MetricWriter metricWriter;
    private TweetService tweetService;
    private TweetPersistence tweetPersistence;

    @Before
    public void setUp() throws Exception {
        this.metricWriter = mock(MetricWriter.class);
        this.tweetPersistence = mock(TweetPersistence.class);
        this.tweetService = new TweetService(tweetPersistence, metricWriter);
    }

    @Test
    public void shouldPublishTweet() {
        Tweet tweet = new Tweet("Guybrush Threepwood", "I am Guybrush Threepwood, mighty pirate.");
        tweetService.publishTweet(tweet);
        verify(tweetPersistence).saveTweet(tweet);
    }

    @Test
    public void shouldListAllTweets() {
        Tweet tweet1 = new Tweet("Guybrush Threepwood", "I am Guybrush Threepwood, mighty pirate.");
        Tweet tweet2 = new Tweet("Pirate", "LeChuck? He's the guy that went to the Governor's for dinner and never wanted to leave. He fell for her in a big way, but she told him to drop dead. So he did. Then things really got ugly.");
        Tweet tweet3 = new Tweet("Guybrush Threepwood", "I am Guybrush Threepwood ( https://en.wikipedia.org/wiki/Guybrush_Threepwood ), mighty pirate.");
        List<Tweet> tweets = new ArrayList<>();
        tweets.add(tweet1);
        tweets.add(tweet2);
        tweets.add(tweet3);

        when(tweetPersistence.findNonDiscardedTweets()).thenReturn(tweets);

        List<Tweet> actual = tweetService.listAllTweets();

        assertNotNull(actual);
        assertFalse(actual.isEmpty());
        assertEquals(tweets.size(), actual.size());
    }

    @Test
    public void shouldListDiscardedTweets() {
        Tweet tweet1 = new Tweet("Guybrush Threepwood", "I am Guybrush Threepwood, mighty pirate.");
        tweet1.setDiscarded(true);
        Tweet tweet2 = new Tweet("Pirate", "LeChuck? He's the guy that went to the Governor's for dinner and never wanted to leave. He fell for her in a big way, but she told him to drop dead. So he did. Then things really got ugly.");
        tweet2.setDiscarded(true);
        Tweet tweet3 = new Tweet("Guybrush Threepwood", "I am Guybrush Threepwood ( https://en.wikipedia.org/wiki/Guybrush_Threepwood ), mighty pirate.");
        tweet3.setDiscarded(true);
        List<Tweet> tweets = new ArrayList<>();
        tweets.add(tweet1);
        tweets.add(tweet2);
        tweets.add(tweet3);

        when(tweetPersistence.findDiscardedTweets(anyString())).thenReturn(tweets);

        List<Tweet> actual = tweetService.listDiscardedTweets(anyString());

        assertNotNull(actual);
        assertFalse(actual.isEmpty());
        assertEquals(tweets.size(), actual.size());
    }

    @Test
    public void shouldDiscardTweet(){
        Tweet tweet = new Tweet();
        tweet.setId(1L);
        tweetService.discardTweet(tweet);

        verify(tweetPersistence).discardTweet(tweet);
    }
}
