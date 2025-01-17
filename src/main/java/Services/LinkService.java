package Services;

import DB.Dao.LinkDao;
import DB.Models.Link;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import java.awt.Desktop;
import java.net.URI;

public class LinkService {

    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private LinkDao linkDao;
    {
        try {
            linkDao = new LinkDao();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static String base62Encode(byte[] input) {
        StringBuilder result = new StringBuilder();
        long value = 0;

        for (byte b : input) {
            value = (value << 8) + (b & 0xFF);
        }

        while (value > 0) {
            result.insert(0, BASE62.charAt((int) (value % 62)));
            value /= 62;
        }

        return result.toString();
    }

    public String addLink(int userId,String uuid ,String longLink, int numberOfAvailableUses) {
        String shortLink = base62Encode((longLink + uuid).getBytes());
        Link link = new Link(userId, longLink, shortLink, numberOfAvailableUses);
        try {
            linkDao.addLink(link);
            return shortLink;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteLink(int userId, String shortLink) {
        try {
            linkDao.deleteLinkByUserIdNShortLink(userId, shortLink);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void editLink(int userId, String shortLink, Optional<String> longLink, Optional<Integer> numberOfAvailableUses, Optional<Boolean> prolongLife) {
        try {
            linkDao.editLink(userId, shortLink, longLink, numberOfAvailableUses, prolongLife);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean goToResource(int userId, String shortLink) throws SQLException {
        Link link = linkDao.getLinkByUserIdNShortLink(userId, shortLink);
        try {
            if (!link.isAvailable()) {
                return false;
            }
            Desktop.getDesktop().browse(new URI(link.getLongLink()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        editLink(userId, shortLink, Optional.empty(), Optional.of(link.getNumberOfAvailableUses() - 1), Optional.empty());
        return true;
    }

    public List<Link> getLinksByUserId(int userId) throws SQLException {
        return linkDao.getLinksByUserId(userId);
    };
}
