/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lobbies;

import cards.Deck;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import src.Game;

/**
 *
 * @author Abar
 */
@Singleton
public class FactoryCaller implements FactoryCallerLocal {

    private Logger logger;
    
    @PostConstruct
    public void init() {
        logger = Logger.getLogger("FactoryCaller");
        logger.log(Level.INFO, "Init");
    }
    
    @Override
    public Game provideGame(Deck d, SocketClientInterface sc, String opponent) {
        return null;
    }

    @Override
    public boolean cancelSearchFor(SocketClientInterface sc) {
        return false;
    }

}
