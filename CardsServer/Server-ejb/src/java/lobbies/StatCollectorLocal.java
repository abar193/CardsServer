/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lobbies;

import javax.ejb.Local;

/**
 *
 * @author Abar
 */
@Local
public interface StatCollectorLocal {

    void incClicker();
    int getClicks();
    public int getActiveGames();
    public void setActiveGames(int activeGames);
    public int getActiveSearchers();
    public void setActiveSearchers(int activeSearchers);
    
}
