<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.List" %>

<%
    Integer questionIndex = (Integer) request.getAttribute("questionIndex");
    if (questionIndex == null) {
        response.sendRedirect("quiz");
        return;
    }
%>
<html>
<head>
    <title>Quiz Application</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/style.css">
</head>
<body>
<h1>Quiz</h1>
<%
    Boolean quizFinished = (Boolean) request.getAttribute("quizFinished");
    if (quizFinished != null && quizFinished) {
        int score = (int) request.getAttribute("score");
        int totalQuestions = (int) request.getAttribute("totalQuestions");
        int progressPercentage = (int) ((double) score / totalQuestions * 100);
%>
        <h2><%= request.getAttribute("question") %></h2>

       
        <form action="quiz" method="post">
            <input type="hidden" name="restart" value="true">
            <input type="submit" value="Restart Quiz">
        </form>
       
        <div class="progress-circle" data-progress="<%= progressPercentage %>">
            <span><%= progressPercentage %>%</span>
        </div>
  <h2>Your Score: <%= score %> out of <%= totalQuestions %></h2>
        
     
<%
    } else {
%>
        <form action="quiz" method="post">
        <div class="ques">
            <h2><%= request.getAttribute("question") %></h2>
        </div>
            <%
                List<String> options = (List<String>) request.getAttribute("options");
                if (options != null && !options.isEmpty()) {
                    for (int i = 0; i < options.size(); i++) {
                        String option = options.get(i);
            %>
            <div class="opt">
                <input type="radio" name="answer" value="<%= i %>"> <%= option %></div>
            <%
                    }
                } else {
            %>
                    <p>No options available.</p>
            <%
                }
            %>
            <input type="hidden" name="questionIndex" value="<%= request.getAttribute("questionIndex") %>">
            <input type="submit" value="Next">
        </form>
<%
    }
%>

<script>
    document.addEventListener("DOMContentLoaded", function () {
        const progressCircle = document.querySelector(".progress-circle");
        if (progressCircle) {
            const progress = progressCircle.getAttribute("data-progress");
            progressCircle.style.setProperty("--progress", progress);
        }
    });
</script>
</body>
</html>
