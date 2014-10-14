package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.websocket.server.ServerEndpoint;

import cards.BasicCard;

//@ServerEndpoint(value = "/cards")
/**
 * Cards server, displays some game statistics.
 * @author Abar
 */
public class CardsServer extends HttpServlet {

    /** svUID. */
    private static final long serialVersionUID = -3751044935112459000L;
    
    /** Names of usefull decks. */
    private final String[] names = {"Neutrals", "Machines"};
    
    /** List of all presented decks. */
    private static ArrayList<ArrayList<BasicCard>> allDecks;
    
    /** Version of the server. */
    private final int version = 10;
    
    /** Test value. */
    //Resource(name="java:comp/AdressedCount")
    public static int adressedCount = 0;
    
    public void init() throws ServletException
    {
        allDecks = cards.CardJSONOperations.singleAllDeck();
    }
    
    @Override
    public void doGet(HttpServletRequest request,
                    HttpServletResponse response)
            throws ServletException, IOException {
        // Set response content type
        response.setContentType("text/html");
        adressedCount++;
        // Actual logic goes here.
        PrintWriter out = response.getWriter();
        out.println("<html><head><title>Server status</title></head><body>\n"
                + "<h2>Cards Server is up and running</h2>\n"
                + "Server version: " + version + " / " + adressedCount);
        out.println("<table><tr><td>Deck</td><td>cards count</td></tr>");
        for(int i = 0; i < names.length; i++) {
            out.format("<tr><td> %s </td><td> %d </td></tr>\n", names[i], allDecks.get(i).size());
        }
        out.println("</table>");
        out.println("</body></html>");

    }
  
    public void destroy()
    {
        
    }
    
    public static int totalCards() {
        int s = 0;
        for(ArrayList<BasicCard> al : allDecks) {
            s += al.size();
        }
        return s;
    }
}
