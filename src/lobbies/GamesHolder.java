package lobbies;

import javax.annotation.Resource;
import javax.ejb.Singleton;

import src.Game;
import lobbies.GameFactory.ClientConfiguration;

@Singleton
public class GamesHolder {
    
    public GamesHolder() {
        
    }
    
    public void launchGame(ClientConfiguration p1, ClientConfiguration p2) {
        final Game g = new Game();
        g.configure(p1.client, p2.client, p1.deck, p2.deck, 15, 15);
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

}
