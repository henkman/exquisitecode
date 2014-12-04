package abnormale.knochen.exquisitecode.web.messages.client;

public class AddLineMessage extends ClientMessage {
    private String line;

    public AddLineMessage() {
        this.type = Type.ADDLINE;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }
}
