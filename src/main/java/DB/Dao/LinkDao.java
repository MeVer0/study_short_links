package DB.Dao;

import Configs.DBConfig;
import DB.Models.Link;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class LinkDao {
    private Dao<Link, String> linkDao;

    public LinkDao() throws SQLException {
        DBConfig dbConfig = DBConfig.getInstance();
        ConnectionSource connectionSource = new JdbcPooledConnectionSource(
                dbConfig.getConnectionUrl(), dbConfig.getUser(), dbConfig.getPassword()
        );
        linkDao = DaoManager.createDao(connectionSource, Link.class);
        TableUtils.createTableIfNotExists(connectionSource, Link.class);
    }

    public void addLink(Link link) throws SQLException {
        linkDao.create(link);
    }

    public void updateLink(Link link) throws SQLException {
        linkDao.update(link);
    }

    public void deleteLink(Link link) throws SQLException {
        linkDao.delete(link);
    }

    public Link linkDao(String id) throws SQLException {
        return linkDao.queryForId(id);
    }

    public void deleteLinkByUserIdNShortLink(int userId, String shortLink) throws SQLException {
        Link linkToDel = linkDao.queryBuilder()
                .where()
                .eq("shortLink", shortLink)
                .and()
                .eq("userId", userId)
                .queryForFirst();

        linkDao.delete(linkToDel);
    }

    public void editLink(int userId, String shortLink, Optional<String> longLink, Optional<Integer> numberOfAvailableUses, Optional<Boolean> prolongLife) throws SQLException {
        Link linkToEdit = linkDao.queryBuilder()
                .where()
                .eq("shortLink", shortLink)
                .and()
                .eq("userId", userId)
                .queryForFirst();

        if (longLink.isPresent()) {
            linkToEdit.setLongLink(longLink.get());
        }

        if (numberOfAvailableUses.isPresent()) {
            linkToEdit.setNumberOfAvailableUses(numberOfAvailableUses.get());
        }

        if (prolongLife.isPresent()) {
            linkToEdit.setAvailableUntil(new Date(System.currentTimeMillis() + 3600 * 24000));
        }

        linkDao.update(linkToEdit);
    }

    public Link getLinkByUserIdNShortLink(int userId, String shortLink) throws SQLException {
        return linkDao.queryBuilder()
                .where()
                .eq("shortLink", shortLink)
                .and()
                .eq("userId", userId)
                .queryForFirst();
    }

    public List<Link> getLinksByUserId(int userId) throws SQLException {
        List<Link> links = linkDao.queryBuilder()
                .where()
                .eq("userId", userId)
                .query();
        return links;
    }
}
