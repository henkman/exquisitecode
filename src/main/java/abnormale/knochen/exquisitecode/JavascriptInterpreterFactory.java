package abnormale.knochen.exquisitecode;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class JavascriptInterpreterFactory implements InterpreterFactory<JavascriptInterpreter> {
	private ScriptEngineManager scriptFactory;
	private ScriptEngine scriptEngine;

	@Override
	public JavascriptInterpreter getInterpreter() {
		scriptFactory = new ScriptEngineManager();
		scriptEngine = scriptFactory.getEngineByName("JavaScript");
		return new JavascriptInterpreter(scriptEngine);
	}
}
