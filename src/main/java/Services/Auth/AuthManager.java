package Services.Auth;

import DB.Dao.UserDao;
import DB.Models.User;

import java.sql.SQLException;
import java.util.Objects;

public class AuthManager {

    private UserDao userDao;
    private User currentUser;

    {
        try {
            userDao = new UserDao();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean login(String uuid) {
        if (currentUser != null) {
            System.out.println("Please logout before attempting to login");
        } else {
            try {
                currentUser = userDao.findUserByUuid(uuid);
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public boolean logout() {
        if (Objects.equals(currentUser, null)) {
            System.out.println("You already logged out");
            return true;
        }
        return true;
    }

    public int getCurrentUserId() {
        return currentUser.getId();
    }

}
