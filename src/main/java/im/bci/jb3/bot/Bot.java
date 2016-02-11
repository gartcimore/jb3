package im.bci.jb3.bot;

import org.springframework.web.util.UriComponentsBuilder;

import im.bci.jb3.data.Post;

public interface Bot {
    
    void handle(Post post, UriComponentsBuilder uriBuilder);
    
}
