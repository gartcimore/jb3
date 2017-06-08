package im.bci.jb3.bouchot.data;

import java.util.List;

/**
 *
 * @author devnewton
 */
public interface SubscriptionRepository {
    void save(Subscription subscription);
    List<Subscription> findAll();
}
