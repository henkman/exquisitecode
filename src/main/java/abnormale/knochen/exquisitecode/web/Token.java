package abnormale.knochen.exquisitecode.web;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class Token {
    private int id;
    private String random;
    private Date freshUntil;

    public Token(int id, int secondsFresh) {
        this.id = id;
        this.random = UUID.randomUUID().toString();
        freshUntilSeconds(secondsFresh);
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
