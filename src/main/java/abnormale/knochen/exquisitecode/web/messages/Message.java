package abnormale.knochen.exquisitecode.web.messages;

import abnormale.knochen.exquisitecode.web.messages.client.AddLineMessage;
import abnormale.knochen.exquisitecode.web.messages.client.ErrorMessage;
import abnormale.knochen.exquisitecode.web.messages.client.LoginMessage;
import abnormale.knochen.exquisitecode.web.messages.server.CodeAndResultMessage;
import abnormale.knochen.exquisitecode.web.messages.server.PlayersMessage;

public abstract class Message {
    public enum Type {
        ADDLINE(AddLineMessage.class), ERROR(ErrorMessage.class),
        LOGIN(LoginMessage.class), CODEANDRESULT(CodeAndResultMessage.class),
        PLAYERS(PlayersMessage.class);

        private Class clazz;

        Type(Class clazz) {
            this.clazz = clazz;
        }

        public Class getClazz() {
            return this.clazz;
        }
    }

    protected Type type;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
