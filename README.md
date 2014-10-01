Server part for my cards game. 

Designed to be used as servelet on Apache Tomcat server.

For my game repo see github.com/abar193/AbarCards

Things done so far: 

* Servelet with deck information about presented decks (just to see, if everything is ok)

* Socket class, capable of initialising and player single-payer game over internet with easy bot. 

All client-server communication is done by sending JSON strings. https://code.google.com/p/json-simple/ libarary is used. 
For each client request server should return response json object, with "response" key containing original client's request 
(like "playCard", "commitAttack", etc.), and "status" key containing one of 
Game.network.ServerResponses strings. 

Server input side already fully implements Game.src.GameInterface plus it may validate player's deck. 

To validate a deck, each of it's cards should be put in JSONArray as Map object by invoking Game.CardJSONOperations.mapFromCard(BasicCard) method.
If user wants to play against SimpleBot, then JSON string should also contain pair of key "opponent" and value "Terran".

To use some of GameInterface methods client should send requests with encoded key "action" containing name of actual method from GameInterface and a list of it's parameters (as keys) with an appropriate values (each integer should be send as String value). 

* For methods attackIsValid, canPlayCard server may return Game.network.ServerResponses.ResponseTrue/ResponseFalse/ResponseIllegal
* For methods playCard, commitAttack(int, int, int) return values are either ResponseIllegal or ResponseOk.

To respond for server's "selectTarget" request client should send JSON object with pair "return": "selectTarget", and two integer values for keys "side" and 
"position" showing selected unit's side and position. For this input server generates no response other than sending new field situation. 

As output send to client from Game.players.PlayerInterface, and Game.ui.VisualSystemInterface methods so far only reciveInfo, reciveAction, and run() are implemented, while run has no client-side-stop-signal, and lasts 30 seconds. 

In progress: 

* Implement full list of PlayerInterface and VisualSystemInterface methods. 

* Improve game-creation code, allow players to play with each other. 

Plans: 

* Make some kind of lobbies for players

* Make some kind of mathmaking system, so I could play with myself all the day.

Please notice: this server is in it's alpha-stages, and has almost nothing implemented. It's launched under Tomcat as localhost on my notebook, and it's impossible for anyone else but me to acess it. If you want to test it, you will have to configure server and launch it by yourself.