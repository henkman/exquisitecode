package abnormale.knochen.exquisitecode.interp;

import java.util.regex.Pattern;

public class JavaScriptValidator implements Validator {

    private static final Pattern reLineComment = Pattern.compile("//[^$]+$");
    private static final Pattern reBlockComment = Pattern.compile("/\\*.*?\\*/");

    private String removeComments(String line) {
        line = reLineComment.matcher(line).replaceAll("");
        line = reBlockComment.matcher(line).replaceAll("");
        return line;
    }

    @Override
    public String fixLine(String line) {
        line = removeComments(line);
        // TODO: remove variables or functions with more than 3 characters
        return line;
    }
}
