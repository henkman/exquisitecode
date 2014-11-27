package abnormale.knochen.exquisitecode.interp;

import org.junit.Assert;
import org.junit.Test;

public class TestInterpreter {
    @Test
    public void testJavascriptInterpreter() throws Exception {
        Interpreter interp = InterpreterManager.getInterpreter("JavaScript");
        String result = interp.execute("var result = ''\nfor(i=0;i<100;)result+=(++i%15?i%5?i%3?i:'Buzz':'Fizz':'FizzBuzz')+'\\n';");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; ) {
            sb.append(++i % 15 > 0 ? i % 5 > 0 ? i % 3 > 0 ? i : "Buzz" : "Fizz" : "FizzBuzz");
            sb.append('\n');
        }
        Assert.assertEquals(sb.toString(), result);
    }

    @Test
    public void testJavascriptValidatorCommentRemoval() throws Exception {
        Interpreter interp = InterpreterManager.getInterpreter("JavaScript");
        String line = interp.fixLine("var abc = 12; /* */ var foo = '';");
        Assert.assertEquals("var abc = 12;  var foo = '';", line);
    }
}
