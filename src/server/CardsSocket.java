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

import cards.BasicCard;
import cards.CardJSONOperations;
import cards.Deck;
import network.ServerResponses;
import src.Game;

@ServerEndpoint(value = "/sock")
public class CardsSocket  {
 
    private Logger logger = Logger.getLogger(this.getClass().getName());
 
    private Deck clientDeck;
    private Game clientGame;
    private String playerOpponent;
    private SocketClient client;
    private Session session;
    
    @OnOpen
    public void onOpen(Session session) {
        logger.info("Connected... " + session.getId());
        try {
			session.getBasicRemote().sendText("Hi");
			client = new SocketClient(this);
			this.session = session;
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    @OnMessage
    public String onMessage(String message, Session session) {
    	try {
    		JSONObject o = (JSONObject) JSONValue.parse(message);
    		if(o.containsKey("deck")) {
    			clientDeck = new Deck();
    			JSONArray jarr = (JSONArray) o.get("deck");
    			playerOpponent = (String) o.get("opponent");
    			java.util.Iterator<Map<String, String>> i = jarr.iterator();
    			CardJSONOperations oper = new CardJSONOperations();
    			while(i.hasNext()) {
    				BasicCard c = oper.cardFromMap(i.next());
    				if(c != null) {
    					clientDeck.addCard(c);
    				}
    			}
    			if(clientDeck.validateCards()) {
    				return ServerResponses.ResponseOk;
    			} else {
    				clientDeck = null;
    				return ServerResponses.ResponseFail;
    			}
    		} else if(o.containsKey("action")) {
    			String ret = actionAnalyser(o); 
    			logger.info("Returning " + ret);
    			return ret;
    		}
    	} catch(Exception e) {
    		logger.info("Exception: onMessage: " + e.getStackTrace());
    		e.printStackTrace();
    		return ServerResponses.ResponseIllegal;
    	}
    	
    	return ServerResponses.ResponseFail;
    }
 
    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        logger.info(String.format("Session %s closed because of %s", session.getId(), closeReason));
    }
    
    public void sendText(String message) {
    	try {
			session.getBasicRemote().sendText(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    @SuppressWarnings("unchecked")
	public String actionAnalyser(JSONObject o) {
    	switch((String)o.get("action")) {
    		case "play":
    			if(clientDeck != null) {
    				logger.info("Game starting!");
    				clientGame = GameFactory.instance().provideGame(clientDeck, client, playerOpponent);
    				launchGame();
    				return ServerResponses.ResponseOk;
    			} else {
    				return ServerResponses.ResponseIllegal;
    			}
    		case "canPlayCard": {
    			BasicCard c;
    			int pn;
    			try {
	    			c = new cards.CardJSONOperations().cardFromMap((Map<String, String>) o.get("card"));
	    			pn = Integer.parseInt((String)o.get("player"));
	    			if(c == null || pn != client.playerNumber) throw new Exception("Wrong input");
    			} catch(Exception e) {
    				return ServerResponses.ResponseIllegal;
    			}
    			return (clientGame.canPlayCard(c, pn)) ? ServerResponses.ResponseTrue : ServerResponses.ResponseFalse;
    		}
    		
    		case "playCard": {
    			BasicCard c;
    			int pn;
    			try {
	    			c = new cards.CardJSONOperations().cardFromMap((Map<String, String>) o.get("card"));
	    			pn = Integer.parseInt((String)o.get("player"));
	    			if(c == null || pn != client.playerNumber) throw new Exception("Wrong input");
    			} catch(Exception e) {
    				return ServerResponses.ResponseIllegal;
    			}
    			clientGame.playCard(c, pn);
    			return ServerResponses.ResponseOk;
    		}
    		
    		case "attackIsValid": {
    			int ua, ut, p;
    			try {
    				ua = Integer.parseInt((String)o.get("attacker"));
    				ut = Integer.parseInt((String)o.get("target"));
	    			p = Integer.parseInt((String)o.get("player"));
	    			if(p != client.playerNumber) throw new Exception("Wrong input");
    			} catch(Exception e) {
    				return ServerResponses.ResponseIllegal;
    			}
    			return (clientGame.attackIsValid(ua, ut, p)) ? ServerResponses.ResponseTrue : ServerResponses.ResponseFalse;
    		}
    		
    		case "commitAttack": {
    			int ua, ut, p;
    			try {
    				ua = Integer.parseInt((String)o.get("attacker"));
    				ut = Integer.parseInt((String)o.get("target"));
	    			p = Integer.parseInt((String)o.get("player"));
	    			if(p != client.playerNumber) throw new Exception("Wrong input");
    			} catch(Exception e) {
    				return ServerResponses.ResponseIllegal;
    			}
    			clientGame.commitAttack(ua, ut, p);
    		}
			default:
				return "";
    	}
    }
    
    public void launchGame() {
    	
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
					logger.info("Game.play()");
					clientGame.play();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}).start();
    }
    
    public static void main(String[] args) {
    	CardsSocket sock = new CardsSocket();
    	String m = "{\"opponent\":\"Terran\",\"deck\":[{\"Name\":\"Smart Lamp\",\"Cost\":\"1\",\"Deck\":\"1\"},{\"Name\":\"Tosters!!!\",\"Cost\":\"1\",\"Deck\":\"1\"},{\"Name\":\"Tosters!!!\",\"Cost\":\"1\",\"Deck\":\"1\"},{\"Name\":\"Cleaner\",\"Cost\":\"2\",\"Deck\":\"1\"},{\"Name\":\"Cleaner\",\"Cost\":\"2\",\"Deck\":\"1\"},{\"Name\":\"R-Hit\",\"Cost\":\"2\",\"Deck\":\"1\"},{\"Name\":\"Strike\",\"Cost\":\"2\",\"Deck\":\"1\"},{\"Name\":\"Spambot\",\"Cost\":\"2\",\"Deck\":\"1\"},{\"Name\":\"Optimiser\",\"Cost\":\"2\",\"Deck\":\"1\"},{\"Name\":\"C 1-1\",\"Cost\":\"1\",\"Deck\":\"0\"},{\"Name\":\"Cyborg\",\"Cost\":\"4\",\"Deck\":\"1\"},{\"Name\":\"Cyborg\",\"Cost\":\"4\",\"Deck\":\"1\"},{\"Name\":\"G-Car\",\"Cost\":\"9\",\"Deck\":\"1\"},{\"Name\":\"Bomb\",\"Cost\":\"3\",\"Deck\":\"1\"},{\"Name\":\"Solid 1-2\",\"Cost\":\"1\",\"Deck\":\"0\"}]}";
    	System.out.println(sock.onMessage(m, null));
    }

}