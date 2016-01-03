package com.freerelationship;

import sql.beans.RelationBean;
import sql.dao.Dao;
import sql.dao.DaoPool;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "main", urlPatterns = "/main", loadOnStartup = 1)
public class Main extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Dao dao = new DaoPool();
            List<RelationBean> list = dao.getTen();
            dao.close();

            if (list.size() < 10) {
                throw new SQLException();
            }

            for (int i = 0; i < list.size(); i++) {
                req.setAttribute(String.valueOf(i), list.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;//todo: forward to error page
        }

        RequestDispatcher dispatcher = req.getRequestDispatcher("main.jsp");
        dispatcher.forward(req, resp);
    }
}
