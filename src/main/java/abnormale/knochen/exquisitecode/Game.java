package abnormale.knochen.exquisitecode;

import abnormale.knochen.exquisitecode.interp.Interpreter;

public class Game {
    private Interpreter interpreter;
    private Task task;
    private String code;
    private String result;

    public Game(Interpreter interpreter, Task task) {
        this.interpreter = interpreter;
        this.code = "var result = '';\n";
        this.task = task;
    }

    public void addLine(String line) throws Exception {
        String ccode = code + interpreter.fixLine(line) + '\n';
        result = interpreter.execute(ccode);
        code = ccode;
    }

    public boolean isSolved() {
        return task.isSolution(result);
    }
}
