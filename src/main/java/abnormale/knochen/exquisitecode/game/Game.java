package abnormale.knochen.exquisitecode.game;

import abnormale.knochen.exquisitecode.interp.Interpreter;

public class Game {
    private Interpreter interpreter;
    private Task task;
    private String code;
    private String result;
    private int maxLineLength;

    public Game(Interpreter interpreter, Task task) {
        this.interpreter = interpreter;
        this.code = "var result = '';\n";
        this.task = task;
        this.maxLineLength = 150;
    }

    public void addLine(String line) throws Exception {
        if (line.length() > maxLineLength) {
            throw new Exception("line length of " + maxLineLength + " exceeded");
        }
        String ccode = code + interpreter.fixLine(line) + '\n';
        result = interpreter.execute(ccode);
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
