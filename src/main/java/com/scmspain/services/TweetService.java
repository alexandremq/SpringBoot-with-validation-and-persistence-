package com.scmspain.services;

import com.scmspain.entities.Tweet;
import com.scmspain.persistence.TweetPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.metrics.writer.Delta;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class TweetService {
    private static final Logger LOG = LoggerFactory.getLogger(TweetService.class);
    private MetricWriter metricWriter;
    private TweetPersistence tweetPersistence;

    public TweetService(TweetPersistence tweetPersistence, MetricWriter metricWriter) {
        this.tweetPersistence = tweetPersistence;
        this.metricWriter = metricWriter;
    }

    /**
     * Push tweet to repository
     * @param tweet The tweet to be published and stored.
    */
    @Transactional
    public void publishTweet(Tweet tweet) {
        this.metricWriter.increment(new Delta<Number>("published-tweets", 1));
        this.tweetPersistence.saveTweet(tweet);
    }

    /**
     * Recover all tweets from repository descended by publicationDate.
     * @return All available Tweets descended by publicationDate.
     */
    public List<Tweet> listAllTweets() {
        this.metricWriter.increment(new Delta<Number>("times-queried-tweets", 1));
        return this.tweetPersistence.findNonDiscardedTweets();
    }

    /**
     * Recover all discarded tweets for the given publisher.
     * @param publisher A publisher of discarded tweets
     * @return A List of all discarded tweets of the given publisher.
     */
    public List<Tweet> listDiscardedTweets(String publisher){
        this.metricWriter.increment(new Delta<Number>("times-queried-discarded-tweets", 1));
        return this.tweetPersistence.findDiscardedTweets(publisher);
    }

    /**
     * Marks a tweet, based on the given id, as discarded.
     * @param tweet The tweet instance containing the id of tweet to be marked as discarded.
     */
    @Transactional
    public void discardTweet(Tweet tweet){
        this.metricWriter.increment(new Delta<Number>("discarded-tweets", 1));
        this.tweetPersistence.discardTweet(tweet);
    }

}
