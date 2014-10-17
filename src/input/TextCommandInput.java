package input;

/**
 * Most non-game related socket messages should be decoded as this message.
 * So far it's only "cancelSearchGame" request.
 * @author Abar
 *
 */
public class TextCommandInput implements SocketInput {

    private String command;
    
    public TextCommandInput(String command) {
        this.command = command;
    }

    @Override
    public String getCommand() {
        return command;
    }

}
