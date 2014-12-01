package abnormale.knochen.exquisitecode.web;

import abnormale.knochen.exquisitecode.game.Player;
import abnormale.knochen.exquisitecode.web.dao.Db;
import abnormale.knochen.exquisitecode.web.dao.PlayerDao;
import spark.ModelAndView;
import spark.Session;
import spark.TemplateEngine;
import spark.servlet.SparkApplication;
import spark.template.velocity.VelocityTemplateEngine;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class Exquisite implements SparkApplication {

    private TokenRegister tokenRegister;

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
        tokenRegister = new TokenRegister();

        // TODO: add html escaping tool
        TemplateEngine templateEngine = new VelocityTemplateEngine();

        staticFileLocation("/res");

        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            return new ModelAndView(model, "tmpl/index.html");
        }, templateEngine);

        post("/login", (req, res) -> {
            String name = req.queryParams("name");
            String password = req.queryParams("password");
            if (name == null || password == null) {
                halt(401);
            }
            Player player = PlayerDao.getByNameAndPassword(name, password);
            if (player == null) {
                halt(401);
            }
            Session session = req.session(true);
            session.attribute("token", tokenRegister.create(player));
            res.redirect("/");
            return "";
        });

        post("/logout", (req, res) -> {
            Session session = req.session(false);
            if (session != null) {
                Token token = session.attribute("token");
                if (token != null) {
                    tokenRegister.destroy(token);
                }
                session.invalidate();
            }
            res.redirect("/");
            return "";
        });

        post("/game", (req, res) -> {
            // TODO: check if logged in
            // TODO: check if a game id was sent, if so get game and display, otherwise create new game
            /*
            Task task = new Task("name", "description", "solution");
            Interpreter interp;
            try {
                interp = InterpreterManager.getInterpreter("JavaScript");
            } catch (InterpreterException e) {
                e.printStackTrace();
                return;
            }
            InetSocketAddress serverAddress = new InetSocketAddress(4568);
            Token t = PlayerRegister.login("", "");
            Player master = PlayerRegister.get(null);
            game = new WebSocketGame(serverAddress, interp, task, master);
            try {
                game.start();
            } catch (GameException e) {
                e.printStackTrace();
                return;
            }
            */
            Map<String, Object> model = new HashMap<>();
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
