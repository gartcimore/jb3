package im.bci.jb3.coincoin;

import org.fluttercode.datafactory.impl.DataFactory;
import org.joda.time.DateTime;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import im.bci.jb3.bouchot.logic.CleanUtils;

/**
 * 
 * @author devnewton
 */
@RestController
@RequestMapping("/api/coincoin")
public class CoincoinApiController {

    private final DataFactory dataFactory = new DataFactory();

    public CoincoinApiController() {
        dataFactory.randomize(DateTime.now().getMillisOfDay());
    }

    @RequestMapping(path="/random-nickname", method = RequestMethod.POST)
    public RandomNicknameMV randomNickname() {
        RandomNicknameMV mv = new RandomNicknameMV();
        mv.setNickname(CleanUtils.cleanNickname(dataFactory.getName()));
        return mv;
    }
}
