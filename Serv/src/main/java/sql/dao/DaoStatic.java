package sql.dao;

import sql.beans.RelationBean;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by RJA.
 */
public class DaoStatic implements Dao{

    private static Date lastUpdate;

    private Connection connection;
    private Statement statement;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    static {
        try {
            DriverManager.registerDriver(new org.gjt.mm.mysql.Driver());
        } catch (SQLException e) {/*NOP*/}
    }

    public DaoStatic() throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydata", "root", "root");
        statement = connection.createStatement();
        if (lastUpdate == null) {
            lastUpdate = new Date();
        }
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {/*NOP*/}
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (Exception e) {/*NOP*/}
        }
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (Exception e) {/*NOP*/}
        }
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (Exception e) {/*NOP*/}
        }
    }

    public void insert(RelationBean bean) throws SQLException {
        String message = bean.getMessage();
        String link = bean.getLink();
        int imageIndex = bean.getImageIndex();

        preparedStatement = connection.prepareStatement("INSERT INTO relation (message, link, imageIndex)  VALUES (?, ?, ?)");
        preparedStatement.setString(1, message);
        preparedStatement.setString(2, link);
        preparedStatement.setInt(3, imageIndex);

        preparedStatement.executeUpdate();
    }

    public List<RelationBean> getTen() throws SQLException {
        update();
        List<RelationBean> result = new ArrayList<RelationBean>();

        resultSet = statement.executeQuery("SELECT imageIndex FROM relation WHERE put=TRUE ");
        resultSet.next();
        int putIndex = resultSet.getInt(1);

        resultSet = statement.executeQuery("SELECT message, link, imageIndex " +
                "FROM relation WHERE imageIndex<" + (putIndex + 10) + " LIMIT 10");

        while (resultSet.next()) {
            RelationBean bean = new RelationBean(resultSet.getString("message"), resultSet.getString("link"), resultSet.getInt("imageIndex"));
            result.add(bean);
        }

        return result;
    }

    private static synchronized int needUpdate() {
        Date currentDate = new Date();

        int result = (int) (currentDate.getTime() - lastUpdate.getTime()) / ONE_PICTURE_TIME;
        lastUpdate = currentDate;

        return result;
    }

    private void update() throws SQLException {
        int putIndex, newPutIndex, lastTenIndex, putMove;

        if ((putMove = needUpdate()) == 0) return;

        resultSet = statement.executeQuery("SELECT imageIndex FROM relation WHERE put=TRUE");
        resultSet.next();
        putIndex = resultSet.getInt(1);

        resultSet = statement.executeQuery("SELECT imageIndex FROM relation ORDER BY imageIndex DESC LIMIT 10");
        for (int i = 0; i < 10; i++) {
            resultSet.next();
        }
        lastTenIndex = resultSet.getInt(1);

        newPutIndex = Math.min(lastTenIndex, putIndex + putMove);
        reduceAndSetPut(newPutIndex);
    }

    private void reduceAndSetPut(int newPutIndex) throws SQLException {
            connection.setAutoCommit(false);
        try {
            statement.execute("UPDATE relation SET put = FALSE WHERE put = TRUE");
            statement.execute("UPDATE relation SET put = TRUE WHERE imageIndex =" + newPutIndex);
            //statement.execute("DELETE FROM relation WHERE imageIndex <" + newPutIndex);

            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public int getLastIndex() throws SQLException {
        resultSet = statement.executeQuery("SELECT imageIndex FROM relation ORDER BY imageIndex DESC LIMIT 1");
        resultSet.next();
        return resultSet.getInt(1);
    }

    public RelationBean getNextOrNothing(int lastImageIndex) throws SQLException {
        RelationBean result = new RelationBean();
        String message = "", link = "";
        int imageIndex = -1;

        resultSet = statement.executeQuery("SELECT message, link, imageIndex FROM relation WHERE imageIndex>" + lastImageIndex + " LIMIT 1");
        if (resultSet.next()) {
            message = resultSet.getString(1);
            link = resultSet.getString(2);
            imageIndex = resultSet.getInt(3);
        }
        result.init(message, link, imageIndex);

        return result;
    }

}
