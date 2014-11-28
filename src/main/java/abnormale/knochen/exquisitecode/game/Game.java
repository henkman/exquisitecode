package abnormale.knochen.exquisitecode.game;

import abnormale.knochen.exquisitecode.interp.Interpreter;

import javax.script.ScriptException;

public class Game {
    private Interpreter interpreter;
    private Task task;
    private String code;
    private String result;
    private int maxLineLength;
    private int maxExecutionTimeMs;

    public Game(Interpreter interpreter, Task task) {
        this.interpreter = interpreter;
        this.code = "var result = '';\n";
        this.task = task;
        this.maxLineLength = 150;
        this.maxExecutionTimeMs = 5000;
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
}
