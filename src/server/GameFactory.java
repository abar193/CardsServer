package server;

import src.Game;
import ui.SwingVS;
import cards.Deck;
import decks.DeckPackReader;

public class GameFactory {

	public GameFactory() {
		
	}
	
	private static GameFactory instance = new GameFactory();
	
	public static GameFactory instance() {
		return instance;
	}
	
	public Game provideGame(Deck d, SocketClient sc, String opponent) {
		DeckPackReader dpr = new DeckPackReader();
		d.shuffleCards();
		Game g = new Game();

		switch (opponent) {
			case "Terran": {
				Deck d2 = new Deck(dpr.parseFile("BotImbaDeck.xml"));
				d2.shuffleCards();
				g.configure(sc, new players.SimpleBot(), d, d2, 15, 15);
				break;
			}
		}
		
		return g;
	}

}
