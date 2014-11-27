package abnormale.knochen.exquisitecode;


import java.util.HashMap;

public class LanguageManager {
	protected static final HashMap<String, InterpreterFactory> languages;
	static {
		languages = new HashMap<String, InterpreterFactory>();
		languages.put(JavascriptInterpreter.name, new JavascriptInterpreterFactory());
	}

	public static Interpreter getInterpreter(String name) {
		InterpreterFactory<Interpreter> ifa = languages.get(name);
		if(ifa == null) {
			return null;
		}
		return ifa.getInterpreter();
	}
}
