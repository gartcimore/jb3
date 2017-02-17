package im.bci.jb3.dlfp;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/dlfp")
public class DlfpController {

	@Value("${jb3.dlfp.oauth.client_id}")
	private String clientId;

	@Value("${jb3.dlfp.oauth.client_secret}")
	private String clientSecret;

	@RequestMapping(path = "/connect", method = RequestMethod.GET)
	public RedirectView connect(RedirectAttributes attributes) {
		attributes.addAttribute("client_id", clientId);
		attributes.addAttribute("redirect_uri", buildRedirectURI());
		attributes.addAttribute("response_type", "code");
		attributes.addAttribute("scope", "board");
		return new RedirectView("https://linuxfr.org/api/oauth/authorize");
	}

	public static class OauthToken {
		private String access_token;
		private String refresh_token;
		private String expires_in;

		public String getAccess_token() {
			return access_token;
		}

		public void setAccess_token(String access_token) {
			this.access_token = access_token;
		}

		public String getRefresh_token() {
			return refresh_token;
		}

		public void setRefresh_token(String refresh_token) {
			this.refresh_token = refresh_token;
		}

		public String getExpires_in() {
			return expires_in;
		}

		public void setExpires_in(String expires_in) {
			this.expires_in = expires_in;
		}
	}

	@RequestMapping(path = "/connected", method = RequestMethod.GET)
	public String connect(Model model, @RequestParam String code) {
		RestTemplate restTemplate = new RestTemplate();
		try {
			MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
			map.add("client_id", clientId);
			map.add("client_secret", clientSecret);
			map.add("code", code);
			map.add("grant_type", "authorization_code");
			map.add("redirect_uri", buildRedirectURI());
			OauthToken token = restTemplate.postForObject("https://linuxfr.org/api/oauth/token", map, OauthToken.class);
			model.addAttribute("token", token);
		} catch (Exception ex) {
			Logger.getLogger(DlfpController.class.getName()).log(Level.WARNING, null, ex);
		}
		model.addAttribute("wro-group", "dlfp");
		return "dlfp/connected";
	}

	private String buildRedirectURI() {
		return ServletUriComponentsBuilder.fromCurrentRequest().replacePath("/dlfp/connected").build().toString();
	}

}
