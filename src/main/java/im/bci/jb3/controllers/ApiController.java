package im.bci.jb3.controllers;

import im.bci.jb3.frontend.RandomNicknameMV;

import org.fluttercode.datafactory.impl.DataFactory;
import org.joda.time.DateTime;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * @author devnewton
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    private final DataFactory dataFactory = new DataFactory();

    public ApiController() {
        dataFactory.randomize(DateTime.now().getMillisOfDay());
    }

    @RequestMapping("/random-nickname")
    public RandomNicknameMV randomNickname() {
        RandomNicknameMV mv = new RandomNicknameMV();
        mv.setNickname(dataFactory.getName());
        return mv;
    }
}
