package im.bci.jb3.data;

import im.bci.jb3.frontend.PostSearchRQ;
import java.util.List;

public interface FortuneRepository {

    Fortune save(Fortune f);

    Fortune findOne(String fortuneId);
    
    List<Fortune> search(PostSearchRQ fo);
    
}
