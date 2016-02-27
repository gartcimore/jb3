package im.bci.jb3.handlebars;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import java.io.IOException;

/**
 *
 * @author devnewton
 */
public class RawHelper implements Helper<Object> {
    
    public static final RawHelper INSTANCE = new RawHelper();

    @Override
    public CharSequence apply(Object t, Options options) throws IOException {
        return options.fn.text();
    }

}
