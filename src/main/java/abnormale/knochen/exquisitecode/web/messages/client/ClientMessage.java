package abnormale.knochen.exquisitecode.web.messages.client;

import abnormale.knochen.exquisitecode.web.Token;
import abnormale.knochen.exquisitecode.web.messages.Message;

public abstract class ClientMessage extends Message {
    protected String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Token parseToken() {
        return Token.parse(getToken());
    }
}
