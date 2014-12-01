package abnormale.knochen.exquisitecode.web.dao;

import abnormale.knochen.exquisitecode.game.Player;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerDao {
    private static byte[] hash(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        return digest.digest(data);
    }

    public static void deleteById(int id) {
        PreparedStatement stmt = null;
        try {
            stmt = Db.prepareStatement("DELETE FROM PLAYER WHERE id=?");
            stmt.setInt(1, id);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Player create(String name, String password) {
        PreparedStatement stmt = null;
        try {
            byte[] hash = hash(password.getBytes());

            stmt = Db.prepareStatement("INSERT INTO PLAYER (name, password) VALUES (?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, name);
            stmt.setBytes(2, hash);
            stmt.executeUpdate();
            ResultSet r = stmt.getGeneratedKeys();
            if (r.next()) {
                return new Player(r.getInt(1), name);
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Player getByName(String name) {
        PreparedStatement stmt = null;
        try {
            stmt = Db.prepareStatement("SELECT id, name FROM player WHERE name=?");
            stmt.setString(1, name);
            stmt.setMaxRows(1);
            ResultSet r = stmt.executeQuery();
            if (!r.next()) {
                return null;
            }
            return new Player(r.getInt("id"), r.getString("name"));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Player getByNameAndPassword(String name, String password) {
        PreparedStatement stmt = null;
        try {
            byte[] hash = hash(password.getBytes());
            stmt = Db.prepareStatement("SELECT id, name FROM player WHERE name=? and password=?");
            stmt.setString(1, name);
            stmt.setBytes(2, hash);
            stmt.setMaxRows(1);
            ResultSet r = stmt.executeQuery();
            if (!r.next()) {
                return null;
            }
            return new Player(r.getInt("id"), r.getString("name"));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
