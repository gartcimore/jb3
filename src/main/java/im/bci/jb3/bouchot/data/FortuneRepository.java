package im.bci.jb3.bouchot.data;

import java.util.List;

import im.bci.jb3.coincoin.FortuneSearchRQ;

public interface FortuneRepository {

    Fortune save(Fortune f);

    Fortune findOne(String fortuneId);
    
    List<Fortune> search(FortuneSearchRQ fo);
    
}
