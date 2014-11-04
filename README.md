Server part of my card game. 

After some time of working with TomEE server I decided to move to glassfish4. This new version is organised in as it should be from Java EE 
standarts (or at least I'm trying to follow guidelines). All EJB components are moved to the separate project, as well as JSP server pages.

The most interesting part is CardsSocket, and corresponding classes located in GameServer project. They enable online games with a client-server
protocol. 

I'm going to define cient-server protocol some day somewhere, but for now there are only short highlights over it.
***
All client-server communications are done by sending JSON strings. https://code.google.com/p/json-simple/ libarary is used. 
For each client request server should return response json object, with "response" key containing original client's request 
(like "playCard", "commitAttack", etc.), and "status" key containing one of Game.network.ServerResponses strings. 

Server input side already fully implements Game.src.GameInterface plus it may validate player's deck. 

To validate a deck, each of it's cards should be put in the JSONArray as Map object by invoking Game.CardJSONOperations.mapFromCard(BasicCard) method.
If user wants to play against SimpleBot, then JSON string should also contain pair of key "opponent" and value "Terran". 

To use some of GameInterface methods client should send requests with encoded key "action" containing name of actual method from GameInterface and a list of it's parameters (as keys) with an appropriate value (each integer should be send as String value). 

* For methods attackIsValid, canPlayCard server may return Game.network.ServerResponses.ResponseTrue/ResponseFalse/ResponseIllegal
* For methods playCard, commitAttack(int, int, int) return values are either ResponseIllegal or ResponseOk.

To respond for server's "selectTarget" request client should send the JSON object with a pair "return": "selectTarget", and two integer values for keys "side" and 
"position" showing selected unit's side and position. For this input server generates no response other than sending new field situation. 

***
In progress: 

* Implement full list of PlayerInterface and VisualSystemInterface methods. 

* Simple mathmaking - players may play with each other, but there are no MMR or leagues system, players are matched in the same way they 
entered the queue - e. g.: first with second, third with fourd, e.t.c..

Plans: 

* Make some kind of lobbies for players

* Make observer mode

* Improve web pages

* Deploy server somehere 

Please notice: this server is in it's alpha-stages, and has almost nothing implemented. It's launched under Tomcat as localhost on my notebook, and it's impossible for anyone else but me to acess it. If you want to test it, you will have to configure server and launch it by yourself.