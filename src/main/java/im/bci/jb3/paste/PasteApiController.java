package im.bci.jb3.paste;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import im.bci.jb3.bouchot.data.PasteRepository;

/**
 * 
 * @author devnewton
 */
@RestController
@RequestMapping("/api/paste")
public class PasteApiController {
	
	@Autowired
	private PasteRepository pasteRepository;

	@RequestMapping(path = "/text", method = RequestMethod.POST)
	public PastedMV paste(String ptext) throws FileNotFoundException, IOException {
		PastedMV mv = new PastedMV();
		if(StringUtils.isNotBlank(ptext)) {
			mv.setUrl(pasteRepository.saveTextPaste(ptext));
		}
		return mv;
	}
	
	@RequestMapping(path = "/image", method = RequestMethod.POST)
	public PastedMV pasteImage(MultipartRequest request) throws FileNotFoundException, IOException {
		PastedMV mv = new PastedMV();
		List<MultipartFile> pimage = request.getFiles("pimage");
		if(null != pimage && pimage.size()>0) {
			for(int i = pimage.size()-1; i>=0; --i) {
				if(!pimage.get(i).isEmpty()) {
				mv.setUrl(pasteRepository.saveFilePaste(pimage.get(i)));
				break;
				}
			}
		}
		return mv;
	}
	
	@RequestMapping(path = "/file", method = RequestMethod.POST)
	public PastedMV pasteFile(MultipartFile pfile) throws FileNotFoundException, IOException {
		PastedMV mv = new PastedMV();
		if(null != pfile && !pfile.isEmpty()) {
			mv.setUrl(pasteRepository.saveFilePaste(pfile));
		}
		return mv;
	}
}