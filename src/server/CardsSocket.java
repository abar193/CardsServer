package server;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import javax.websocket.*;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.server.ServerEndpoint;

import org.json.simple.*;
import org.json.*;
 
import cards.*;

@ServerEndpoint(value = "/sock")
public class CardsSocket  {
 
    private Logger logger = Logger.getLogger(this.getClass().getName());
 
    private Deck clientDeck;
    
    @OnOpen
    public void onOpen(Session session) {
        logger.info("Connected ... " + session.getId());
        try {
			session.getBasicRemote().sendText("Hi");
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
 
    @OnMessage
    public String onMessage(String message, Session session) {
    	try {
    		JSONObject o = (JSONObject) JSONValue.parse(message);
    		if(o.containsKey("Deck")) {
    			clientDeck = new Deck();
    			JSONArray jarr = (JSONArray) o.get("Deck");
    			java.util.Iterator<Map<String, String>> i = jarr.iterator();
    			CardJSONOperations oper = new CardJSONOperations();
    			while(i.hasNext()) {
    				BasicCard c = oper.cardFromMap(i.next());
    				if(c != null) {
    					clientDeck.addCard(c);
    				}
    			}
    			if(clientDeck.validateCards()) 
    				return "Deck ok";
    			else 
    				return "Deck fail";
    		}
    	} catch(Exception e) {
    		logger.info("onMessage: " + e.getStackTrace().toString());
    		return "fail";
    	}
    	
    	return "unclear";
    }
 
    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        logger.info(String.format("Session %s closed because of %s", session.getId(), closeReason));
    }
    
    public static void main(String[] args) {
    	CardsSocket sock = new CardsSocket();
    	String m = "{\"Deck\":[{\"Name\":\"Cyborg\",\"Cost\":\"4\",\"Deck\":\"1\"},{\"Name\":\"Smart Lamp\",\"Cost\":\"1\",\"Deck\":\"1\"},{\"Name\":\"Taunt 0-4\",\"Cost\":\"1\",\"Deck\":\"0\"}]}";
    	System.out.println(sock.onMessage(m, null));
    }

}