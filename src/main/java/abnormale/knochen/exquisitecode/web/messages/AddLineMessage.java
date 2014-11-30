package abnormale.knochen.exquisitecode.web.messages;

public class AddLineMessage extends Message {
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
