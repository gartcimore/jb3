package im.bci.jb3.handlebars;

import com.github.jknack.handlebars.springmvc.HandlebarsViewResolver;

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

	@Autowired
	private FormatMessageHelper formatMessageHelper;

	@PostConstruct
	public void configure() throws Exception {
		handlebarsViewResolver.registerHelper("raw-include", rawIncludeHelper);
		handlebarsViewResolver.registerHelper("format-datetime", formatDateTimeHelper);
		handlebarsViewResolver.registerHelper("format-post-message", formatMessageHelper);
	}
}
