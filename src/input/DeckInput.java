package input;

import cards.Deck;

public class DeckInput implements SocketInput {

    private Deck d;
    private String opponent;
    
    public DeckInput(Deck nd, String newopp) {
        d = nd;
        opponent = newopp;
    }
    
    public Deck getDeck() {
        return d;
    }
    
    public String getOpponent() {
        return opponent;
    }

    @Override
    public String getCommand() {
        return "Deck";
    }

}
