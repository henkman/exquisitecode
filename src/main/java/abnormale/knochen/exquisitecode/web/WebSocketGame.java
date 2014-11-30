package abnormale.knochen.exquisitecode.web;

import abnormale.knochen.exquisitecode.game.Game;
import abnormale.knochen.exquisitecode.game.Player;
import abnormale.knochen.exquisitecode.game.Task;
import abnormale.knochen.exquisitecode.interp.Interpreter;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class WebSocketGame extends Game {
    private WebSocketServer sockServ;

    public WebSocketGame(InetSocketAddress address, Interpreter interpreter, Task task, Player master) {
        super(interpreter, task, master);
        sockServ = new WebSocketServer(address) {
            @Override
            public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {

            }

            @Override
            public void onClose(WebSocket webSocket, int i, String s, boolean b) {

            }

            @Override
            public void onMessage(WebSocket webSocket, String s) {

            }

            @Override
            public void onError(WebSocket webSocket, Exception e) {

            }
        };
    }
}
