package abnormale.knochen.exquisitecode.web.messages;

import abnormale.knochen.exquisitecode.web.Token;

public abstract class Message {
    enum Type {
        ADDLINE(AddLineMessage.class), ERROR(ErrorMessage.class);

        private Class clazz;

        Type(Class clazz) {
            this.clazz = clazz;
        }

        public Class getClazz() {
            return this.clazz;
        }
    }

    protected Token token;
    protected Type type;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }
}
