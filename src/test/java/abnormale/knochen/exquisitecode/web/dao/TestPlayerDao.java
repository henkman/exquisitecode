package abnormale.knochen.exquisitecode.web.dao;

import abnormale.knochen.exquisitecode.game.Player;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

public class TestPlayerDao {
    @Before
    public void init() throws SQLException, ClassNotFoundException {
        Db.init();
    }

    @Test
    public void testGetPlayer() {
        Player p = PlayerDao.create("john", "john");
        Assert.assertNotNull(p);
        Assert.assertEquals("john", p.getName());
        int id = p.getId();
        Player pg = PlayerDao.getByNameAndPassword("john", "john");
        Assert.assertNotNull(pg);
        Assert.assertEquals(id, pg.getId());
        PlayerDao.deleteById(id);
        p = PlayerDao.getByName("john");
        Assert.assertNull(p);
    }
}
