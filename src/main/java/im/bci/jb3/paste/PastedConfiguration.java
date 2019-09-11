package im.bci.jb3.paste;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import im.bci.jb3.bouchot.data.PasteRepository;

@Configuration
public class PastedConfiguration implements WebMvcConfigurer {

	@Autowired
	private PasteRepository pasteRepository;
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/pasted/*").addResourceLocations(pasteRepository.getPasteDir().toURI().toASCIIString());
	}
}
