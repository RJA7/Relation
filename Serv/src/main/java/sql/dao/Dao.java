package sql.dao;

import sql.beans.RelationBean;

import java.sql.SQLException;
import java.util.List;

public interface Dao {

    int ONE_PICTURE_TIME = 3000; //ms

    void close();

    void insert(RelationBean bean) throws SQLException;

    List<RelationBean> getTen() throws SQLException;

    int getLastIndex() throws SQLException;

    RelationBean getNextOrNothing(int lastImageIndex) throws SQLException;
}
