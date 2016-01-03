package com.freerelationship;

import org.json.JSONObject;
import sql.beans.RelationBean;
import sql.dao.Dao;
import sql.dao.DaoPool;

import java.io.*;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet (       urlPatterns = "/register_image",
                    name = "register_image",
                    loadOnStartup = 50)
@MultipartConfig(   fileSizeThreshold=1024*1024,        // 1 MB
                    maxFileSize=1024*1024*5,            // 5 MB
                    maxRequestSize=1024*1024*10)        // 10 MB

public class RegisterImage extends HttpServlet {

    static private int name = -1;

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String message = null;
        String link = null;
        Part image = null;

        try {
            image = getPart(request);
            message = getMessage(request);
            link = getLink(request);
        } catch (Exception re) {
            String error = re.getMessage();
            sendAnswer(error, out);
        }

        try {
            int imageIndex = saveImage(image);
            Dao dao = new DaoPool();
            dao.insert(new RelationBean(message, link, imageIndex));
            dao.close();
        } catch (SQLException e) {
            sendAnswer("Server error.", out);
        }

        sendAnswer("Uploaded successfully.", out);
    }

    private void sendAnswer(String cause, PrintWriter out) {
        JSONObject answer = new JSONObject();

        answer.put("answer", cause);

        out.print(answer);
        out.flush();
    }

    private int saveImage(Part image) throws IOException, SQLException {
        if (name == -1) {
            Dao dao = new DaoPool();
            name = dao.getLastIndex() + 1;
            dao.close();
        }
        String realPath = getServletContext().getRealPath(getServletContext().getContextPath()) + "images" + File.separator + name + ".jpg";

        image.write(realPath);

        return name++;
    }

    private String getLink(HttpServletRequest request) throws IOException, ServletException {
        Part part = request.getPart("link");
        BufferedReader in = new BufferedReader(new InputStreamReader(part.getInputStream()));
        String result = in.readLine();

        if (result.length() > 100) {
            throw new IllegalArgumentException("The link is longer than 256 symbols.");
        }
        return result;
    }

    private String getMessage(HttpServletRequest request) throws IOException, ServletException {
        Part part = request.getPart("message");
        BufferedReader in = new BufferedReader(new InputStreamReader(part.getInputStream()));
        String result = in.readLine();

        if (result.length() > 256) {
            throw new IllegalArgumentException("The message is longer than 256 symbols.");
        }
        return result;
    }

    private Part getPart(HttpServletRequest request) throws IOException, ServletException {
        Part part = request.getPart("image");

        if (part == null) {
            throw new NullPointerException("There is no any image in request.");
        }
        if (!isImage(part)) {
            throw new IllegalArgumentException("Illegal image file.");
        }

        return resize(part);
    }

    private Part resize(Part part) {
        return part;    //todo: return resize part;
    }

    private boolean isImage(Part part) {
        return true;    //todo: return true if part is image;
    }

}
