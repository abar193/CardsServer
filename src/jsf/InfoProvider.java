package jsf;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import lobbies.GamesHolder;

@Named
@SessionScoped
public class InfoProvider implements Serializable {

    private static final long serialVersionUID = 4556869256275414451L;

    @EJB
    GamesHolder gamesHolder;
    
    public InfoProvider() {
        
    }
    
    public Integer getPlayingPlayers() {
        return gamesHolder.players();
    }

}
