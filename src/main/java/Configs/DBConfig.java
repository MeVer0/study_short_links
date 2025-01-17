package Configs;

import io.github.cdimascio.dotenv.Dotenv;

public class DBConfig {
    private static DBConfig instance;

    private String host;
    private String port;
    private String dbName;
    private String user;
    private String password;
    private String connectionUrl;

    private DBConfig() {
        Dotenv dotenv = Dotenv.load(); // Загрузка переменных из .env
        this.host = dotenv.get("MYSQL_HOST");
        this.port = dotenv.get("MYSQL_PORT");
        this.dbName = dotenv.get("MYSQL_DATABASE");
        this.user = dotenv.get("MYSQL_USER");
        this.password = dotenv.get("MYSQL_PASSWORD");
        this.connectionUrl = "jdbc:mysql://" + host + ":" + port + "/" + dbName;
    }

    // Метод для получения экземпляра класса
    public static synchronized DBConfig getInstance() {
        if (instance == null) {
            instance = new DBConfig();
        }
        return instance;
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }

    public String getDbName() {
        return dbName;
    }

    public String getPassword() {
        return password;
    }

    public String getUser() {
        return user;
    }
}
