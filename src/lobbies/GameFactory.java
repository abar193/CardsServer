package lobbies;

import java.util.Vector;

import java.util.Random;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
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
    private class ClientConfiguration {
        /** User's deck. */
        public Deck deck;
        /** User's client. */
        public SocketClient client;
    }
    
    @Resource TimerService tservice;
    private Random random;
    private Logger logger = Logger.getLogger("Factory");
    private Vector<ClientConfiguration> lobbies;
    
    public GameFactory() {    
    }
    
    /** Single instance of GameFactory. */
    private static GameFactory instance = new GameFactory();
    
    /** Represents that one client, that hadn't found his pair yet. */
    private ClientConfiguration seeker;
    
    @PostConstruct
    public void init() {
        random = new Random();
        tservice.createIntervalTimer(1000, 1000, new TimerConfig());
    }
    
    @Timeout
    public void timeout() {
    }
    
    /**
     * Provides game for player, or let's him wait.
     * @param d deck of player
     * @param sc 
     * @param opponent 
     * @return Game instance
     */
    public synchronized final Game provideGame(final Deck d, final SocketClient sc,
            final String opponent) {
        DeckPackReader dpr = new DeckPackReader();
        d.shuffleCards();
        Game g = new Game();

        switch (opponent) {
            case "Terran": {
                Deck d2 = new Deck(dpr.parseFile("BotImbaDeck.xml"));
                d2.shuffleCards();
                g.configure(sc, new players.SimpleBot(), d, d2, 15, 15);
                break;
            }
            case "Player": {
                if (seeker == null) {
                    seeker = new ClientConfiguration();
                    seeker.deck = d;
                    seeker.client = sc;
                    return null;
                } else {
                    
                }
                break;
            }
            default: {
                Deck d2 = new Deck(dpr.parseFile("BotImbaDeck.xml"));
                d2.shuffleCards();
                g.configure(sc, new players.PassiveBot(), d, d2, 15, 15);
                break;
            }
                
        }
        
        return g;
    }

}



