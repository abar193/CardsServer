package lobbies;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import src.Game;
import src.GameStatReceiver;
import lobbies.GameFactory.ClientConfiguration;

@Startup
@Singleton
public class GamesHolder implements HolderInterface {
    
    public GamesHolder() {
        
    }
    
    private Logger logger = Logger.getLogger(this.getClass().getName());
    private Vector<Game>games = new Vector<Game>(100);
    private GameStat stat = new GameStat();
    
    public GameStatReceiver provideStatRecorder() {
        return stat;
    }
    
    public void launchGame(ClientConfiguration p1, ClientConfiguration p2) {
        //if(games == null) games = new Vector<Game>(100);
        final Game g = new Game();
        g.configure(p1.client, p2.client, p1.deck, p2.deck, 15, 15);
        games.add(g);
        g.statReceiver = stat;
        p1.client.approveGame(g);
        p2.client.approveGame(g);
        new Thread(new Runnable() {
            @Override
            public void run() {
                laterLaunch(g);
            }
        }).start();
    }
    
    // I'm really considering moving from TomEE to GlassFish.
    private void laterLaunch(Game g) {
        try {
            Thread.sleep(1000);
            g.play();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void gameEnd(Game g, int winner) {
        int i = games.indexOf(g);
        if(i >= 0) {
            logger.log(Level.INFO, 
                    String.format("Game %d ended with victory of %d player.", i, winner));
            games.remove(i);
        } else {
            logger.log(Level.INFO, String.format
                ("Game ended with victory of %d player.", winner));
        }
            
    }
    
    private class GameStat implements GameStatReceiver {

        @Override
        public void gameEnded(Game g, int winner) {
            gameEnd(g, winner);
        }
        
    }

}
