package Services.Auth;

import DB.Models.User;
import DB.Dao.UserDao;

import java.sql.SQLException;
import java.util.Optional;


public class RegistrationManager {

    private UserDao userDao;

    {
        try {
            userDao = new UserDao();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public RegistrationManager() throws SQLException {
    }

    public String registerNewUser(String username) throws SQLException {
        User userModel = new User(username);
        return userDao.addUser(userModel);
    }

    public boolean checkAlreadyRegistered(String username) throws SQLException {
        User userModels = userDao.findUserByUsername(username);
        return userModels != null;
    };

}
