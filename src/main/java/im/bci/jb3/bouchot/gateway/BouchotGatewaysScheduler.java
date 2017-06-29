package im.bci.jb3.bouchot.gateway;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
@Component
public class BouchotGatewaysScheduler {

    @Autowired
    private SchedulableGateway[] gateways;

    @Resource(name = "mouleScheduler")
    private TaskScheduler scheduler;

    @PostConstruct
    public void schedule() {
        for (SchedulableGateway g : gateways) {
            scheduler.schedule(g, g);
        }

    }

}
