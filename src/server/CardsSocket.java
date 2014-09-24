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
public class CardsSocket {
 
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
    public void onMessage(String message, Session session) {
    	logger.info("Got message: " + message.substring(0, Math.max(10, message.length())));
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
    				sendText(generateJSONResponse("deck", ServerResponses.ResponseOk));
    			} else {
    				clientDeck = null;
    				sendText(generateJSONResponse("deck", ServerResponses.ResponseFail));
    			}
    			return;
    		} else if(o.containsKey("action")) {
    			String ret = actionAnalyser(o); 
    			sendText(ret);
    			return;
    		}
    	} catch(Exception e) {
    		e.printStackTrace();
    		sendText(ServerResponses.ResponseIllegal);
    		return;
    	}

    	sendText("Unclear");
    }
 
    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        logger.info(String.format("Session %s closed because of %s", session.getId(), closeReason));
    }
    
    public String generateJSONResponse(String toAction, String response) {
    	JSONObject jobj = new JSONObject();
    	jobj.put("response", toAction);
    	jobj.put("status", response);
    	return JSONValue.toJSONString(jobj);
    }
    
    public  void sendText(String message) {
    	if(message == null) return;
    	synchronized (session) {
    	    if (session.isOpen()) {
    	        try { 
    	        	logger.info("Sending: " + message.substring(0, Math.min(message.length(), 15)));
					session.getBasicRemote().sendText(message);
				} catch (IOException e) {
					e.printStackTrace();
				}
    	    }
    	}
    }
    
    @SuppressWarnings("unchecked")
	public String actionAnalyser(JSONObject o) {
    	String action = (String)o.get("action");
    	switch((String)o.get("action")) {
    		case "play":
    			if(clientDeck != null) {
    				logger.info("Game starting!");
    				clientGame = GameFactory.instance().provideGame(clientDeck, client, playerOpponent);
    				sendText(generateJSONResponse(action, ServerResponses.ResponseOk));
    				launchGame();
    			} else {
    				logger.info("Client tried to play without a valid deck");
    				sendText(generateJSONResponse(action, ServerResponses.ResponseIllegal));
    			}
    			return null;
    		case "canPlayCard": {
    			BasicCard c;
    			int pn;
    			try {
	    			c = new cards.CardJSONOperations().cardFromMap((Map<String, String>) o.get("card"));
	    			pn = Integer.parseInt((String)o.get("player"));
	    			if(c == null) throw new Exception("Wrong card");
	    			if(pn != client.playerNumber) throw new Exception("Wrong player number");
    			} catch(Exception e) {
    				e.printStackTrace();
    				logger.info(String.format("For card %s player %s was an exception", o.get("card"), 
    						o.get("player")));
    				return generateJSONResponse(action, ServerResponses.ResponseIllegal);
    			}
    			String resp = (clientGame.canPlayCard(c, pn)) ? ServerResponses.ResponseTrue : ServerResponses.ResponseFalse;
    			return generateJSONResponse(action, resp);
    		}
    		
    		case "playCard": {
    			BasicCard c;
    			int pn;
    			try {
	    			c = new cards.CardJSONOperations().cardFromMap((Map<String, String>) o.get("card"));
	    			pn = Integer.parseInt((String)o.get("player"));
	    			if(c == null) throw new Exception("Wrong card");
	    			if(pn != client.playerNumber) throw new Exception("Wrong player number");
    			} catch(Exception e) {
    				e.printStackTrace();
    				return generateJSONResponse(action, ServerResponses.ResponseIllegal);
    			}
    			
    			clientGame.playCard(c, pn);
    			return generateJSONResponse(action, ServerResponses.ResponseOk);
    		}
    		
    		case "attackIsValid": {
    			int ua, ut, p;
    			try {
    				ua = Integer.parseInt((String)o.get("attacker"));
    				ut = Integer.parseInt((String)o.get("target"));
	    			p  = Integer.parseInt((String)o.get("player"));
	    			if(p != client.playerNumber) throw new Exception("Wrong input");
    			} catch(Exception e) {
    				e.printStackTrace();
    				return generateJSONResponse(action, ServerResponses.ResponseIllegal);
    			}
    			String ret = (clientGame.attackIsValid(ua, ut, p)) ? ServerResponses.ResponseTrue : ServerResponses.ResponseFalse;
    			return generateJSONResponse(action, ret); 
    		}
    		
    		case "commitAttack": {
    			int ua, ut, p;
    			try {
    				ua = Integer.parseInt((String)o.get("attacker"));
    				ut = Integer.parseInt((String)o.get("target"));
	    			p  = Integer.parseInt((String)o.get("player"));
	    			if(p != client.playerNumber) throw new Exception("Wrong input");
    			} catch(Exception e) {
    				e.printStackTrace();
    				return generateJSONResponse(action, ServerResponses.ResponseIllegal);
    			}
    			clientGame.commitAttack(ua, ut, p);
    			return generateJSONResponse(action, ServerResponses.ResponseOk);
    		}
			default:
				return generateJSONResponse(action, ServerResponses.ResponseIllegal);
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

}