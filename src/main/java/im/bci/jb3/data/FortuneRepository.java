package im.bci.jb3.data;

import im.bci.jb3.frontend.FortuneSearchFO;
import java.util.List;

public interface FortuneRepository {

    Fortune save(Fortune f);

    Fortune findOne(String fortuneId);
    
    List<Fortune> search(FortuneSearchFO fo);
    
}
