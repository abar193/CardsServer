/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lobbies;

import javax.ejb.Singleton;

/**
 *
 * @author Abar
 */
@Singleton
public class StatCollector implements StatCollectorLocal {

    private int clicks = 0;    
    private int activeGames = 0;
    private int activeSearchers = 0;

    public int getActiveGames() {
        System.out.println("Returning activeGames: " + activeGames);
        return activeGames;
    }
    public void setActiveGames(int activeGames) {
        this.activeGames = activeGames;
        System.out.println("Set activeGames to: " + activeGames);
    }

    public int getActiveSearchers() {
        System.out.println("Returning activeSearchers: " + activeSearchers);
        return activeSearchers;
    }
    public void setActiveSearchers(int activeSearchers) {
        System.out.println("Set activeSearchers to: " + activeSearchers);
        this.activeSearchers = activeSearchers;
    }

    @Override
    public void incClicker() {
        clicks++;
    }

    @Override
    public int getClicks() {
        return clicks;
    }
    
}
