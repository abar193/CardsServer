package input;

public class ResponseInput implements SocketInput {

    private String target;
    
    private int side, position;
    
    public ResponseInput(String target, int side, int position) {
        this.target = target;
        this.side = side;
        this.position = position;
    }

    public String getTarget() {
        return target;
    }
    
    public int getSide() {
        return side;
    }
    
    public int getPosition() {
        return position;
    }
    
    @Override
    public String getCommand() {
        return "return";
    }

}
