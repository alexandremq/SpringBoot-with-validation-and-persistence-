package com.scmspain.persistence;

import com.scmspain.entities.Tweet;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class TweetPersistenceTest {
    private EntityManager entityManager;
    private TweetPersistence tweetPersistence;

        @Before
        public void setUp() throws Exception {
            this.entityManager = mock(EntityManager.class);
            this.tweetPersistence = new TweetPersistence(entityManager);
    }

    @Test
    public void shouldInsertANewTweet() throws Exception {
        Tweet tweet = new Tweet("Guybrush Threepwood", "I am Guybrush Threepwood, mighty pirate.");
        tweetPersistence.saveTweet(tweet);

        verify(entityManager).persist(any(Tweet.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowAnExceptionWhenTweetLengthIsInvalid() throws Exception {
        Tweet tweet = new Tweet("Pirate", "LeChuck? He's the guy that went to the Governor's for dinner and never wanted to leave. He fell for her in a big way, but she told him to drop dead. So he did. Then things really got ugly.");
        tweetPersistence.saveTweet(tweet);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenSavingSameTweet() {
        Tweet tweet = new Tweet("Pirate", "LeChuck? He's the guy that went to the Governor's for dinner and never wanted to leave. He fell for her in a big way, but she told him to drop dead. So he did. Then things really got ugly.");

        doThrow(EntityExistsException.class).when(entityManager).persist(tweet);

        tweetPersistence.saveTweet(tweet);
    }

    @Test
    public void shouldInsertNewTweetWithLinks() throws Exception {
        Tweet tweet = new Tweet("Guybrush Threepwood", "I am Guybrush Threepwood ( https://en.wikipedia.org/wiki/Guybrush_Threepwood ), mighty pirate.");
        tweetPersistence.saveTweet(tweet);

        assertFalse(tweet.getLinks().isEmpty());
        assertEquals(1, tweet.getLinks().size());
    }

    @Test
    public void shouldFindById(){
        Tweet tweet = new Tweet("Guybrush Threepwood", "I am Guybrush Threepwood, mighty pirate.");
        tweet.setId(1L);

        when(entityManager.find(Tweet.class, 1L)).thenReturn(tweet);

        Tweet actual = tweetPersistence.findTweetById(1L);

        assertNotNull(actual);
        assertNotNull(tweet.getId());
    }

    @Test
    public void shouldFindNonDiscardedTweets(){
        Tweet tweet1 = new Tweet("Guybrush Threepwood", "I am Guybrush Threepwood, mighty pirate.");
        tweet1.setId(1L);
        Tweet tweet2 = new Tweet("Pirate", "LeChuck? He's the guy that went to the Governor's for dinner and never wanted to leave. He fell for her in a big way, but she told him to drop dead. So he did. Then things really got ugly.");
        tweet2.setId(2L);
        Tweet tweet3 = new Tweet("Guybrush Threepwood", "I am Guybrush Threepwood ( https://en.wikipedia.org/wiki/Guybrush_Threepwood ), mighty pirate.");
        tweet3.setId(3L);
        List<Tweet> tweets = new ArrayList<>();
        tweets.add(tweet1);
        tweets.add(tweet2);
        tweets.add(tweet3);

        Query query = mock(Query.class);
        when(entityManager.createQuery(anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(tweets);
        List<Tweet> result = tweetPersistence.findNonDiscardedTweets();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(tweets.size(), result.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionFindNonDiscardedTweets(){
        Query query = mock(Query.class);
        when(entityManager.createQuery(anyString())).thenReturn(query);
        when(query.getResultList()).thenThrow(PersistenceException.class);

        tweetPersistence.findNonDiscardedTweets();
    }

    @Test
    public void shouldFindDiscardedTweets(){
        Tweet tweet1 = new Tweet("Guybrush Threepwood", "I am Guybrush Threepwood, mighty pirate.");
        tweet1.setId(1L);
        tweet1.setDiscarded(true);
        Tweet tweet2 = new Tweet("Pirate", "LeChuck? He's the guy that went to the Governor's for dinner and never wanted to leave. He fell for her in a big way, but she told him to drop dead. So he did. Then things really got ugly.");
        tweet2.setId(2L);
        tweet2.setDiscarded(true);
        Tweet tweet3 = new Tweet("Guybrush Threepwood", "I am Guybrush Threepwood ( https://en.wikipedia.org/wiki/Guybrush_Threepwood ), mighty pirate.");
        tweet3.setId(3L);
        tweet3.setDiscarded(true);
        List<Tweet> tweets = new ArrayList<>();
        tweets.add(tweet1);
        tweets.add(tweet2);
        tweets.add(tweet3);

        Query query = mock(Query.class);
        when(entityManager.createQuery(anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(tweets);
        List<Tweet> result = tweetPersistence.findDiscardedTweets(anyString());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(tweets.size(), result.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionFindDiscardedTweets(){
        Query query = mock(Query.class);
        when(entityManager.createQuery(anyString())).thenReturn(query);
        when(query.getResultList()).thenThrow(PersistenceException.class);

        tweetPersistence.findDiscardedTweets(anyString());
    }

    @Test
    public void shouldDiscardTweet(){
        Tweet tweet = new Tweet("Guybrush Threepwood", "I am Guybrush Threepwood, mighty pirate.");
        tweet.setId(1L);

        when(entityManager.find(Tweet.class, 1L)).thenReturn(tweet);

        tweetPersistence.discardTweet(tweet);

        verify(entityManager).merge(any(Tweet.class));
        assertEquals(true, tweet.isDiscarded());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenDiscardTweet(){
        Tweet tweet = new Tweet("Guybrush Threepwood", "I am Guybrush Threepwood, mighty pirate.");
        tweet.setId(1L);

        when(entityManager.find(Tweet.class, 1L)).thenReturn(null);

        tweetPersistence.discardTweet(tweet);
    }
}
