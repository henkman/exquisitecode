package abnormale.knochen.exquisitecode.interp;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import java.io.StringWriter;

public class JavascriptInterpreter implements Interpreter {
	public static final String name = "javascript";

	private ScriptEngine engine;

	public JavascriptInterpreter(ScriptEngine engine) {
		this.engine = engine;
	}

	@Override
	public String execute(String code) throws InterpreterException {
		ScriptContext context = new SimpleScriptContext();
		StringWriter result = new StringWriter();
		context.setWriter(result);
		try {
			engine.eval(code, context);
		} catch (ScriptException e) {
			throw new InterpreterException(e);
		}
		return result.toString();
	}
}
