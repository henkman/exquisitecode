package abnormale.knochen.exquisitecode.game;

import abnormale.knochen.exquisitecode.interp.Interpreter;

import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game {
    private Interpreter interpreter;
    private Task task;
    private String code;
    private String result;
    private int maxLineLength;
    private int maxExecutionTimeMs;
    private List<Player> players;
    private Player master;
    private Player current;
    private State state;

    enum State {
        WAITING_FOR_PLAYERS,
        RUNNING,
        OVER
    }

    public Game(Interpreter interpreter, Task task, Player master) {
        this.interpreter = interpreter;
        this.code = "var result = '';\n";
        this.task = task;
        this.maxLineLength = 150;
        this.maxExecutionTimeMs = 5000;
        this.players = new ArrayList<Player>();
        this.master = master;
        this.current = null;
        this.players.add(master);
        this.state = State.WAITING_FOR_PLAYERS;
    }

    /**
     * Use this when the $master decides to start the game
     */
    public void start() throws GameException {
        if (state != State.WAITING_FOR_PLAYERS) {
            return;
        }
        if (players.size() < 2) {
            throw new GameException("not enough players, need at least 2");
        }
        Collections.shuffle(players);
        current = players.get(0);
        state = State.RUNNING;
    }

    protected void stop() {
        state = State.OVER;
    }

    /**
     * Use this when a Player joins the game when waiting for players
     */
    public void addPlayer(Player p) {
        if (state != State.WAITING_FOR_PLAYERS || players.contains(p)) {
            return;
        }
        players.add(p);
    }

    /**
     * Use this when a Player exits the game while it's running
     * or is kicked by the $master while waiting for players
     */
    public void removePlayer(Player p) {
        if (!(state == State.RUNNING || state == State.WAITING_FOR_PLAYERS)) {
            return;
        }
        int ns = players.size() - 1;
        if (ns < 2) {
            stop();
        } else if (isCurrent(p)) {
            nextPlayer();
        } else if (isMaster(p)) {
            // TODO: maybe promote somebody else to master
            stop();
        }
        players.remove(p);
    }

    private void nextPlayer() {
        int o = players.indexOf(current);
        int no = o + 1;
        if (no == players.size()) {
            no = 0;
        }
        current = players.get(no);
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
        if (se.ex != null) {
            throw se.ex;
        }
        return se.result;
    }

    private void checkAddLine(String line) throws ScriptException, InterruptedException, GameException {
        if (line.length() > maxLineLength) {
            throw new GameException("line length of " + maxLineLength + " exceeded");
        }
        String ccode = code + interpreter.fixLine(line) + '\n';
        result = execute(ccode);
        code = ccode;
    }

    /**
     * Use this when the $current player decides to add a line.
     * Returns true if the game was solved
     */
    public boolean addLine(String line) throws ScriptException, InterruptedException, GameException {
        if (state != State.RUNNING) {
            return false;
        }
        checkAddLine(line);
        if (isSolved()) {
            stop();
            return true;
        }
        nextPlayer();
        return false;
    }

    public boolean isSolved() {
        return task.isSolution(result);
    }

    public boolean isMaster(Player player) {
        return master != null && player.getId() == master.getId();
    }

    public boolean isCurrent(Player player) {
        return current != null && player.getId() == current.getId();
    }

    public boolean isWaitingForPlayers() {
        return state == State.WAITING_FOR_PLAYERS;
    }

    public boolean isRunning() {
        return state == State.RUNNING;
    }

    public boolean isOver() {
        return state == State.OVER;
    }

    public boolean isMember(Player player) {
        for (Player p : players) {
            if (p.getId() == player.getId()) {
                return true;
            }
        }
        return false;
    }

    public Player getCurrent() {
        return current;
    }

    public Player getMaster() {
        return master;
    }

    public List<Player> getPlayers() {
        return players;
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
}
