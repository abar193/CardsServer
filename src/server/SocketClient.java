package server;

import java.util.ArrayList;

import cards.SpellCard;
import players.PlayerData;
import players.PlayerInterface;
import players.PlayerOpenData;
import src.FieldSituation;
import src.GameInterface;
import ui.InputInterface;
import ui.VisualSystemInterface;
import units.TriggeringCondition;
import units.Unit;

import org.json.simple.*;
import org.json.*;

public class SocketClient implements PlayerInterface, VisualSystemInterface {

	private CardsSocket sock;
	public int playerNumber;
	public int selectedUnitSide, selectedUnitPosition;
	
	private FieldSituation latestSituation;
	
	public SocketClient(CardsSocket sock) {
		this.sock = sock;
	}
	
	@Override
	public void displayError(String m) {
		
	}

	@Override
	public void displayMessage(String m) {
		// TODO Auto-generated method stub

	}

	@Override
	public void displayFieldState(PlayerData p1, FieldSituation field,
			PlayerOpenData p2) 
	{
	}

	@Override
	public void displayAttack(Unit u1, Unit u2, boolean died1, boolean died2) {
		

	}

	@Override
	public void displayPower(Unit u, TriggeringCondition e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispaySpell(SpellCard s, int player) {
		// TODO Auto-generated method stub

	}

	@Override
	public void displayUnitDamage(Unit u, int damage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setInputInterface(InputInterface i) {
		// TODO Auto-generated method stub

	}

	@Override
	public Unit provideUnit() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void read() {
		// TODO Auto-generated method stub

	}

	/* Player interface */
	
	@SuppressWarnings("unchecked")
	@Override
	public void reciveInfo(PlayerData yourData, FieldSituation field,
			PlayerOpenData enemyData) 
	{
		latestSituation = field;
		this.playerNumber = yourData.playerNumber;
		JSONObject jobj = new JSONObject();
		jobj.put("target", "player");
		jobj.put("action", "reciveInfo");
		jobj.put("yourData", yourData.toMap());
		jobj.put("field", field.toMap());
		jobj.put("enemyData", enemyData.toMap());
		sock.sendText(JSONValue.toJSONString(jobj));
	}

	@Override
	public void reciveAction(String m) { // PI
		JSONObject jobj = new JSONObject();
		jobj.put("target", "player");
		jobj.put("action", "reciveAction");
		jobj.put("message", m);
		sock.sendText(JSONValue.toJSONString(jobj));
	}

	@Override
	// PI, unused here
	public void setParentGameInterface(GameInterface g) {
		
	}

	@Override
	// PI
	public void run() {
		JSONObject jobj = new JSONObject();
		jobj.put("target", "player");
		jobj.put("action", "run");
		sock.sendText(JSONValue.toJSONString(jobj));
		
		try {
			while (true) {
				Thread.sleep(100);
			}
		} catch (InterruptedException e) {
 
		}
	}

	
	@Override
	// PI
	public Unit selectTarget() {
		JSONObject jobj = new JSONObject();
		jobj.put("target", "player");
		jobj.put("action", "selectTarget");
		sock.sendText(JSONValue.toJSONString(jobj));
		
		selectedUnitPosition = -2;
		selectedUnitSide = -1;
		
		try {
			while(selectedUnitPosition == -2) {
				Thread.sleep(100);
			}
		} catch (InterruptedException e) {
		}
		
		return latestSituation.unitForPlayer(selectedUnitPosition, selectedUnitSide);
	}

	@Override
	public VisualSystemInterface visual() {
		return this;
	}

}
