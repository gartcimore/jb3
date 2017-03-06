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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

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
        attributes.addAttribute("scope", "account board");
        return new RedirectView("https://linuxfr.org/api/oauth/authorize");
    }

    @RequestMapping(path = "/connected", method = RequestMethod.GET)
    public String connected(Model model, @RequestParam String code) {
        try {
            DlfpOauthToken token = retrieveToken(code);
            model.addAttribute("token", token);
            model.addAttribute("login", retrieveLogin(token));
        } catch (Exception ex) {
            Logger.getLogger(DlfpController.class.getName()).log(Level.WARNING, null, ex);
        }
        model.addAttribute("wro-group", "dlfp");
        return "dlfp/connected";
    }

    private DlfpOauthToken retrieveToken(String code) throws RestClientException {
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
        HttpEntity<MultiValueMap<String, String>> formEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<DlfpOauthToken> response
                = restTemplate.exchange("https://linuxfr.org/api/oauth/token", HttpMethod.POST,
                        formEntity, DlfpOauthToken.class);
        return response.getBody();
    }

    private String retrieveLogin(DlfpOauthToken token) {
        RestTemplate restTemplate = new RestTemplate();
        String uri = UriComponentsBuilder.fromHttpUrl("https://linuxfr.org/api/v1/me").queryParam("bearer_token", token.getAccess_token()).build().toString();
        DlfpMe me = restTemplate.getForObject(uri, DlfpMe.class);
        return me.getLogin();
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

            HttpEntity<MultiValueMap<String, String>> formEntity = new HttpEntity<>(requestBody, headers);

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
