package com.freerelationship;

import org.json.JSONObject;
import sql.beans.RelationBean;
import sql.dao.Dao;
import sql.dao.DaoPool;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

/**
 * Created by RJA.
 */
@WebServlet (name = "getImage", urlPatterns = "/getImage", loadOnStartup = 30)
public class GetImage extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        String lastImageSrc = req.getParameter("lastImageSrc");
        int lastIndex = parseToInt(lastImageSrc);
        RelationBean bean;

        try {
            Dao dao = new DaoPool();
            bean = dao.getNextOrNothing(lastIndex);
            dao.close();
        } catch (SQLException e) {
            log(e.getMessage());
            return;
        }

        String path = "nothing";
        if (bean.getImageIndex() != -1) {
            path = "images/" + bean.getImageIndex() + ".jpg";
        }

        JSONObject answer = new JSONObject();
        answer.put("putMessage", bean.getMessage());
        answer.put("putLink", bean.getLink());
        answer.put("imagePath", path);
        out.print(answer);
        out.flush();
    }

    private int parseToInt(String lastImageSrc) {
        char[] arr = lastImageSrc.toCharArray();
        int firstIndex = 0, lastIndex = arr.length - 5;

        for (int i = arr.length - 1; i >= 0; i--) {
            if (arr[i] == '/') {
                firstIndex = i + 1;
                break;
            }
        }
        String num = String.valueOf(arr, firstIndex, lastIndex - firstIndex + 1);

        return Integer.parseInt(num);
    }
}
