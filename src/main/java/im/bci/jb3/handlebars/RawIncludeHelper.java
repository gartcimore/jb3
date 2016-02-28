package im.bci.jb3.handlebars;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import java.io.IOException;

/**
 *
 * @author devnewton
 */
public class RawIncludeHelper implements Helper<String> {

    public static final RawIncludeHelper INSTANCE = new RawIncludeHelper();

    @Override
    public CharSequence apply(String file, Options options) throws IOException {
        return new Handlebars.SafeString(options.handlebars.getLoader().sourceAt(file).content());
    }

}
