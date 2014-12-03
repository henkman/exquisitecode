package abnormale.knochen.exquisitecode.web;

import abnormale.knochen.exquisitecode.game.Game;
import abnormale.knochen.exquisitecode.game.GameException;
import abnormale.knochen.exquisitecode.game.Player;
import abnormale.knochen.exquisitecode.game.Task;
import abnormale.knochen.exquisitecode.interp.Interpreter;
import abnormale.knochen.exquisitecode.web.messages.AddLineMessage;
import abnormale.knochen.exquisitecode.web.messages.ErrorMessage;
import abnormale.knochen.exquisitecode.web.messages.LoginMessage;
import abnormale.knochen.exquisitecode.web.messages.Message;
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
            System.out.println("ws:" + webSocket.getRemoteSocketAddress());

            if (!isWaitingForPlayers()) {
                webSocket.close();
                return;
            }
        }

        @Override
        public void onClose(WebSocket webSocket, int i, String s, boolean b) {
            System.out.println("ws:" + s);

            // TODO: find out who disconnected and remove them from the game
            // removePlayer(player);
        }

        @Override
        public void onMessage(WebSocket webSocket, String s) {
            System.out.println("ws:" + s);

            Message message = Json.unmarshal(s, Message.class);
            Token token = message.getToken();
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
                if (!isWaitingForPlayers() || isMember(player)) {
                    return;
                }
                addPlayer(player);
                // TODO: let other players know that a new player joined
            } else if (message instanceof AddLineMessage) {
                if (!isCurrent(player)) {
                    webSocket.send("{'error':'not current player'}");
                    return;
                }
                AddLineMessage addLineMessage = (AddLineMessage) message;
                try {
                    addLine(addLineMessage.getLine());
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
                // TODO: send new code and result to all players
            }
        }

        @Override
        public void onError(WebSocket webSocket, Exception e) {
            // TODO: handle this
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
