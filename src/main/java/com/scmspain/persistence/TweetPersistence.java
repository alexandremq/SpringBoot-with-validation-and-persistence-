package com.scmspain.persistence;

import com.scmspain.entities.Tweet;
import com.scmspain.utils.LinkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Persistence class responsible by interacting with the existing repository. Transfering the persistence
 * responsability from the Service class allow, if necessary, easier modification of the persistence mechanism
 * or consuption of services for persistence in a microservice architecture.
 */
@Repository
public class TweetPersistence {
    private static final Logger LOG = LoggerFactory.getLogger(TweetPersistence.class);

    private static final String QUERY_NON_DISCARDED_TWEETS = "SELECT t FROM Tweet t WHERE t.discarded = false ORDER BY t.date DESC";
    private static final String QUERY_DISCARDED_TWEETS = "SELECT t FROM Tweet t WHERE t.discarded = true AND t.publisher = :publisher ORDER BY t.date DESC";

    private EntityManager entityManager;

    public TweetPersistence(final EntityManager entityManager){
        this.entityManager = entityManager;
    }

    /**
     * Push tweet to repository
     * @param tweet The tweet to be published and stored.
     * @throws IllegalArgumentException explicitly included (RuntimeExceptions don't need to
     * be included) to throw when an exception happens performing an operation in the existing
     * EntityManager.
     */
    @Transactional
    public void saveTweet(Tweet tweet) throws IllegalArgumentException {
        LOG.debug("Saving tweet from ["+ tweet.getPublisher() +"] in the repository...");

        String text = tweet.getTweet();
        tweet.setTweet("");
        tweet.setDate(Instant.now());
        tweet.setDiscarded(false);

        try {
            this.entityManager.persist(tweet); //persisting tweet without "text" to create primary key to be used by possible links.

            // extracting links to include text and update tweet.
            if (LinkUtils.extractLinks(tweet, text)) {
                LOG.debug("Updating tweet from [" + tweet.getPublisher() + "] in the repository, due to found links...");
                checkTweetSize(tweet.getTweet());
                this.entityManager.persist(tweet);
            } else {
                checkTweetSize(text);
            }
        } catch(EntityExistsException ex){
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }

        LOG.debug("Tweet from ["+ tweet.getPublisher() +"] saved successfully!");
    }

    /**
     * Check tweet size against allowed limit.
     * @param tweet The tweet to check size against limit allowed.
     * @throws IllegalArgumentException in case the tweet size limit is greater than 140 characters.
     */
    private void checkTweetSize(final String tweet){
        if(tweet.length() > 140){
            final String msg = "A Tweet can't contain more than 140 characters.";
            LOG.warn(msg);
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Recover tweet from repository
     * @param id id of Tweet to be retrieved
     * @return retrieved Tweet
     * @throws IllegalArgumentException explicitly included (RuntimeExceptions don't need to
     * be included) to throw when an exception happens performing an operation in the existing
     * EntityManager.
     */
    public Tweet findTweetById(final Long id) throws IllegalArgumentException {
        Tweet tweet = this.entityManager.find(Tweet.class, id);
        tweet = LinkUtils.includeLinks(tweet);
        return tweet;
    }

    /**
     * Recover all non discarded tweets from the repository, returning the found tweets sorted
     * by publication date in descending order.
     * @return All available non discarded Tweets sorted by publication date descending order.
     * @throws IllegalArgumentException explicitly included (RuntimeExceptions don't need to
     * be included) to throw when an exception happens performing an operation in the existing
     * EntityManager.
     */
    public List<Tweet> findNonDiscardedTweets() throws IllegalArgumentException {
        LOG.debug("Retrieving all tweets...");

        List<Tweet> result = null;
        try {
            final Query query = entityManager.createQuery(QUERY_NON_DISCARDED_TWEETS);
            result = query.getResultList();
        } catch (PersistenceException ex){
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }

        includeTweetsLinks(result);

        LOG.debug(result.size() +" tweet(s) found!");

        return result;
    }

    /**
     * Include all links found that were originally in the given tweet.
     * @param tweets Tweets to found links and insert those like the original tweet published.
     */
    private void includeTweetsLinks(final List<Tweet> tweets){
        if(Objects.isNull(tweets)){ return; }
        for(Tweet tweet : tweets){
            tweet = LinkUtils.includeLinks(tweet);
        }
    }

    /**
     * Recover all discarded tweets for the given publisher.
     * @param publisher A publisher of discarded tweets
     * @return A List of all discarded tweets of the given publisher.
     * @throws IllegalArgumentException explicitly included (RuntimeExceptions don't need to
     * be included) to throw when an exception happens performing an operation in the existing
     * EntityManager.
     */
    public List<Tweet> findDiscardedTweets(final String publisher) throws IllegalArgumentException{
        LOG.debug("Retrieving all discarded tweets for the publisher ["+ publisher +"]...");

        List<Tweet> result = null;
        try {
            final Query query = entityManager.createQuery(QUERY_DISCARDED_TWEETS);
            query.setParameter("publisher", publisher);
            result = query.getResultList();
        } catch (PersistenceException ex){
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }

        includeTweetsLinks(result);

        LOG.debug(result.size() +" tweet(s) found!");

        return result;
    }

    /**
     * Marks a tweet, based on the given id, as discarded.
     * @param tweet The tweet instance containing the id of tweet to be marked as discarded.
     * @throws IllegalArgumentException explicitly included (RuntimeExceptions don't need to
     * be included) to throw when an exception happens performing an operation in the existing
     * EntityManager.
     */
    @Transactional
    public void discardTweet(Tweet tweet) throws IllegalArgumentException {
        LOG.debug("Setting tweet ["+ tweet.getId() +"] as discarded...");
        tweet = entityManager.find(Tweet.class, tweet.getId()); //fetching the whole object, without possible links, for update.

        if(Objects.isNull(tweet)){
            throw new IllegalArgumentException("Invalid tweet id was not found!");
        }

        tweet.setDiscarded(true);
        tweet.setDate(Instant.now());
        entityManager.merge(tweet);

        LOG.debug("Tweet id ["+ tweet.getId() +"] marked as discarded!");
    }

}
