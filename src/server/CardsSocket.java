package server;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.websocket.*;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.server.ServerEndpoint;

import lobbies.GameFactory;
import input.DeckInput;
import input.ActionInput;
import input.SocketInput;
import input.ResponseInput;
import input.SocketDecoder;

import org.json.simple.*;

import cards.BasicCard;
import cards.CardJSONOperations;
import cards.Deck;
import network.ServerResponses;
import src.Game;

@ServerEndpoint(
    value = "/sock",
    decoders = { SocketDecoder.class }
)
public class CardsSocket {
	
    private Logger logger = Logger.getLogger("localhost/CardsSocket");
 
    private Deck clientDeck;
    private Game clientGame;
    private String playerOpponent;
    public SocketClient client;
    private Session session;
    
    @EJB
    GameFactory fact;
    
    @OnOpen
    public void onOpen(Session session) {
        logger.info("Connected... " + session.getId());
        logger.info("Addressed: " + CardsServer.adressedCount);
        try {
			session.getBasicRemote().sendText("Hi");
			client = new SocketClient(this);
			this.session = session;
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    @OnMessage
    public void onMessage(final SocketInput message, final Session session) {
		if (message instanceof DeckInput) {
			DeckInput di = (DeckInput)message;
			
   			if (di.getDeck().validateCards()) {
   			    clientDeck = di.getDeck();
   			    playerOpponent = di.getOpponent();
				sendText(generateJSONResponse("deck", ServerResponses.ResponseOk));
			} else {
				clientDeck = null;
				sendText(generateJSONResponse("deck", ServerResponses.
				        ResponseFail));
			}
			return;
		} else if (message instanceof ActionInput) {
			String ret = actionAnalyser((ActionInput)message); 
			sendText(ret);
			return;
		} else if (message instanceof ResponseInput) { 
			responseAnalyser((ResponseInput) message); 
			return;
		}  else if(message instanceof input.TextCommandInput) {
		    commandAnalyser(message.getCommand());
		    return;
		}

    	sendText("Unclear");
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        fact.cancelSearchFor(client);
        logger.info(String.format("Session %s closed.", session.getId()));
    }

    @SuppressWarnings("unchecked")
	public String generateJSONResponse(String toAction, String response) {
    	JSONObject jobj = new JSONObject();
    	jobj.put("response", toAction);
    	jobj.put("status", response);
    	return JSONValue.toJSONString(jobj);
    }
    
    @OnError
    public void error(Session session, Throwable t) {
        //t.printStackTrace();
    }
    
    public void sendText(String message) {
    	if(message == null) return;
    	synchronized (session) {
    	    if (session.isOpen()) {
    	        try { 
    	        	logger.info("Sending: " + message);
					session.getBasicRemote().sendText(message);
				} catch (IOException e) {
					e.printStackTrace();
				}
    	    }
    	}
    }
    
	public void responseAnalyser(ResponseInput ri) {
    	String target = ri.getTarget();
    	switch(target) {
    		case "selectTarget":
    			client.selectedUnitSide = ri.getSide();
    			client.selectedUnitPosition = ri.getPosition();
    			return;
			default:
				break;
    	}
    }
    
	public void commandAnalyser(final String command) {
	    if(command.equals("cancelSearchGame")) {
	        if(fact.cancelSearchFor(this.client)) {
	            sendText(generateJSONResponse("cancelSearchGame", ServerResponses.ResponseOk));
	        } else {
	            sendText(generateJSONResponse("cancelSearchGame", ServerResponses.ResponseFail));
	        }
	    }
	}
	
    /**
     * Translates client action and passes it to game instance. 
     * @param o client input
     * @return answer string for client
     */
	public String actionAnalyser(final ActionInput ai) {
    	String action = ai.getCommand();
    	switch(action) {
    		case "play":
    			if (clientDeck != null) {
    				clientGame = fact.provideGame(clientDeck, client,
    				        playerOpponent);
    				if (clientGame != null) {
    				    sendText(generateJSONResponse(action, ServerResponses.
    				            ResponseOk));
    				    launchGame();
    				} else {
    				    sendText(generateJSONResponse(action, ServerResponses.
                                ResponseWait));
    				}
    				
    			} else {
    				logger.info("Client tried to play without a valid deck");
    				sendText(generateJSONResponse(action, ServerResponses.
    				        ResponseIllegal));
    			}
    			return null;
    		case "canPlayCard": {
    			final BasicCard c = ai.getCard();
    			final int pn = ai.getPlayerNumber();
    			
    			if (c == null || pn != client.playerNumber) {
    			    return generateJSONResponse(action, ServerResponses.
                            ResponseIllegal);
                }
    			
    			String resp = (clientGame.canPlayCard(c, pn)) 
    			        ? ServerResponses.ResponseTrue : ServerResponses.ResponseFalse;
    			return generateJSONResponse(action, resp);
    		}
    		
    		case "playCard": {
    		    final BasicCard c = ai.getCard();
                final int pn = ai.getPlayerNumber();
                
                if (c == null || pn != client.playerNumber) {
                    return generateJSONResponse(action, ServerResponses.
                            ResponseIllegal);
                }
    			new Thread(new Runnable() {
					@Override
					public void run() {
						clientGame.playCard(c, pn);
					}
    			}).start();
    			
    			return generateJSONResponse(action, ServerResponses.ResponseOk);
    		}
    		
    		case "attackIsValid": {
    			final int ua = ai.getAttacker();
    			final int ut = ai.getTarget();
    			final int p = ai.getPlayerNumber();
    			if (p != client.playerNumber) {
    			    return generateJSONResponse(action, ServerResponses.
                            ResponseIllegal);
                }
    			String ret = (clientGame.attackIsValid(ua, ut, p)) ? 
    			        ServerResponses.ResponseTrue : ServerResponses.ResponseFalse;
    			return generateJSONResponse(action, ret); 
    		}
    		
    		case "commitAttack": {
    		    final int ua = ai.getAttacker();
                final int ut = ai.getTarget();
                final int p = ai.getPlayerNumber();
                if (p != client.playerNumber) {
                    return generateJSONResponse(action, ServerResponses.
                            ResponseIllegal);
                }
    			clientGame.commitAttack(ua, ut, p);
    			return generateJSONResponse(action, ServerResponses.ResponseOk);
    		}
    		
    		case "endTurn" : {
    		    final int p = ai.getPlayerNumber();
                if (p != client.playerNumber) {
                    return generateJSONResponse(action, ServerResponses.
                            ResponseIllegal);
                }
                
    			clientGame.endTurn(p);
    			return generateJSONResponse(action, ServerResponses.ResponseOk);
    		}
			default:
				return generateJSONResponse(action, ServerResponses.
				        ResponseUnknownCommand);
    	}
    }
    
	public void setGame(Game g) {
	    clientGame = g;
	}
	
    /**
     * Called in separate thread to launch the game
     */
    public void launchGame() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
					logger.info("Game.play()");
					clientGame.play();
				} catch (InterruptedException e) {
					logger.info("CardsSocket.launchGame -> Game interrupted!");
					e.printStackTrace();
				}
			}
			
		}).start();
    }

}
