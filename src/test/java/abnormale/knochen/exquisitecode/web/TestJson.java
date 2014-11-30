package abnormale.knochen.exquisitecode.web;

import abnormale.knochen.exquisitecode.web.messages.AddLineMessage;
import abnormale.knochen.exquisitecode.web.messages.Message;
import org.junit.Assert;
import org.junit.Test;

public class TestJson {
    @Test
    public void testMarshalling() {
        AddLineMessage addLineMessage = new AddLineMessage();
        addLineMessage.setLine("line");
        String s = Json.marshal(addLineMessage);
        Assert.assertEquals("{\"line\":\"line\",\"type\":\"ADDLINE\"}", s);
        Message m = Json.unmarshal(s, Message.class);
        if (m instanceof AddLineMessage) {
            AddLineMessage addLineMessage1 = (AddLineMessage) m;
            Assert.assertEquals("line", addLineMessage1.getLine());
        } else {
            Assert.fail("did not manage to unmarshal to correct type");
        }
    }
}
