package lobbies;

import java.util.ArrayList;
import java.util.Vector;
import java.util.Random;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;




import server.SocketClient;
import src.Game;
import cards.Deck;
import decks.DeckPackReader;

/**
 * Creates and provides games for connected users.
 * @author Abar
 */

@Startup
@Singleton
public class GameFactory {

    /**
     * Contains client configuration, used to store user-information for
     * matchmaking.
     * @author Abar
     */
    public class ClientConfiguration {
        /** User's deck. */
        public Deck deck;
        /** User's client. */
        public SocketClient client;
        
        public ClientConfiguration(Deck d, SocketClient sc) {
            this.deck = d;
            this.client = sc;
        }
    }
    
    @Resource TimerService tservice;
    private Random random;
    private Logger logger;
    private static ArrayList<ClientConfiguration> seekers;
    
    @EJB
    GamesHolder holder;
    
    public GameFactory() {
    }
    
    @PostConstruct
    public void init() {
        random = new Random();
        tservice.createIntervalTimer(1000, 1000, new TimerConfig());
        seekers = new ArrayList<ClientConfiguration>(10);
        logger = Logger.getLogger("Factory");
        logger.log(java.util.logging.Level.FINE, "Factry init");
    }
    
    @Timeout
    public void timeout() {
        if(seekers.size() >= 2) {
            for(int i = 0; i < seekers.size() - 1; i++) {
                int oppPos = i + 1;
                ClientConfiguration opp = seekers.remove(oppPos);
                ClientConfiguration cc = seekers.remove(i);
                holder.launchGame(opp, cc);
            }
        }
    }
    
    /**
     * Provides game for player, or let's him wait.
     * @param d deck of player
     * @param sc 
     * @param opponent 
     * @return Game instance
     */
    public final Game provideGame(final Deck d, final SocketClient sc,
            final String opponent) {
        DeckPackReader dpr = new DeckPackReader();
        d.shuffleCards();

        switch (opponent) {
            case "Terran": {
                Game g = new Game();
                Deck d2 = new Deck(dpr.parseFile("BotImbaDeck.xml"));
                d2.shuffleCards();
                g.configure(sc, new players.SimpleBot(), d, d2, 15, 15);
                return g;
            }
            case "Player": {
                if(seekers == null) {
                    System.out.println("Creating seekers!");
                    logger.log(java.util.logging.Level.INFO, "Creating seekers!");
                    seekers = new ArrayList<ClientConfiguration>(10);
                }
                seekers.add(new ClientConfiguration(d, sc));
                break;
            }
            default: {
                Game g = new Game();
                Deck d2 = new Deck(dpr.parseFile("BotImbaDeck.xml"));
                d2.shuffleCards();
                g.configure(sc, new players.PassiveBot(), d, d2, 15, 15);
                return g;
            }
                
        }
        
        return null;
    }

}
