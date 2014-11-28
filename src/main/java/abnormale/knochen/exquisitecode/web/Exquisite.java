package abnormale.knochen.exquisitecode.web;

import abnormale.knochen.exquisitecode.game.Game;
import abnormale.knochen.exquisitecode.game.Task;
import abnormale.knochen.exquisitecode.interp.Interpreter;
import abnormale.knochen.exquisitecode.interp.InterpreterManager;
import spark.ModelAndView;
import spark.TemplateEngine;
import spark.servlet.SparkApplication;
import spark.template.velocity.VelocityTemplateEngine;

import javax.script.ScriptException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class Exquisite implements SparkApplication {

    private Game game;

    public Exquisite() throws Exception {
        Task task = new Task("name", "description", "solution");
        Interpreter interp = InterpreterManager.getInterpreter("JavaScript");
        InetSocketAddress serverAddress = new InetSocketAddress(4568);
        game = new Game(serverAddress, interp, task);
    }

    @Override
    public void init() {
        // TODO: add html escaping tool
        TemplateEngine templateEngine = new VelocityTemplateEngine();

        staticFileLocation("/res");
        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("game", game);
            return new ModelAndView(model, "tmpl/index.tmpl");
        }, templateEngine);


        post("/addline", (req, res) -> {
            // TODO: this should be in a websocket (https://github.com/TooTallNate/Java-WebSocket)

            String line = req.queryParams("line");
            if (line == null) {
                halt("line is null");
            }
            Map<String, Object> model = new HashMap<>();
            model.put("game", game);
            try {
                game.addLine(line);
            } catch (ScriptException e) {
                model.put("error", e.getMessage());
            } catch(InterruptedException e) {
                model.put("error", "code took too long to run");
            } catch(Exception e) {
                model.put("error", e.getMessage());
            }
            return new ModelAndView(model, "tmpl/index.tmpl");
        }, templateEngine);

        exception(Exception.class, (e, request, response) -> {
            // TODO: handle this more carefully
            response.body("ups: " + e.getMessage());
        });
    }

    public static void main(String[] args) throws Exception {
        new Exquisite().init();
    }
}
