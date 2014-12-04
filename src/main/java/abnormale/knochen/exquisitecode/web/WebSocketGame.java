package abnormale.knochen.exquisitecode.web;

import abnormale.knochen.exquisitecode.game.Game;
import abnormale.knochen.exquisitecode.game.GameException;
import abnormale.knochen.exquisitecode.game.Player;
import abnormale.knochen.exquisitecode.game.Task;
import abnormale.knochen.exquisitecode.interp.Interpreter;
import abnormale.knochen.exquisitecode.web.messages.client.AddLineMessage;
import abnormale.knochen.exquisitecode.web.messages.client.ClientMessage;
import abnormale.knochen.exquisitecode.web.messages.client.ErrorMessage;
import abnormale.knochen.exquisitecode.web.messages.client.LoginMessage;
import abnormale.knochen.exquisitecode.web.messages.server.CodeAndResultMessage;
import abnormale.knochen.exquisitecode.web.messages.server.PlayersMessage;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import javax.script.ScriptException;
import java.io.IOException;
import java.net.InetSocketAddress;

public class WebSocketGame extends Game {
    private int id;
    private WebSocketServer sockServ;
    private TokenRegister tokenRegister;

    class GameWebSocketServer extends WebSocketServer {
        public GameWebSocketServer(InetSocketAddress address) {
            super(address);
        }

        @Override
        public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
            // TODO: try to get the token already here out of the session (clientHandshake.getFieldValue("COOKIE") -> JSESSIONID=blah)
            if (!isWaitingForPlayers()) {
                webSocket.close();
                return;
            }
        }

        @Override
        public void onClose(WebSocket webSocket, int i, String s, boolean b) {
            // TODO: find out who disconnected and removePlayer(player);
        }

        @Override
        public void onMessage(WebSocket webSocket, String s) {
            System.out.println("ws:" + s);


            ClientMessage message = Json.unmarshal(s, ClientMessage.class);
            if (message == null) {
                webSocket.send("{'error':'not a valid message'}");
                return;
            }
            Token token = message.parseToken();
            if (token == null) {
                webSocket.send("{'error':'no token sent'}");
                return;
            }
            Player player = tokenRegister.get(token);
            if (player == null) {
                webSocket.send("{'error':'token invalid'}");
                return;
            }
            if (message instanceof LoginMessage) {
                handleLoginMessage(webSocket, (LoginMessage) message, player);
            } else if (message instanceof AddLineMessage) {
                handleAddLineMessage(webSocket, (AddLineMessage) message, player);
            }
        }

        private void handleLoginMessage(WebSocket webSocket, LoginMessage message, Player player) {
            if (!isWaitingForPlayers() || isMember(player)) {
                return;
            }
            addPlayer(player);
            sendToAllPlayers(Json.marshal(new PlayersMessage(getPlayers())));
        }

        private void handleAddLineMessage(WebSocket webSocket, AddLineMessage message, Player player) {
            if (!isCurrent(player)) {
                webSocket.send("{'error':'not current player'}");
                return;
            }
            try {
                addLine(message.getLine());
            } catch (ScriptException e) {
                webSocket.send(Json.marshal(new ErrorMessage(e.getMessage())));
            } catch (InterruptedException e) {
                webSocket.send(Json.marshal(new ErrorMessage("Script took too long to run")));
            } catch (GameException e) {
                webSocket.send(Json.marshal(new ErrorMessage(e.getMessage())));
            }
            if (isSolved()) {
                try {
                    stop();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return;
            }
            sendToAllPlayers(Json.marshal(new CodeAndResultMessage(getCode(), getResult())));
        }

        @Override
        public void onError(WebSocket webSocket, Exception e) {
            // TODO: handle this
        }

        private void sendToAllPlayers(String s) {
            for (WebSocket ws : connections()) {
                ws.send(s);
            }
        }
    }

    public WebSocketGame(int id, Interpreter interpreter, Task task, Player master, InetSocketAddress address, TokenRegister tokenRegister) {
        super(interpreter, task, master);
        this.id = id;
        this.sockServ = new GameWebSocketServer(address);
        this.tokenRegister = tokenRegister;
        sockServ.start();
    }

    public int getPort() {
        return sockServ.getPort();
    }

    public int getId() {
        return id;
    }
}
