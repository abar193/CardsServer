package stats;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import lobbies.StatCollectorLocal;

/**
 *
 * @author Abar
 */
@ManagedBean
@RequestScoped
public class InfoProvider {
 
    @EJB
    StatCollectorLocal collector;
    private int someValue = 42;

    
    public int getSomeValue() {
        return someValue;
    }
    public void setSomeValue(int someValue) {
        this.someValue = someValue;
    }

    public int getActiveSearches() {
        System.out.println(">>Returning searchers");
        return collector.getActiveSearchers();
    }
    
    public int getActiveGames() {
        System.out.println(">>Returning games");
        return collector.getActiveGames();
    }
    
    /**
     * Creates a new instance of InfoProvider
     */
    public InfoProvider() {
    }
    
}
