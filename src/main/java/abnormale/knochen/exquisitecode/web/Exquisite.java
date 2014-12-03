package abnormale.knochen.exquisitecode.web;

import abnormale.knochen.exquisitecode.game.Game;
import abnormale.knochen.exquisitecode.game.Player;
import abnormale.knochen.exquisitecode.game.Task;
import abnormale.knochen.exquisitecode.interp.Interpreter;
import abnormale.knochen.exquisitecode.interp.InterpreterException;
import abnormale.knochen.exquisitecode.interp.InterpreterManager;
import abnormale.knochen.exquisitecode.web.dao.Db;
import abnormale.knochen.exquisitecode.web.dao.PlayerDao;
import abnormale.knochen.exquisitecode.web.dao.TaskDao;
import spark.ModelAndView;
import spark.Session;
import spark.TemplateEngine;
import spark.servlet.SparkApplication;
import spark.template.velocity.VelocityTemplateEngine;

import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class Exquisite implements SparkApplication {

    private static final int SESSION_SECONDS_FRESH = 60 * 30;
    private TokenRegister tokenRegister;
    private List<WebSocketGame> games;
    private int gameIdCounter;

    public Exquisite() {
        this.gameIdCounter = 0;
        this.games = new ArrayList<>();
    }

    private WebSocketGame getGameById(int id) {
        for (WebSocketGame game : games) {
            if (game.getId() == id) {
                return game;
            }
        }
        return null;
    }

    private WebSocketGame createGame(Player master) throws InterpreterException {
        Interpreter interp = InterpreterManager.getInterpreter("JavaScript");
        Task task = TaskDao.getRandom();
        // TODO: get unique game id
        int id = gameIdCounter++;
        WebSocketGame game = new WebSocketGame(id, interp, task, master, new InetSocketAddress(0), tokenRegister);
        games.add(game);
        return game;
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
        tokenRegister = new TokenRegister(SESSION_SECONDS_FRESH);
        tokenRegister.start();

        // TODO: add html escaping tool
        TemplateEngine templateEngine = new VelocityTemplateEngine();

        staticFileLocation("/res");

        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("games", games);
            return new ModelAndView(model, "tmpl/index.html");
        }, templateEngine);

        get("/game/:id", (req, res) -> {
            Session sess = req.session(false);
            if (sess == null) {
                halt(401);
            }
            Token token = sess.attribute("token");
            if (token == null) {
                halt(401);
            }
            Player player = tokenRegister.get(token);
            if (player == null) {
                halt(401);
            }
            String sid = req.params(":id");
            int id = -1;
            try {
                id = Integer.parseInt(sid);
            } catch (NumberFormatException e) {
                halt(404);
            }
            Game game = getGameById(id);
            if (game == null) {
                halt(404);
            }
            Map<String, Object> model = new HashMap<>();
            model.put("game", game);
            return new ModelAndView(model, "tmpl/game.html");
        }, templateEngine);

        post("/login", (req, res) -> {
            String name = req.queryParams("name");
            String password = req.queryParams("password");
            if (name == null || password == null) {
                return "{'error':'parameter missing'}";
            }
            Player player = PlayerDao.getByNameAndPassword(name, password);
            if (player == null) {
                return "{'error':'bad credentials'}";
            }
            Session session = req.session(true);
            session.maxInactiveInterval(SESSION_SECONDS_FRESH);
            Token token = tokenRegister.create(player);
            token.freshUntilSeconds(SESSION_SECONDS_FRESH);
            session.attribute("token", token);
            return String.format("{'token':'%s'}", token.toString());
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
            return "";
        });

        post("/create", (req, res) -> {
            Session sess = req.session(false);
            if (sess == null) {
                halt(401, "not logged in");
            }
            Token token = sess.attribute("token");
            if (token == null) {
                halt(401, "not logged in");
            }
            Player player = tokenRegister.get(token);
            if (player == null) {
                halt(401, "not logged in");
            }
            // TODO: handle this better
            try {
                WebSocketGame game = createGame(player);
                return String.format("{'game':'%d'}", game.getId());
            } catch (InterpreterException e) {
                e.printStackTrace();
                return "{'error':'internal error'}";
            }
        });

        exception(Exception.class, (e, request, response) -> {
            // TODO: handle this more carefully
            response.body("ups: " + e.getMessage());
        });
    }

    public static void main(String[] args) throws Exception {
        new Exquisite().init();
    }
}
