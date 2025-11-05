package Darius.repository.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JdbcUtils {
    private Properties jdbcProperties;

    private static final Logger logger = LogManager.getLogger(JdbcUtils.class);

    private Connection instance = null;

    public JdbcUtils(Properties properties){
        jdbcProperties = properties;
    }

    private Connection getNewConnection(){
        logger.traceEntry("Creez o conexiune cu baza de date");

        String url = jdbcProperties.getProperty("jdbc.url");

        logger.info("Incercam o conectare la baza de date ... {}", url);

        Connection connection = null;

        try{
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            logger.error(e);
        }

        return connection;
    }

    public Connection getConnection(){
        logger.traceEntry("Get connection for database");

        try{
            if (instance == null || instance.isClosed()){
                instance = getNewConnection();
            }
        } catch (SQLException e) {
            logger.error(e);
        }

        logger.traceExit(instance);

        return instance;
    }
}
