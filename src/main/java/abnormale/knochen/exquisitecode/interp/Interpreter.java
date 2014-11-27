package abnormale.knochen.exquisitecode.interp;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.StringWriter;

public class Interpreter {
    private ScriptEngine engine;

    public Interpreter(ScriptEngine engine) {
        this.engine = engine;
    }

    public String execute(String code) throws ScriptException {
        ScriptContext ctx = engine.getContext();
        StringWriter out = new StringWriter();
        ctx.setWriter(out);
        StringWriter err = new StringWriter();
        ctx.setErrorWriter(err);
        engine.setBindings(engine.createBindings(), ScriptContext.ENGINE_SCOPE);
        engine.eval(code);
        return (String) engine.get("result");
    }
}
