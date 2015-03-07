package im.bci.jb3.gateway;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
public interface Gateway {
    String getRoom();
    void post(String nickname, String message);
}
