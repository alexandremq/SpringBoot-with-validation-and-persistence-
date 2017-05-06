package com.scmspain.entities;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Link of a tweet. A tweet might have links on it and those shouldn't affect the tweet text limit of 140 characters,
 * so whenever a link is found in a tweet it will be extracted with the position of the link and stored on an instance
 * of this class.
 */
@Entity
public class TweetLink implements Serializable{

    @Id
    @Column(name = "tweet_id")
    private Long tweetLinkId;

    @Id
    @Column(nullable = false)
    private Integer index;

    @Column(nullable = false)
    private String link;

    public TweetLink(){}

    public TweetLink(Long tweetLinkId){
        this.tweetLinkId = tweetLinkId;
    }

    public Long getTweetLinkId() {
        return tweetLinkId;
    }

    public void setTweetLinkId(Long tweetLinkId) {
        this.tweetLinkId = tweetLinkId;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
