package server;

import java.io.*;
import java.util.ArrayList;

import javax.servlet.*;
import javax.servlet.http.*;

import cards.BasicCard;
import decks.*;

public class CardsServer extends HttpServlet {
 
	private static final long serialVersionUID = -3751044935112459000L;
	
	
	private final String[] names = {"Neutrals", "Machines"};
	private final String[] links = {"NeutralsDeck.xml", "MachinesDeck.xml"};

	private ArrayList<ArrayList<BasicCard>> allDecks;
	
	public void init() throws ServletException
	{
		allDecks = new ArrayList<ArrayList<BasicCard>>(links.length);
		DeckPackReader dpr = new DeckPackReader();
		for(String s : links) {
			allDecks.add(dpr.parseFile(s));
		}
	}

	public void doGet(HttpServletRequest request,
                    HttpServletResponse response)
            throws ServletException, IOException
    {
      // Set response content type
		response.setContentType("text/html");

		// Actual logic goes here.
		PrintWriter out = response.getWriter();
		out.println("<html><head><title>Server status</title></head><body>\n"
				+ "<h2>Cards Server is up and running</h2>");
		out.println("<table><tr><td>Deck</td><td>cards count</td></tr>");
		for(int i = 0; i < names.length; i++) {
			out.format("<tr><td> %s </td><td> %d </td></tr>\n", names[i], allDecks.get(i).size());
		}
		out.println("</table>");
		out.println("</body></html>");
		

    }
  
	  public void destroy()
	  {
	      // do nothing.
	  }
}