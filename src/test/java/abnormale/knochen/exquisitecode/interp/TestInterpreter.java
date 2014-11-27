package abnormale.knochen.exquisitecode.interp;

import org.junit.Assert;
import org.junit.Test;

public class TestInterpreter {
	@Test
	public void testJavascriptInterpreter() {
		Interpreter interp = LanguageManager.getInterpreter(JavascriptInterpreter.name);
		try {
			String result = interp.execute("var a = 0;" +
			"print('wat')");
			Assert.assertEquals("wat", "wat");
		} catch (InterpreterException e) {
			e.printStackTrace();
		}
	}
}
