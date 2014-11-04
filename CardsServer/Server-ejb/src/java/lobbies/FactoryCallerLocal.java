/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lobbies;

import cards.Deck;
import javax.ejb.Local;
import src.Game;

/**
 *
 * @author Abar
 */
@Local
public interface FactoryCallerLocal {
    public Game provideGame(final Deck d, final SocketClientInterface sc, final String opponent);
    public boolean cancelSearchFor(SocketClientInterface sc);   
}
