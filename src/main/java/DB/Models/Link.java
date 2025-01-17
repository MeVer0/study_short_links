package DB.Models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.DatabaseTableConfig;

import java.util.Date;

@DatabaseTable(tableName = "Links") // Уникальная комбинация ключей
public class Link {

    @DatabaseField(columnName = "Id", generatedId = true)
    private int id;

    @DatabaseField(columnName = "userId", canBeNull = false, uniqueCombo = true)
    private int userId;

    @DatabaseField(columnName = "longLink", canBeNull = false, width = 300, uniqueCombo = true) // Указываем длину
    private String longLink;

    @DatabaseField(columnName = "shortLink", canBeNull = false, uniqueCombo = true) // Уникальный ключ
    private String shortLink;

    @DatabaseField(columnName = "numberOfAvailableUses", canBeNull = false)
    private int numberOfAvailableUses;

    @DatabaseField(columnName = "availableUntil", canBeNull = false, dataType = DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss")
    private Date availableUntil;

    public Link() {
    }

    public Link(int userId, String longLink, String shortLink, int numberOfAvailableUses) {
        this.userId = userId;
        this.longLink = longLink;
        this.shortLink = shortLink;
        this.numberOfAvailableUses = numberOfAvailableUses;
        this.availableUntil = new Date(System.currentTimeMillis() + 3600 * 24000);
    }

    public int getId() {
        return id;
    }

    public String getLongLink() {
        return longLink;
    }

    public void setLongLink(String longLink) {
        this.longLink = longLink;
    }

    public String getShortLink() {
        return shortLink;
    }

    public void setShortLink(String shortLink) {
        this.shortLink = shortLink;
    }

    public int getNumberOfAvailableUses() {
        return numberOfAvailableUses;
    }

    public void setNumberOfAvailableUses(int numberOfAvailableUses) {
        this.numberOfAvailableUses = numberOfAvailableUses;
    }

    public Date getAvailableUntil() {
        return availableUntil;
    }

    public void setAvailableUntil(Date availableUntil) {
        this.availableUntil = availableUntil;
    }

    public boolean isAvailable() {
        return this.availableUntil.getTime() >= System.currentTimeMillis() && this.numberOfAvailableUses > 0;
    }
}
