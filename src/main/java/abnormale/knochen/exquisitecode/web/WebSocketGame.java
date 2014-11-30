package abnormale.knochen.exquisitecode.web;

import abnormale.knochen.exquisitecode.game.Game;
import abnormale.knochen.exquisitecode.game.GameException;
import abnormale.knochen.exquisitecode.game.Player;
import abnormale.knochen.exquisitecode.game.Task;
import abnormale.knochen.exquisitecode.interp.Interpreter;
import abnormale.knochen.exquisitecode.web.messages.AddLineMessage;
import abnormale.knochen.exquisitecode.web.messages.ErrorMessage;
import abnormale.knochen.exquisitecode.web.messages.Message;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import javax.script.ScriptException;
import java.io.IOException;
import java.net.InetSocketAddress;

public class WebSocketGame extends Game {
    private WebSocketServer sockServ;

    class GameWebSocketServer extends WebSocketServer {
        public GameWebSocketServer(InetSocketAddress address) {
            super(address);
        }

        @Override
        public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
            if (!WebSocketGame.this.isWaitingForPlayers()) {
                webSocket.close();
                return;
            }
            /*
            Player p = PlayerDao.get(webSocket.getRemoteSocketAddress());
            if (p == null) {
                webSocket.close();
                return;
            }
            WebSocketGame.this.addPlayer(p);
            */
        }

        @Override
        public void onClose(WebSocket webSocket, int i, String s, boolean b) {
            /*
            Player p = PlayerDao.get(webSocket.getRemoteSocketAddress());
            if (p == null) {
                webSocket.close();
                return;
            }
            WebSocketGame.this.removePlayer(p);
            */
        }

        @Override
        public void onMessage(WebSocket webSocket, String s) {
            Message message = Json.unmarshal(s, Message.class);
            if (message instanceof AddLineMessage) {
                AddLineMessage addLineMessage = (AddLineMessage) message;
                try {
                    WebSocketGame.this.addLine(addLineMessage.getLine());
                } catch (ScriptException e) {
                    webSocket.send(Json.marshal(new ErrorMessage(e.getMessage())));
                } catch (InterruptedException e) {
                    webSocket.send(Json.marshal(new ErrorMessage("Script took too long to run")));
                } catch (GameException e) {
                    webSocket.send(Json.marshal(new ErrorMessage(e.getMessage())));
                }
            }
        }

        @Override
        public void onError(WebSocket webSocket, Exception e) {
            // TODO: handle this
        }
    }

    public WebSocketGame(InetSocketAddress address, Interpreter interpreter, Task task, Player master) {
        super(interpreter, task, master);
        this.sockServ = new GameWebSocketServer(address);
    }

    public void start() throws GameException {
        super.start();
        sockServ.start();
    }

    public void stop() {
        super.stop();
        try {
            sockServ.stop();
        } catch (IOException e) {
            // TODO: handle errors
        } catch (InterruptedException e) {
            // TODO: handle errors
        }
    }
}
