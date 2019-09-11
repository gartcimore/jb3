package im.bci.jb3.handlebars;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import java.io.IOException;
import java.nio.charset.Charset;

import org.springframework.stereotype.Component;

/**
 *
 * @author devnewton
 */
@Component
public class RawIncludeHelper implements Helper<String> {

    private static final Charset CHARSET = Charset.forName("UTF-8");

	@Override
    public CharSequence apply(String file, Options options) throws IOException {
        return new Handlebars.SafeString(options.handlebars.getLoader().sourceAt(file).content(CHARSET));
    }

}
