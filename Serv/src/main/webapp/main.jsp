<%@ page import="sql.beans.RelationBean" %>
<%--
  Created by IntelliJ IDEA.
  User: RJA
  Date: 12/17/2015
  Time: 4:20 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="utf-8" />
    <script src="http://code.jquery.com/jquery-1.8.3.js"></script>
    <link rel="stylesheet" href="style.css" type="text/css" />
    <script src="motion.js"></script>
</head>
<body>
<% String[] messages = new String[10];
  for (int i = 0; i < messages.length; i++) {
    messages[i] = ((RelationBean) request.getAttribute(String.valueOf(i))).getMessage();
  }

  String[] links = new String[10];
  for (int i = 0; i < links.length; i++) {
    links[i] = ((RelationBean) request.getAttribute(String.valueOf(i))).getLink();
  }

  String[] images = new String[10];
  for (int i = 0; i < images.length; i++) {
      int index = ((RelationBean) request.getAttribute(String.valueOf(i))).getImageIndex();
      images[i] = "images/" + index + ".jpg";
  }
%>

    <div id="i0" class="item">  <a href="<%= links[0] %>" target="_blank" title="<%= messages[0] %>">
        <img src="<%= images[0] %>" alt="error" width="300" height="200"></a> </div>
    <div id="i1" class="item">  <a href="<%= links[1] %>" target="_blank" title="<%= messages[1] %>">
        <img src="<%= images[1] %>" alt="error" width="300" height="200"></a> </div>
    <div id="i2" class="item">  <a href="<%= links[2] %>" target="_blank" title="<%= messages[2] %>">
        <img src="<%= images[2] %>" alt="error" width="300" height="200"></a> </div>
    <div id="i3" class="item">  <a href="<%= links[3] %>" target="_blank" title="<%= messages[3] %>">
        <img src="<%= images[3] %>" alt="error" width="300" height="200"></a> </div>
    <div id="i4" class="item">  <a href="<%= links[4] %>" target="_blank" title="<%= messages[4] %>">
        <img src="<%= images[4] %>" alt="error" width="300" height="200"></a> </div>
    <div id="i5" class="item">  <a href="<%= links[5] %>" target="_blank" title="<%= messages[5] %>">
        <img src="<%= images[5] %>" alt="error" width="300" height="200"></a> </div>
    <div id="i6" class="item">  <a href="<%= links[6] %>" target="_blank" title="<%= messages[6] %>">
        <img src="<%= images[6] %>" alt="error" width="300" height="200"></a> </div>
    <div id="i7" class="item">  <a href="<%= links[7] %>" target="_blank" title="<%= messages[7] %>">
        <img src="<%= images[7] %>" alt="error" width="300" height="200"></a> </div>
    <div id="i8" class="item">  <a href="<%= links[8] %>" target="_blank" title="<%= messages[8] %>">
        <img src="<%= images[8] %>" alt="error" width="300" height="200"></a> </div>
    <div id="i9" class="item">  <a href="<%= links[9] %>" target="_blank" title="<%= messages[9] %>">
        <img src="<%= images[9] %>" alt="error" width="300" height="200"></a> </div>

    <div id="add"><input type="image" value="add" src="img/add.png" width="100%" height="100%" onclick="setVisibility();" /></div>
    <div id="addform">
        <form method="post" action="register_image" enctype="multipart/form-data" target="iframe1">
            Input your message:           <input type="text"  name="message" /><br /><br />
            Input a href to your site:    <input type="url" value ="" name="link" /><br /><br />
            Paste image:                  <input type="file" value ="Image" name="image" /><br /><br />
            <input type="submit" value ="ok" />
        </form>
    </div>

    <div id="footer">Created by RJA</div>

    <iframe name="iframe1" style="position: absolute; left: -9999px;"></iframe>
</body>
</html>
