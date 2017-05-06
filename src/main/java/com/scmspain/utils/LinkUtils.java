package com.scmspain.utils;

import com.scmspain.entities.Tweet;
import com.scmspain.entities.TweetLink;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class LinkUtils {

    //private static String regex = "(http|https)[-a-zA-Z0-9+&@#/%?=~_|!¡:,.;]+[-a-zA-Z0-9+&@#/%=~_|]"; //url ending with any of the latest char class
    private static String regex = "(http|https)[-a-zA-Z0-9+&@#/%?=~_|!¡:,.;]+[\\s]"; //url ending with space

    /**
     * Extract any available link from the given tweet, returning
     * the tweet text without any links.
     * @param tweet The tweet instance tweeted to have links extracted.
     * @return The same tweet without links, if any.
     */
    public static boolean extractLinks(final Tweet tweet, String text){
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(text);
        final StringBuilder sb = new StringBuilder(text);

        while(matcher.find()){
            int start = matcher.start();
            String link = matcher.group();

            final TweetLink tweetLink = new TweetLink(tweet.getId());
            tweetLink.setIndex(start);
            tweetLink.setLink(link);

            tweet.addLink(tweetLink);
            sb.delete(sb.indexOf(link), (sb.indexOf(link) + link.length()));
        }

        tweet.setTweet(sb.toString());

        return tweet.getLinks() != null && !tweet.getLinks().isEmpty();
    }

    /** Add all available links related to the given tweet
     * @param tweet The tweet that will receive back all links previously associated to it.
     * @return The same given Tweet instance with all previously associated links back.
     */
    public static Tweet includeLinks(final Tweet tweet){
        if(Objects.isNull(tweet) || Objects.isNull(tweet.getLinks()) || tweet.getLinks().isEmpty()){
            return tweet;
        }

        final StringBuilder text = new StringBuilder(tweet.getTweet());

        for(TweetLink link : tweet.getLinks()){
            text.insert(link.getIndex(), link.getLink());
        }

        tweet.setTweet(text.toString());

        return tweet;
    }

}
