package DB.Models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.UUID;

@DatabaseTable(tableName = "Users") // Название таблицы должно совпадать с базой данных
public class User {

    @DatabaseField(columnName = "Id", generatedId = true) // Генерация автоинкрементного ID
    private int id;

    @DatabaseField(columnName = "username", canBeNull = false, unique = true) // username как уникальное поле
    private String username;

    @DatabaseField(columnName = "uuid", canBeNull = false, unique = true) // UUID как уникальное поле
    private String uuid;

    public User() {
    }

    public User(String username) {
        this.username = username;
        this.uuid = UUID.randomUUID().toString();
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUuid() {
        return uuid;
    }
}
