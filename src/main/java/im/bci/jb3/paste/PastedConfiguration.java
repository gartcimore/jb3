package im.bci.jb3.paste;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import im.bci.jb3.bouchot.data.PasteRepository;

@Configuration
public class PastedConfiguration extends WebMvcConfigurerAdapter {

	@Autowired
	private PasteRepository pasteRepository;
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/pasted/*").addResourceLocations(pasteRepository.getPasteDir().toURI().toASCIIString());
	}
}
