package im.bci.jb3.bot;

import org.springframework.web.util.UriComponentsBuilder;

import im.bci.jb3.bouchot.data.Post;

public interface Bot {
    
    void handle(Post post, UriComponentsBuilder uriBuilder);
    
}
