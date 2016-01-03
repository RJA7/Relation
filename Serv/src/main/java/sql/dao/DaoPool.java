package sql.dao;

import sql.beans.RelationBean;
import sql.datasourse.DataSource;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class DaoPool implements Dao {

    public void insert(RelationBean bean) throws SQLException {
        Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            String message = bean.getMessage();
            String link = bean.getLink();
            int imageIndex = bean.getImageIndex();

            stmt.executeUpdate("INSERT INTO relation (message, link, imageIndex)  VALUES ('" + message +
                    "', '" + link + "', '" + imageIndex + "')");
        } finally {
            quietlyClose(stmt);
            con.close();
        }
    }

    public List<RelationBean> getTen() throws SQLException {
        update();
        List<RelationBean> result = new ArrayList<RelationBean>();
        Connection con = getConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery("SELECT imageIndex FROM relation WHERE put=TRUE ");
            rs.next();
            int putIndex = rs.getInt(1);

            rs = stmt.executeQuery("SELECT message, link, imageIndex " +
                    "FROM relation WHERE imageIndex<" + (putIndex + 10) + " LIMIT 10");

            while (rs.next()) {
                RelationBean bean = new RelationBean(rs.getString("message"), rs.getString("link"), rs.getInt("imageIndex"));
                result.add(bean);
            }
        } finally {
            quietlyClose(rs);
            quietlyClose(stmt);
            con.close();
        }
        return result;
    }

    public int getLastIndex() throws SQLException {
        int res;
        Connection con = getConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery("SELECT imageIndex FROM relation ORDER BY imageIndex DESC LIMIT 1");
            rs.next();
            res = rs.getInt(1);
        } finally {
            quietlyClose(rs);
            quietlyClose(stmt);
            con.close();
        }
        return res;
    }

    public RelationBean getNextOrNothing(int lastImageIndex) throws SQLException {
        RelationBean result = new RelationBean("", "", -1);
        Connection con = getConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery("SELECT message, link, imageIndex FROM relation WHERE imageIndex>" + lastImageIndex + " LIMIT 1");
            if (rs.next()) {
                result.setMessage(rs.getString(1));
                result.setLink(rs.getString(2));
                result.setImageIndex(rs.getInt(3));
            }
        } finally {
            quietlyClose(rs);
            quietlyClose(stmt);
            con.close();
        }
        return result;
    }

    public void close() {
        //throw new UnsupportedOperationException();
    }

    private static synchronized int needUpdate() {
        int result = 0;
        InputStream is = null;
        OutputStream out = null;
        try {
            is = DaoPool.class.getResourceAsStream("/data.properties");
            Properties p = new Properties();
            p.load(is);
            int onePictureTime = Integer.parseInt(p.getProperty("onePictureTime"));
            long lastUpdate = Long.parseLong(p.getProperty("lastUpdate"));
            is.close();

            URL url = DaoPool.class.getResource("/data.properties");
            if (lastUpdate == 0) {
                p.setProperty("lastUpdate", String.valueOf(System.currentTimeMillis()));
                p.store(new FileOutputStream(new File(url.toURI())), null);
                return result;
            }

            Date currentDate = new Date();
            Date lastDate = new Date(lastUpdate);

            result = (int) (currentDate.getTime() - lastDate.getTime()) / onePictureTime;
            p.setProperty("lastUpdate", String.valueOf(currentDate.getTime()));
            p.store(new FileOutputStream(new File(url.toURI())), null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {/*NOP*/}
        }
        return result;
    }

    private void update() throws SQLException {
        int putIndex, newPutIndex, lastTenIndex, putMove;

        if ((putMove = needUpdate()) == 0) return;

        Connection con = getConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = null;

        try {
            rs = stmt.executeQuery("SELECT imageIndex FROM relation WHERE put=TRUE");
            rs.next();
            putIndex = rs.getInt(1);

            rs = stmt.executeQuery("SELECT imageIndex FROM relation ORDER BY imageIndex DESC LIMIT 10");
            for (int i = 0; i < 10; i++) {
                rs.next();
            }
            lastTenIndex = rs.getInt(1);

            newPutIndex = Math.min(lastTenIndex, putIndex + putMove);
            reduceAndSetPut(newPutIndex);
        } finally {
            quietlyClose(rs);
            quietlyClose(stmt);
            con.close();
        }
    }

    private void reduceAndSetPut(int newPutIndex) throws SQLException {
        Connection con = getConnection();
        Statement stmt = con.createStatement();
        con.setAutoCommit(false);
        try {
            stmt.addBatch("UPDATE relation SET put = FALSE WHERE put = TRUE");
            stmt.addBatch("UPDATE relation SET put = TRUE WHERE imageIndex =" + newPutIndex);
            stmt.addBatch("DELETE FROM relation WHERE imageIndex <" + newPutIndex);
            stmt.executeBatch();
            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
            quietlyClose(stmt);
            con.close();
        }
    }

    private Connection getConnection() throws SQLException {
        Connection con;
        try {
            con = DataSource.getInstance().getConnection();
        } catch (Exception e) {
            SQLException s = new SQLException();
            s.initCause(e);
            throw s;
        }
        return con;
    }

    private void quietlyClose(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                /*NOP*/
            }
        }
    }

    private void quietlyClose(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                /*NOP*/
            }
        }
    }

}
