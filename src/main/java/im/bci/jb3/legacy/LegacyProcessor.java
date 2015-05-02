package im.bci.jb3.legacy;

import im.bci.jb3.logic.Norloge;
import java.util.regex.Matcher;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
public interface LegacyProcessor {

    void processNorloge(Norloge norloge, Matcher matcher);

    void processTotoz(String totoz, Matcher matcher);

    void processBigorno(String bigorno, Matcher matcher);

    void end(Matcher matcher);

}
