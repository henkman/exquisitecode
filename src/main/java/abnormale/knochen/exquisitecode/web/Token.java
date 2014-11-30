package abnormale.knochen.exquisitecode.web;

public class Token {
    private int id;
    private String random;

    public Token(int id) {
        this.id = id;
        this.random = String.valueOf(Math.random()).replace(".", "");
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
}
