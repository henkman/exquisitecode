package abnormale.knochen.exquisitecode.interp;


import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.HashMap;
import java.util.Map;

public class InterpreterManager {
    private static ScriptEngineManager scriptFactory = new ScriptEngineManager();

    private static final Map<String, Validator> validators;

    static {
        validators = new HashMap<String, Validator>();
        validators.put("JavaScript", new JavaScriptValidator());
    }

    public static Interpreter getInterpreter(String name) throws Exception {
        ScriptEngine engine = scriptFactory.getEngineByName(name);
        if (engine == null) {
            throw new Exception("scriptengine " + name + " does not exist");
        }
        Validator val = validators.get(name);
        if (val == null) {
            throw new Exception("validator " + name + " does not exist");
        }
        return new Interpreter(engine, val);
    }
}
