package abnormale.knochen.exquisitecode.web;

import abnormale.knochen.exquisitecode.game.Player;

import java.util.HashMap;
import java.util.Map;

public class TokenRegister implements Runnable {
    private final int secondsFresh;
    private boolean running;
    private Map<Token, Player> tokenToPlayer;

    public TokenRegister(int secondsFresh) {
        this.tokenToPlayer = new HashMap<>();
        this.secondsFresh = secondsFresh;
    }

    public Token create(Player player) {
        Token token = new Token(player.getId(), secondsFresh);
        tokenToPlayer.put(token, player);
        return token;
    }

    public Player get(Token token) {
        token.freshUntilSeconds(secondsFresh);
        return tokenToPlayer.get(token);
    }

    public void destroy(Token token) {
        tokenToPlayer.remove(token);
    }

    @Override
    public void run() {
        running = true;
        while (running) {
            for (Token token : tokenToPlayer.keySet()) {
                if (!token.isFresh()) {
                    System.out.println("destroying token " + token.toString());
                    destroy(token);
                }
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void start() {
        if (running) {
            return;
        }
        Thread th = new Thread(this);
        th.start();
    }

    public synchronized void stop() {
        running = false;
    }
}
