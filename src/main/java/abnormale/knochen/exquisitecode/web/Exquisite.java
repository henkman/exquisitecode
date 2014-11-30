package abnormale.knochen.exquisitecode.web;

import abnormale.knochen.exquisitecode.game.Player;
import abnormale.knochen.exquisitecode.game.Task;
import abnormale.knochen.exquisitecode.interp.Interpreter;
import abnormale.knochen.exquisitecode.interp.InterpreterManager;
import spark.ModelAndView;
import spark.TemplateEngine;
import spark.servlet.SparkApplication;
import spark.template.velocity.VelocityTemplateEngine;

import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class Exquisite implements SparkApplication {

    private WebSocketGame game;

    public Exquisite() throws Exception {
        Task task = new Task("name", "description", "solution");
        Interpreter interp = InterpreterManager.getInterpreter("JavaScript");
        InetSocketAddress serverAddress = new InetSocketAddress(4568);
        Player master = new Player(0, "Gurke");
        game = new WebSocketGame(serverAddress, interp, task, master);
    }

    @Override
    public void init() {
        try {
            Db.init();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        // TODO: add html escaping tool
        TemplateEngine templateEngine = new VelocityTemplateEngine();

        staticFileLocation("/res");
        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("game", game);
            return new ModelAndView(model, "tmpl/index.html");
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
