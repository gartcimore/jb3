package im.bci.jb3.frontend;

import org.fluttercode.datafactory.impl.DataFactory;
import org.joda.time.DateTime;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * @author devnewton
 */
@RestController
@RequestMapping("/api")
public class FrontendApiController {

    private final DataFactory dataFactory = new DataFactory();

    public FrontendApiController() {
        dataFactory.randomize(DateTime.now().getMillisOfDay());
    }

    @RequestMapping(path="/random-nickname", method = RequestMethod.POST)
    public RandomNicknameMV randomNickname() {
        RandomNicknameMV mv = new RandomNicknameMV();
        mv.setNickname(dataFactory.getName());
        return mv;
    }
}
