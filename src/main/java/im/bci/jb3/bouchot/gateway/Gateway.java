package im.bci.jb3.bouchot.gateway;

/**
 *
 * @author devnewton <devnewton@bci.im>
 */
public interface Gateway {
    boolean handlePost(String nickname, String message, String room, String auth);
}
