package abnormale.knochen.exquisitecode.interp;


import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class LanguageManager {
    private static ScriptEngineManager scriptFactory = new ScriptEngineManager();

    public static Interpreter getInterpreter(String name) throws Exception {
        ScriptEngine engine = scriptFactory.getEngineByName(name);
        if (engine == null) {
            throw new Exception("scriptengine " + name + " does not exist");
        }
        return new Interpreter(engine);
    }
}
