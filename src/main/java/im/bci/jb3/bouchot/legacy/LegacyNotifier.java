package im.bci.jb3.bouchot.legacy;

import im.bci.jb3.bouchot.data.Post;
import im.bci.jb3.bouchot.data.Subscription;
import im.bci.jb3.bouchot.data.SubscriptionRepository;
import im.bci.jb3.bouchot.logic.UserPostHandler;
import im.bci.jb3.event.NewPostsEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 *
 * @author devnewton
 */
@Component
public class LegacyNotifier {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private LegacyUtils legacyUtils;

    @Autowired
    private UserPostHandler tribune;

    @EventListener
    public void notify(NewPostsEvent event) {
        for (Subscription subscription : subscriptionRepository.findAll()) {
            for (Post post : event.getPosts()) {
                if (shouldNotify(subscription, post)) {
                    notify(subscription, legacyUtils.post2legacy(post));
                }
            }
        }
    }

    private static boolean shouldNotify(Subscription subscription, Post post) {
        if (!StringUtils.equals(subscription.getRoom(), post.getRoom())) {
            return false;
        }
        if (StringUtils.isNotBlank(subscription.getBot())) {
            if (StringUtils.equals(subscription.getBot(), post.getNickname())) {
                return false;
            }
        }
        return true;
    }

    private void notify(Subscription subscription, LegacyPost post) {
        try {
            String reply = Jsoup.connect(subscription.getCallback())
                    .data("id", "" + post.getId())
                    .data("time", post.getTime())
                    .data("info", post.getInfo())
                    .data("message", post.getMessage())
                    .method(Connection.Method.POST).execute().body();
            if (StringUtils.isNotBlank(reply) && StringUtils.isNotBlank(subscription.getBot())) {
                tribune.post(subscription.getBot(), reply, subscription.getRoom(), null, null);
            }
        } catch (Exception e) {
            LogFactory.getLog(this.getClass()).error("notify error", e);
        }
    }

}
