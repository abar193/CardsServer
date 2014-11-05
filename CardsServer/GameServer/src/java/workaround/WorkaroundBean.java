package workaround;

import cards.Deck;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import lobbies.FactoryInterface;
import lobbies.SocketClientInterface;
import src.Game;

/**
 *
 * @author Abar
 */
@Stateless
public class WorkaroundBean implements WorkaroundBeanLocal {
    
    @EJB
    FactoryInterface factory;
    
    @Override
    public Game provideGame(Deck d, SocketClientInterface sc, String opponent) {
        return factory.provideGame(d, sc, opponent);
    }

    @Override
    public boolean cancelSearchFor(SocketClientInterface sc) {
        return factory.cancelSearchFor(sc);
    }
}
