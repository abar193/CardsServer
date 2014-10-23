package lobbies;

import lobbies.GameFactory.ClientConfiguration;
import src.GameStatReceiver;

public interface HolderInterface {//extends GameStatReceiver {
    public void launchGame(ClientConfiguration p1, ClientConfiguration p2);
    public GameStatReceiver provideStatRecorder();
}
