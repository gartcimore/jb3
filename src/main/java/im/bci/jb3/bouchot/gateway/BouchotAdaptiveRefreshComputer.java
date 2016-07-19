package im.bci.jb3.bouchot.gateway;

import java.util.Date;
import java.util.List;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.joda.time.DateTime;
import org.joda.time.Seconds;

import im.bci.jb3.bouchot.data.Post;

/**
 *
 * @author devnewton
 */
public class BouchotAdaptiveRefreshComputer {

    private final DescriptiveStatistics stats = new DescriptiveStatistics(10);
    private DateTime lastPostTime;
    private DateTime lastRefreshTime = DateTime.now();
    private boolean firstRefresh = true;

    public void analyseBouchotPostsResponse(List<Post> newPosts) {
        lastRefreshTime = DateTime.now();
        for (Post post : newPosts) {
            if (null != lastPostTime) {
                int secondsBetweenPosts = Math.max(0, Seconds.secondsBetween(lastPostTime, post.getTime()).getSeconds());
                stats.addValue(secondsBetweenPosts);
            }
            lastPostTime = post.getTime();
        }
    }

    public Date nextRefreshDate() {
        if (firstRefresh) {
            firstRefresh = false;
            return new Date();
        } else {
            int meanSecondsBetweenTwoPosts = (int) stats.getMean();
            int secondsWithoutPost = null != lastPostTime ? Math.max(0, Seconds.secondsBetween(lastPostTime, DateTime.now()).getSeconds()) : Integer.MAX_VALUE;
            int refreshDelay = Math.max(secondsWithoutPost, meanSecondsBetweenTwoPosts);
            int boundedRefreshDelay = Math.max(Math.min(refreshDelay, 30), 3);
            return lastRefreshTime.plusSeconds(boundedRefreshDelay).toDate();
        }
    }

}
