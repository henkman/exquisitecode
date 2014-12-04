package abnormale.knochen.exquisitecode.web.messages.server;

import abnormale.knochen.exquisitecode.web.messages.Message;

public class CodeAndResultMessage extends Message {
    private String code;
    private String result;

    public CodeAndResultMessage(String code, String result) {
        this.type = Type.CODEANDRESULT;
        this.code = code;
        this.result = result;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
