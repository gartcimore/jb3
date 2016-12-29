package im.bci.jb3.bouchot.rtc;

import org.nextrtc.signalingserver.NextRTCConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 *
 * @author devnewton
 */
@Configuration
@Import(NextRTCConfig.class)
public class RtcConfig {

    @Bean
    public RtcEndpoint rtcEndpoint() {
        return new RtcEndpoint();
    }

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
