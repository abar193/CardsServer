package lobbies;

import players.PlayerInterface;
import src.Game;

/**
 *
 * @author Abar
 */
public interface SocketClientInterface extends PlayerInterface {
    public void approveGame(Game g);
}
