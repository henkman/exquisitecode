package abnormale.knochen.exquisitecode.game;

import abnormale.knochen.exquisitecode.interp.Interpreter;
import abnormale.knochen.exquisitecode.interp.InterpreterException;
import abnormale.knochen.exquisitecode.interp.InterpreterManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.List;

public class TestGame {

    Player player1;
    Player player2;
    Player player3;
    Interpreter interp;
    Task task;

    @Before
    public void init() throws InterpreterException {
        player1 = new Player(0, "Gurke");
        player2 = new Player(1, "Baguette");
        player3 = new Player(2, "Fred");
        interp = InterpreterManager.getInterpreter("JavaScript");
        task = new Task("", "", "foo");
    }

    @Test
    public void testGameLineAddingAndSolving() throws GameException, ScriptException, InterruptedException {
        Game g = new Game(interp, task, player1);
        Assert.assertTrue(g.isWaitingForPlayers());
        g.addPlayer(player2);
        g.start();
        Assert.assertTrue(g.isRunning());
        Assert.assertTrue(g.addLine("result = 'foo';"));
        Assert.assertTrue(g.isSolved());
    }

    @Test
    public void testGameLineAddingAndSolvingFailing() throws GameException, ScriptException, InterruptedException {
        Game g = new Game(interp, task, player1);
        Assert.assertTrue(g.isWaitingForPlayers());
        g.addPlayer(player2);
        g.start();
        Assert.assertTrue(g.isRunning());
        Assert.assertFalse(g.addLine("result = '';"));
        Assert.assertFalse(g.isSolved());
    }

    @Test
    public void testGameLineAddingAndSolvingParserError() throws GameException {
        Game g = new Game(interp, task, player1);
        Assert.assertTrue(g.isWaitingForPlayers());
        g.addPlayer(player2);
        g.start();
        Assert.assertTrue(g.isRunning());
        try {
            Assert.assertFalse(g.addLine("result = ;"));
            Assert.fail("script should throw parse error");
        } catch (ScriptException e) {
            Assert.assertTrue(e.getMessage().contains("Expected an operand but found ;"));
        } catch (Exception e) {
            Assert.fail("there should be a ScriptException, nothing else");
        }
    }

    @Test
    public void testGamePlayerSequence() throws GameException {
        Game g = new Game(interp, task, player1);
        Assert.assertTrue(g.isMaster(player1));
        Assert.assertTrue(g.isWaitingForPlayers());
        g.addPlayer(player2);
        g.addPlayer(player3);
        Assert.assertFalse(g.isCurrent(player1));
        Assert.assertFalse(g.isCurrent(player2));
        Assert.assertFalse(g.isCurrent(player3));
        g.start();
        Assert.assertTrue(g.isRunning());
        Assert.assertTrue(g.isCurrent(player1) || g.isCurrent(player2) || g.isCurrent(player3));
        List<Player> hasPlayed = new ArrayList<Player>(g.getPlayers().size());
        try {
            {
                Player current = g.getCurrent();
                Assert.assertFalse(g.addLine("result = '';"));
                Assert.assertFalse(g.isCurrent(current));
                hasPlayed.add(current);
            }
            {
                Player current = g.getCurrent();
                Assert.assertFalse(g.addLine("result = '';"));
                Assert.assertFalse(g.isCurrent(current));
                Assert.assertFalse(hasPlayed.contains(current));
                hasPlayed.add(current);
            }
            {
                Player current = g.getCurrent();
                Assert.assertFalse(g.addLine("result = '';"));
                Assert.assertFalse(g.isCurrent(current));
                Assert.assertFalse(hasPlayed.contains(current));
                hasPlayed.add(current);
            }
            {
                Player current = g.getCurrent();
                Assert.assertFalse(g.addLine("result = '';"));
                Assert.assertFalse(g.isCurrent(current));
                Assert.assertTrue(hasPlayed.contains(current));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("there should be no exceptions");

        }
    }
}
