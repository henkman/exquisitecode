package abnormale.knochen.exquisitecode.web;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Token {
    private int id;
    private String random;
    private Date freshUntil;

    public Token(int id, int secondsFresh) {
        this.id = id;
        this.random = UUID.randomUUID().toString().replace("-", "");
        freshUntilSeconds(secondsFresh);
    }

    private Token(int id, String random) {
        this.id = id;
        this.random = random;
    }

    private static final Pattern reToken = Pattern.compile("\\d+:[a-z0-9]{32}");

    public static Token parse(String s) {
        Matcher m = reToken.matcher(s);
        if (!m.matches()) {
            return null;
        }
        try {
            int id = Integer.parseInt(m.group(0));
            return new Token(id, m.group(2));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return String.format("%d:%s", this.id, this.random);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Token token = (Token) o;

        if (id != token.id) return false;
        if (random != null ? !random.equals(token.random) : token.random != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (random != null ? random.hashCode() : 0);
        return result;
    }

    public boolean isFresh() {
        return freshUntil.after(Calendar.getInstance().getTime());
    }

    public void freshUntilSeconds(int secondsFresh) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, secondsFresh);
        freshUntil = c.getTime();
    }
}
