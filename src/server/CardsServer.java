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
	private final String[] links = {"NeutralsDeck.xml", "MachinesDeck.xml"};

	private static ArrayList<ArrayList<BasicCard>> allDecks;
	
	private final int version = 9;
	
	private static Set<CardsServer> connections = new CopyOnWriteArraySet<>();
	
	public void init() throws ServletException
	{
		allDecks = new ArrayList<ArrayList<BasicCard>>(links.length);
		DeckPackReader dpr = new DeckPackReader();
		for(String s : links) {
			allDecks.add(dpr.parseFile(s));
		}
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
  
	public void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException 
    {
		response.setContentType("text/html");

		// Actual logic goes here.
		PrintWriter out = response.getWriter();
		Enumeration paramNames = request.getParameterNames();
		out.println("Request recived\n");
		StringBuilder sb = new StringBuilder();
	    BufferedReader reader = request.getReader();
	    try {
	        String line;
	        while ((line = reader.readLine()) != null) {
	            sb.append(line).append('\n');
	        }
	    } finally {
	        reader.close();
	    }
	    
	    JSONParser parser = new JSONParser();
	    try {
			JSONObject jobj = (JSONObject)parser.parse(sb.toString());
			JSONArray arr = (JSONArray)jobj.get("Cards");
			Iterator<Map> i = arr.iterator();
			while(i.hasNext()) {
				Map m = i.next();
				out.println(m.get("Name"));
			}
		} catch (Exception e) {
			out.println("Sorry I kinda had excetion");
		}
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
	
	public static void main(String[] s) 
	{
		StringBuilder sb = new StringBuilder("{\"Cards\":[{\"Name\":\"Bulbasaur\",\"Cost\":1},{\"Name\":\"Pikachu\",\"Cost\":12}]}");
		JSONParser parser = new JSONParser();
	    try {
			JSONObject jobj = (JSONObject)parser.parse(sb.toString());
			JSONArray arr = (JSONArray)jobj.get("Cards");
			Iterator<Map> i = arr.iterator();
			while(i.hasNext()) {
				Map m = i.next();
				System.out.println("Card: " + m.get("Name") + " ");
			}
		} catch (Exception e) {
			System.out.println("Sorry I kinda had excetion");
		}
	}
}