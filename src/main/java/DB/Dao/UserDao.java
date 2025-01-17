package DB.Dao;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import DB.Models.User;
import Configs.DBConfig;

import java.sql.SQLException;
import java.util.List;

public class UserDao {

    private Dao<User, String> userDao;

    public UserDao() throws SQLException {
        DBConfig dbConfig = DBConfig.getInstance();
        ConnectionSource connectionSource = new JdbcPooledConnectionSource(
                dbConfig.getConnectionUrl(), dbConfig.getUser(), dbConfig.getPassword()
        );
        userDao = DaoManager.createDao(connectionSource, User.class);
        TableUtils.createTableIfNotExists(connectionSource, User.class);
    }

    public String addUser(User user) throws SQLException {
        userDao.create(user);
        return user.getUuid();
    }

    public void updateUser(User user) throws SQLException {
        userDao.update(user);
    }

    public void deleteUser(User user) throws SQLException {
        userDao.delete(user);
    }

    public User findUserById(String id) throws SQLException {
        return userDao.queryForId(id);
    }

    public User findUserByUsername(String username) throws SQLException {
        return userDao.queryBuilder().where().eq("username", username).queryForFirst();
    }

    public User findUserByUuid(String uuid) throws SQLException {
        return userDao.queryBuilder().where().eq("uuid", uuid).queryForFirst();
    }

    public List<User> getAllUsers() throws SQLException {
        return userDao.queryForAll();
    }
}
