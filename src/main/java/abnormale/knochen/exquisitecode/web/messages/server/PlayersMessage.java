package abnormale.knochen.exquisitecode.web.messages.server;

import abnormale.knochen.exquisitecode.game.Player;
import abnormale.knochen.exquisitecode.web.messages.Message;

import java.util.List;

public class PlayersMessage extends Message {

    protected List<Player> players;

    public PlayersMessage(List<Player> players) {
        this.type = Type.PLAYERS;
        this.players = players;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }
}
