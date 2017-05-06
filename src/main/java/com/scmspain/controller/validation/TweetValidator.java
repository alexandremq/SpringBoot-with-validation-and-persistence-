package com.scmspain.controller.validation;

import com.scmspain.entities.Tweet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Validator for the controller endpoints. This class will handle all POST endpoints available in the TweetController
 * with no coupling with the controller, allowing exchange or disabling validations in the app configuration.
 */
@Component("beforeCreateTweetValidator")
public class TweetValidator implements Validator{
    Logger LOG = LoggerFactory.getLogger(TweetValidator.class);

    /**
     * @see org.springframework.validation.Validator#supports(Class)
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return Tweet.class.equals(clazz);
    }

    /**
     * @see org.springframework.validation.Validator#validate(Object, Errors)
     */
    @Override
    public void validate(Object target, Errors errors) {
        Tweet tweet = (Tweet)target;

        if(!StringUtils.hasText(tweet.getPublisher())){
            String msg = "A Tweet's Publisher name can't be empty.";
            LOG.warn(msg);
            throw new IllegalArgumentException(msg);
        } else if(!StringUtils.hasText(tweet.getTweet())){
            String msg = "A Tweet can't be empty.";
            LOG.warn(msg);
            throw new IllegalArgumentException(msg);
        }
    }
}
