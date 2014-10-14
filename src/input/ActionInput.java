package input;

import cards.BasicCard;

public class ActionInput implements SocketInput {

    private String command;
    
    private BasicCard c;
    private int playerNumber;
    
    private int attacker, target;
    
    /** One-command configuration. */
    public ActionInput(String command) {
        this.command = command;
    }
    
    /** One-command configuration with player number. */
    public ActionInput(String command, int playerNumber) {
        this.command = command;
        this.playerNumber = playerNumber;
    }
    
    /** (Can)PlayCard configuration. */
    public ActionInput(String command, BasicCard c, int playerNumber) {
        this.command = command;
        this.c = c;
        this.playerNumber = playerNumber;
    }

    /** Attack(IsValid) configuration. */
    public ActionInput(String command, int attacker, int target, int playerNumber) {
        this.command = command;
        this.attacker = attacker;
        this.target = target;
        this.playerNumber = playerNumber;
    }
    
    public int getPlayerNumber() {
        return playerNumber;
    }
    
    public int getAttacker() {
        return attacker;
    }
    
    public int getTarget() {
        return target;
    }
    
    public BasicCard getCard() {
        return c;
    }
    
    @Override
    public String getCommand() {
        return command;
    }

}
