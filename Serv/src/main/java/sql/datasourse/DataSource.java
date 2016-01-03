package sql.datasourse;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.beans.PropertyVetoException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DataSource {
    private static ComboPooledDataSource cpds;
    private static DataSource dataSource;

    private DataSource() throws IOException, PropertyVetoException {
        InputStream is = getClass().getResourceAsStream("/data.properties");
        Properties properties = new Properties();
        properties.load(is);

        cpds = new ComboPooledDataSource();
        cpds.setDriverClass(properties.getProperty("mysqlDriverClass"));
        cpds.setJdbcUrl(properties.getProperty("jdbcUrl"));
        cpds.setUser(properties.getProperty("login"));
        cpds.setPassword(properties.getProperty("password"));

        cpds.setInitialPoolSize(Integer.parseInt(properties.getProperty("initialPoolSize")));
        cpds.setMinPoolSize(Integer.parseInt(properties.getProperty("minPoolSize")));
        cpds.setMaxPoolSize(Integer.parseInt(properties.getProperty("maxPoolSize")));
        cpds.setAcquireIncrement(Integer.parseInt(properties.getProperty("acquireIncrement")));

        is.close();
    }

    public static DataSource getInstance() throws IOException, PropertyVetoException {
        if (dataSource == null) {
            dataSource = new DataSource();
        }
        return dataSource;
    }

    public Connection getConnection() throws SQLException {
        return cpds.getConnection();
    }
}
