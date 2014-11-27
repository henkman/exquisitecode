package abnormale.knochen.exquisitecode;

import abnormale.knochen.exquisitecode.interp.Interpreter;

public class Game {
	private Interpreter interpreter;
	private StringBuilder code;
	private Task task;

	public Game(Interpreter interpreter, Task task) {
		this.interpreter = interpreter;
		this.code = new StringBuilder();
		this.task = task;
	}

	public void checkLineValid(String line) {
		// TODO: strip line from comments
		// TODO: disallow lines with too long variable and function names
	}

	public void addLine(String line) {
		code.append(line);
		code.append('\n');
	}

	public boolean isSolved() {
		return task.getSolution() == "";
	}
}
