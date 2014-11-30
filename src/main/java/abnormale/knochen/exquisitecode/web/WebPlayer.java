package abnormale.knochen.exquisitecode.web;

import abnormale.knochen.exquisitecode.game.Player;

public class WebPlayer extends Player {
    private Token token;

    public WebPlayer(int id, String name) {
        super(id, name);
        this.token = token;
    }

    public Token getToken() {
        return token;
    }
}
