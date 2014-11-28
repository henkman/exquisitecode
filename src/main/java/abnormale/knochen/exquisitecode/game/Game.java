package abnormale.knochen.exquisitecode.game;

import abnormale.knochen.exquisitecode.interp.Interpreter;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import javax.script.ScriptException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collection;

public class Game extends WebSocketServer {
    private Interpreter interpreter;
    private Task task;
    private String code;
    private String result;
    private int maxLineLength;
    private int maxExecutionTimeMs;

    public Game(InetSocketAddress address, Interpreter interpreter, Task task) throws UnknownHostException {
        super(address);
        this.interpreter = interpreter;
        this.code = "var result = '';\n";
        this.task = task;
        this.maxLineLength = 150;
        this.maxExecutionTimeMs = 5000;
    }

    @Override
    public void onOpen(org.java_websocket.WebSocket webSocket, ClientHandshake clientHandshake) {

    }

    @Override
    public void onClose(org.java_websocket.WebSocket webSocket, int i, String s, boolean b) {

    }

    @Override
    public void onMessage(org.java_websocket.WebSocket webSocket, String s) {
        try {
            addLine(s);
            //TODO: send message with following information:    code, result, error, isSolved
        } catch (Exception e) {
            //TODO: send error message
        }
    }

    @Override
    public void onError(org.java_websocket.WebSocket webSocket, Exception e) {

    }

    private class ScriptExecutor implements Runnable {
        String result;
        ScriptException ex;
        String code;
        public ScriptExecutor(String code) {
            this.code = code;
        }
        @Override
        public void run() {
            try {
                result = interpreter.execute(code);
            } catch (ScriptException e) {
                ex = e;
            }
        }
    }

    private String execute(String code) throws ScriptException, InterruptedException {
        ScriptExecutor se = new ScriptExecutor(code);
        Thread th = new Thread(se);
        th.start();
        th.join(maxExecutionTimeMs);
        if(se.ex != null) {
            throw se.ex;
        }
        return se.result;
    }

    public void addLine(String line) throws Exception {
        if (line.length() > maxLineLength) {
            throw new Exception("line length of " + maxLineLength + " exceeded");
        }
        String ccode = code + interpreter.fixLine(line) + '\n';
        result = execute(ccode);
        code = ccode;
    }

    public boolean isSolved() {
        return task.isSolution(result);
    }

    public String getResult() {
        return result;
    }

    public String getCode() {
        return code;
    }

    public Task getTask() {
        return task;
    }

    public void sendToAll(String message) {
        Collection<WebSocket> currentConnections = connections();
        synchronized (currentConnections) {
            for (org.java_websocket.WebSocket socket : currentConnections) {
                socket.send(message);
            }
        }
    }
}
