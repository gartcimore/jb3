package im.bci.jb3.handlebars;

import com.github.jknack.handlebars.springmvc.HandlebarsViewResolver;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    
    @Value("${jb3.wro}")
    private boolean useWro;
    
    @PostConstruct
    public void configure() {
    	handlebarsViewResolver.getAttributesMap().put("jb3UseWro", useWro);
        handlebarsViewResolver.registerHelper("raw-include", rawIncludeHelper);
    }
}
