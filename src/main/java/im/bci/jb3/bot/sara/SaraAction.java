package im.bci.jb3.bot.sara;

import im.bci.jb3.bouchot.data.Post;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author devnewton
 */
public interface SaraAction {
    
    public enum MatchLevel {
        NO,
        CAN,
        MUST        
    }
    MatchLevel match(Post post);
    boolean act(Post post, UriComponentsBuilder uriBuilder);
}
