package lobbies;

import java.util.Vector;

import server.SocketClient;
import src.Game;
import cards.Deck;
import decks.DeckPackReader;

/**
 * Creates and provides games for connected users.
 * @author Abar
 */
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
    
    /** Class constructor. */
    public GameFactory() {    
    }
    
    /** Single instance of GameFactory. */
    private static GameFactory instance = new GameFactory();
    
    /** Represents that one client, that hadn't found his pair yet. */
    // TODO: rework it
    private ClientConfiguration seeker;
    
    /** Naive solution for synchronizing problem. */
    private Object synchronizer = new Object();
    
    /** Singleton pattern.
     * @return GameFactory */
    public static GameFactory instance() {
        return instance;
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
        Game g = new Game();

        switch (opponent) {
            case "Terran": {
                Deck d2 = new Deck(dpr.parseFile("BotImbaDeck.xml"));
                d2.shuffleCards();
                g.configure(sc, new players.SimpleBot(), d, d2, 15, 15);
                break;
            }
            case "Player": {
                synchronized (synchronizer) {
                    if (seeker == null) {
                        seeker = new ClientConfiguration();
                        seeker.deck = d;
                        seeker.client = sc;
                        return null;
                    } else {
                        
                    }
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
