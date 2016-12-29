package im.bci.jb3.bouchot.rtc;

import javax.websocket.server.ServerEndpoint;
import org.nextrtc.signalingserver.api.NextRTCEndpoint;
import org.nextrtc.signalingserver.codec.MessageDecoder;
import org.nextrtc.signalingserver.codec.MessageEncoder;

/**
 *
 * @author devnewton
 */
@ServerEndpoint(value = "/rtcoin",
        decoders = MessageDecoder.class,
        encoders = MessageEncoder.class)
public class RtcEndpoint extends NextRTCEndpoint {

}
