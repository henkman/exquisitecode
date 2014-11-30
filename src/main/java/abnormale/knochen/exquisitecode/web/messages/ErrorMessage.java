package abnormale.knochen.exquisitecode.web.messages;

public class ErrorMessage extends Message {
    private String error;

    public ErrorMessage() {
        this.type = Type.ERROR;
    }

    public ErrorMessage(String error) {
        this.type = Type.ERROR;
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
