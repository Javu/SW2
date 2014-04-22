package fantasyteam.ft1.networkingbase;

import org.easymock.EasyMock;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author javu
 */
public class SockTest {
    
    @Test
    public void DefaultConstructor() {
        Sock sock = new Sock();
        Assert.assertEquals(sock.getSocket(), null, "Value of socket in Sock not set to null");
        Assert.assertEquals(sock.getOut(), null, "Value of out in Sock not set to null");
        Assert.assertEquals(sock.getIn(), null, "Value of in in Sock not set to null");
    }
}
