package com.scmspain.controller;

import com.scmspain.controller.validation.TweetValidator;
import com.scmspain.entities.Tweet;
import com.scmspain.services.TweetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.xml.ws.RequestWrapper;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
public class TweetController {
    Logger LOG = LoggerFactory.getLogger(TweetController.class);

    private TweetService tweetService;

    public TweetController(TweetService tweetService) {
        this.tweetService = tweetService;
    }

    /**
     * Binds the TweetValidator into the WebDataBinder available. This implementation allows having a validator
     * lightly coupled with this solution, provinding a mechanism for custom extension of validation or complete
     * removal without affecting the solution already created. It would be completely decoupled by using additional
     * dependencies that would allow include this validator in the configuration classes.
     * @param binder The WebDataBinder instance to be added validators.
     */
    @InitBinder("tweet")
    private void initBinder(WebDataBinder binder){
        binder.addValidators(new TweetValidator());
    }

    @GetMapping("/tweet")
    public List<Tweet> listAllTweets() {
        LOG.debug("Listing all tweets stored for the current publisher.");
        return this.tweetService.listAllTweets();
    }

    @GetMapping("/discarded")
    public List<Tweet> listDiscardedTweets(@RequestHeader String publisher) {
        LOG.debug("Listing all discarded tweets stored for the current publisher ["+ publisher +"].");
        return this.tweetService.listDiscardedTweets(publisher);
    }

    @PostMapping("/tweet")
    @ResponseStatus(CREATED)
    public void publishTweet(@Valid @RequestBody Tweet tweet) {
        LOG.debug("Publishing tweet for publisher ["+ (tweet != null ? tweet.getPublisher() : "") +"].");
        this.tweetService.publishTweet(tweet);
    }

    @PostMapping("/discarded")
    @ResponseStatus(OK)
    public void discardTweet(@RequestBody Tweet tweet){
        LOG.debug("discarding tweeter "+ tweet.getTweet() +"..."); // Respecting the API defined.
        tweet.setId(Long.valueOf(tweet.getTweet())); //It would be better receiving directly the "id" as a Long value, to avoid this conversion and to enable decoupled validation.
        tweet.setTweet(null);
        this.tweetService.discardTweet(tweet);
    }

    @ExceptionHandler({IllegalArgumentException.class, NumberFormatException.class})
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public Object invalidArgumentException(IllegalArgumentException ex) {
        LOG.warn("An exception happened. Handling exception ["+ ex +"]!");
        return new Object() {
            public String message = ex.getMessage();
            public String exceptionClass = ex.getClass().getSimpleName();
        };
    }
}
