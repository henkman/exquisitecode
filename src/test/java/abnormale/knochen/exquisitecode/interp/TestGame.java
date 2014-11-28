package abnormale.knochen.exquisitecode.interp;

import abnormale.knochen.exquisitecode.game.Game;
import abnormale.knochen.exquisitecode.game.Task;
import org.junit.Assert;
import org.junit.Test;

import javax.script.ScriptException;

public class TestGame {

    @Test
    public void testGameLineAddingAndSolving1() throws Exception {
        Interpreter interp = InterpreterManager.getInterpreter("JavaScript");
        Task task = new Task("", "", "foo");
        Game g = new Game(interp, task);
        g.addLine("result = 'foo';");
        Assert.assertTrue(g.isSolved());
    }

    @Test
    public void testGameLineAddingAndSolving2() throws Exception {
        Interpreter interp = InterpreterManager.getInterpreter("JavaScript");
        Task task = new Task("", "", "foo");
        Game g = new Game(interp, task);
        g.addLine("result = 'bar';");
        Assert.assertFalse(g.isSolved());
    }

    @Test
    public void testGameLineAddingAndSolvingParserError() throws Exception {
        Interpreter interp = InterpreterManager.getInterpreter("JavaScript");
        Task task = new Task("", "", "");
        Game g = new Game(interp, task);
        try {
            g.addLine("result = ;");
            Assert.fail("script should throw parse error");
        } catch (ScriptException e) {
            Assert.assertTrue(e.getMessage().contains("Expected an operand but found ;"));
        }
    }
}