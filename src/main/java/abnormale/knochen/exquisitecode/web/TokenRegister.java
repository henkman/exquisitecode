package abnormale.knochen.exquisitecode.web;

import abnormale.knochen.exquisitecode.game.Player;

import java.util.HashMap;
import java.util.Map;

// TODO: implement mechanism that destroys tokens that were not accessed for a while
public class TokenRegister {
    private Map<Token, Player> tokenToPlayer;

    public TokenRegister() {
        this.tokenToPlayer = new HashMap<>();
    }

    public Token create(Player player) {
        Token token = new Token(player.getId());
        tokenToPlayer.put(token, player);
        return token;
    }

    public Player get(Token token) {
        return tokenToPlayer.get(token);
    }

    public void destroy(Token token) {
        tokenToPlayer.remove(token);
    }
}
