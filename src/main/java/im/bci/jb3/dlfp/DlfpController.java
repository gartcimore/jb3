package im.bci.jb3.dlfp;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/dlfp")
public class DlfpController {

    @Value("${jb3.dlfp.oauth.client_id:}")
    private String clientId;

    @Value("${jb3.dlfp.oauth.client_secret:}")
    private String clientSecret;

    @RequestMapping(path = "/connect", method = RequestMethod.GET)
    public RedirectView connect(RedirectAttributes attributes) {
        attributes.addAttribute("client_id", clientId);
        attributes.addAttribute("redirect_uri", buildRedirectURI());
        attributes.addAttribute("response_type", "code");
        attributes.addAttribute("scope", "board");
        return new RedirectView("https://linuxfr.org/api/oauth/authorize");
    }

    @RequestMapping(path = "/connected", method = RequestMethod.GET)
    public String connect(Model model, @RequestParam String code) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
            headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);

            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("client_id", clientId);
            requestBody.add("client_secret", clientSecret);
            requestBody.add("code", code);
            requestBody.add("grant_type", "authorization_code");
            requestBody.add("redirect_uri", buildRedirectURI());

            HttpEntity formEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<DlfpOauthToken> response
                    = restTemplate.exchange("https://linuxfr.org/api/oauth/token", HttpMethod.POST,
                            formEntity, DlfpOauthToken.class);
            model.addAttribute("token", response.getBody());
        } catch (Exception ex) {
            Logger.getLogger(DlfpController.class.getName()).log(Level.WARNING, null, ex);
        }
        model.addAttribute("wro-group", "dlfp");
        return "dlfp/connected";
    }

    @ResponseBody
    @RequestMapping(path = "/refresh-token", method = RequestMethod.POST)
    public DlfpOauthToken refreshToken(@RequestParam String token) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
            headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);

            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("client_id", clientId);
            requestBody.add("client_secret", clientSecret);
            requestBody.add("refresh_token", token);
            requestBody.add("grant_type", "refresh_token");

            HttpEntity formEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<DlfpOauthToken> response
                    = restTemplate.exchange("https://linuxfr.org/api/oauth/token", HttpMethod.POST,
                            formEntity, DlfpOauthToken.class);
            return response.getBody();
        } catch (Exception ex) {
            Logger.getLogger(DlfpController.class.getName()).log(Level.WARNING, null, ex);
            throw ex;
        }

    }

    private String buildRedirectURI() {
        return ServletUriComponentsBuilder.fromCurrentRequest().replacePath("/dlfp/connected").replaceQuery("").build().toString();
    }

}
