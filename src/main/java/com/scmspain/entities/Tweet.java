package com.scmspain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Tweet {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String publisher;

    @Column(nullable = false, length = 140)
    private String tweet;

    @Column(nullable = false)
    @JsonIgnore
    private Instant date;

    @Column (nullable=true)
    private Long pre2015MigrationStatus = 0L;

    @Column(nullable = false)
    @JsonIgnore
    private Boolean discarded;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true) //intentionally left to use EAGER fetch (default value).
    @JoinColumn(name = "tweet_id", referencedColumnName = "id")
    @JsonIgnore
    private List<TweetLink> links;


    public Tweet() {}

    public Tweet(String publisher, String tweet){
        this.publisher = publisher;
        this.tweet = tweet;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getTweet() {
        return tweet;
    }

    public void setTweet(String tweet) {
        this.tweet = tweet;
    }

    public Long getPre2015MigrationStatus() {
        return pre2015MigrationStatus;
    }

    public void setPre2015MigrationStatus(Long pre2015MigrationStatus) {
        this.pre2015MigrationStatus = pre2015MigrationStatus;
    }

    public String getDate() {
        if(date != null){
            return date.toString();
        }
        return "";
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public Boolean isDiscarded() {
        return discarded;
    }

    public void setDiscarded(Boolean discarded) {
        this.discarded = discarded;
    }

    public List<TweetLink> getLinks() {
        return links;
    }

    public void setLinks(List<TweetLink> links) {
        this.links = links;
    }

    public void addLink(TweetLink link){
        if(this.links == null){
            this.links = new ArrayList<>();
        }
        this.links.add(link);
    }
}
