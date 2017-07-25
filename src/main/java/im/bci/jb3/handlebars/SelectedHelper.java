package im.bci.jb3.handlebars;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

import java.io.IOException;
import java.util.Objects;

import org.springframework.stereotype.Component;

/**
 *
 * @author devnewton
 */
@Component
public class SelectedHelper implements Helper<Object> {

    @Override
    public CharSequence apply(Object value, Options options) throws IOException {
        return new Handlebars.SafeString(Objects.equals(Objects.toString(value), Objects.toString(options.param(0))) ? "selected": "");
    }

}
