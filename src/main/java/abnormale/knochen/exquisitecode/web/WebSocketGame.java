package abnormale.knochen.exquisitecode.web;

import abnormale.knochen.exquisitecode.game.Game;
import abnormale.knochen.exquisitecode.game.GameException;
import abnormale.knochen.exquisitecode.game.Player;
import abnormale.knochen.exquisitecode.game.Task;
import abnormale.knochen.exquisitecode.interp.Interpreter;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class WebSocketGame extends Game {
    private WebSocketServer sockServ;

    public WebSocketGame(InetSocketAddress address, Interpreter interpreter, Task task, Player master) {
        super(interpreter, task, master);
        sockServ = new WebSocketServer(address) {
            @Override
            public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
                // TODO: check if connected person is player in game
            }

            @Override
            public void onClose(WebSocket webSocket, int i, String s, boolean b) {
                // TODO: remove player from game
            }

            @Override
            public void onMessage(WebSocket webSocket, String s) {
                // TODO: find a way to unmarshal $s
                /*
                Message message = Json.unmarshal(s, Message.class);
                if(message instanceof AddLineMessage) {
                    AddLineMessage addLineMessage = (AddLineMessage)message;
                    try {
                        Game.this.addLine(addLineMessage.getLine());
                    } catch (ScriptException e) {
                        // TODO: handle errors
                    } catch (InterruptedException e) {
                        // TODO: handle errors
                    } catch (Exception e) {
                        // TODO: handle errors
                    }
                }
                */
            }

            @Override
            public void onError(WebSocket webSocket, Exception e) {
                // TODO: handle this
            }
        };
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
