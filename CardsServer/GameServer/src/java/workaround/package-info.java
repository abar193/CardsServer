/**
 * Ugly workaround for the injecting EJB in websocket problem. 
 * Problem described here: http://stackoverflow.com/questions/20872300/java-ee-7-how-to-inject-an-ejb-into-a-websocket-serverendpoint
 * In short: I was unable to inject my EJB in websocket CardsSocket class, because it was EJB from
 * other project. So, I had to create this stateless WorkatoundBean in CardsSocket witch calls 
 * GameFactory singleton from Server-ejb. Please, tell me more how Java is awesome.
 */
package workaround;
