package server;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.*;

import org.json.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import cards.BasicCard;
import decks.*;

//@ServerEndpoint(value = "/cards")
public class CardsServer extends HttpServlet {
 
	private static final long serialVersionUID = -3751044935112459000L;
	
	private final String[] names = {"Neutrals", "Machines"};
	
	private static ArrayList<ArrayList<BasicCard>> allDecks;
	
	private final int version = 10;
	
	private static Set<CardsServer> connections = new CopyOnWriteArraySet<>();
	
	public void init() throws ServletException
	{
		allDecks = cards.CardJSONOperations.singleAllDeck();
	}
	
	
	@Override
	public void doGet(HttpServletRequest request,
                    HttpServletResponse response)
            throws ServletException, IOException
    {
		// Set response content type
		response.setContentType("text/html");

		// Actual logic goes here.
		PrintWriter out = response.getWriter();
		out.println("<html><head><title>Server status</title></head><body>\n"
				+ "<h2>Cards Server is up and running</h2>\n"
				+ "Server version: " + version);
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