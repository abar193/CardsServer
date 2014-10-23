package input;

import java.util.Map;
import java.util.logging.Logger;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import cards.BasicCard;
import cards.CardJSONOperations;
import cards.Deck;

/**
 * Decodes socket's input.
 * @author Abar
 *
 */
public class SocketDecoder implements Decoder.Text<SocketInput> {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    
    /** Constructs decoder. */
    public SocketDecoder() {
        
    }

    @Override
    public void destroy() {
        
    }

    @Override
    public void init(EndpointConfig arg0) {
        
    }

    @Override
    public SocketInput decode(String arg0) throws DecodeException {
        logger.info("Got message: " + arg0.substring(0, Math.max(10, arg0.length())));
        try {
            JSONObject o = (JSONObject) JSONValue.parse(arg0);
            if (o.containsKey("deck")) {
                Deck d = new Deck();
                JSONArray jarr = (JSONArray) o.get("deck");
                String playerOpponent = (String) o.get("opponent");
                java.util.Iterator<Map<String, String>> i = jarr.iterator();
                CardJSONOperations oper = new CardJSONOperations();
                while (i.hasNext()) {
                    BasicCard c = oper.cardFromMap(i.next());
                    if (c != null) {
                        d.addCard(c);
                    }
                }
                return new DeckInput(d, playerOpponent);
            } else if (o.containsKey("action")) {
                return actionAnalyser(o);
            } else if (o.containsKey("return")) { 
                return responseAnalyser(o);
            } else if (o.containsKey("command")) {
                return new TextCommandInput((String)o.get("command"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return null;
    }
    
    public ResponseInput responseAnalyser(JSONObject o) {
        String target = (String)o.get("return");
        switch(target) {
            case "selectTarget":
                int pn = -1, un = -1;
                try {
                    pn = Integer.parseInt((String)o.get("side"));
                    un = Integer.parseInt((String)o.get("position"));
                } catch(Exception e) {
                    e.printStackTrace();
                    pn = -1;
                    un = -1;
                }
                return new ResponseInput(target, pn, un);
            default:
                break;
        }
        
        return null;
    }
    
    public ActionInput actionAnalyser(final JSONObject o) {
        
        String action = (String)o.get("action");
        switch(action) {
            case "play":
                return new ActionInput("play");
                
            case "canPlayCard":
            case "playCard": {
                BasicCard c = null;
                int pn = -1;
                try {
                    c = new cards.CardJSONOperations().cardFromMap(
                            (Map<String, String>) o.get("card"));
                    pn = Integer.parseInt((String)o.get("player"));
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return new ActionInput(action, c, pn);
            }
            
            case "attackIsValid":
            case "commitAttack": {
                int ua = -2, ut = -2, p = -1;
                try {
                    ua = Integer.parseInt((String)o.get("attacker"));
                    ut = Integer.parseInt((String)o.get("target"));
                    p  = Integer.parseInt((String)o.get("player"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return new ActionInput(action, ua, ut, p); 
            }
            
            case "endTurn" : {
                int pn = -1;
                try {
                    pn = Integer.parseInt((String)o.get("player"));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                return new ActionInput(action, pn);
            }
            
            case "quitGame": {
                int pn = -1;
                try {
                    pn = Integer.parseInt((String)o.get("player"));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                return new ActionInput(action, pn);
            }
            default: {
                return new ActionInput("unknown");
            }
        }
    }

    @Override
    public boolean willDecode(String arg0) {
        return true;
    }

}
