package im.bci.jb3.handlebars;

import com.github.jknack.handlebars.springmvc.HandlebarsViewResolver;

import java.io.SequenceInputStream;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author devnewton
 */
@Component
public class HandlebarsConfiguration {

    @Autowired
    private HandlebarsViewResolver handlebarsViewResolver;

    @Autowired
    private RawIncludeHelper rawIncludeHelper;

    @Autowired
    private FormatDateTimeHelper formatDateTimeHelper;

    @PostConstruct
    public void configure() throws Exception {
        handlebarsViewResolver.registerHelper("raw-include", rawIncludeHelper);
        handlebarsViewResolver.registerHelper("format-datetime", formatDateTimeHelper);
        SequenceInputStream serverHelpersStream = new SequenceInputStream(getClass().getResourceAsStream("/static/assets/common/post-to-html.js"), getClass().getResourceAsStream("/templates/helpers/server-helpers.js"));
        try {
            handlebarsViewResolver.registerHelpers("/templates/helpers/server-helpers.js", serverHelpersStream);
        } finally {
            serverHelpersStream.close();
        }
    }
}
