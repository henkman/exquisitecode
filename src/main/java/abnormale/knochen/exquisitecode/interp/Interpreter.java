package abnormale.knochen.exquisitecode.interp;


import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.StringWriter;

public class Interpreter {
    private ScriptEngine engine;
    private Validator validator;

    public Interpreter(ScriptEngine engine, Validator validator) {
        this.engine = engine;
        this.validator = validator;
    }

    public String execute(String code) throws ScriptException {
        ScriptContext ctx = engine.getContext();
        StringWriter out = new StringWriter();
        ctx.setWriter(out);
        StringWriter err = new StringWriter();
        ctx.setErrorWriter(err);
        engine.setBindings(engine.createBindings(), ScriptContext.ENGINE_SCOPE);
        engine.eval(code);
        return String.valueOf(engine.get("result"));
    }

    public String fixLine(String line) {
        return validator.fixLine(line);
    }
}
