package com.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizServelet extends HttpServlet {
    private List<Question> questions = new ArrayList<>();

    @Override
    public void init() throws ServletException {
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream inputStream = getServletContext().getResourceAsStream("/WEB-INF/question.json")) {
            if (inputStream == null) {
                throw new IOException("Questions file not found");
            }
            questions = objectMapper.readValue(inputStream, new TypeReference<List<Question>>() {});
            selectRandomQuestions();
        } catch (IOException e) {
            throw new ServletException("Failed to load questions", e);
        }
    }

    private void selectRandomQuestions() {
        Collections.shuffle(questions);
        questions = questions.subList(0, Math.min(5, questions.size()));
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handleQuiz(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handleQuiz(request, response);
    }

    private void handleQuiz(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if ("true".equals(request.getParameter("restart"))) {
            request.getSession().removeAttribute("score");
            request.getSession().setAttribute("questionIndex", 0);
            selectRandomQuestions();
            response.sendRedirect("quiz");
            return;
        }

        Integer score = (Integer) request.getSession().getAttribute("score");
        if (score == null) {
            score = 0;
        }

        int questionIndex = Integer.parseInt(request.getParameter("questionIndex") == null ? "0" : request.getParameter("questionIndex"));

        if (request.getMethod().equalsIgnoreCase("POST")) {
            String answerStr = request.getParameter("answer");
            if (answerStr != null) {
                int answer = Integer.parseInt(answerStr);
                if (questionIndex < questions.size() && answer == questions.get(questionIndex).getAnswerIndex()) {
                    score++;
                }
            }
            questionIndex++;
        }

        request.getSession().setAttribute("score", score);

        if (questionIndex < questions.size()) {
            Question currentQuestion = questions.get(questionIndex);
            request.setAttribute("question", currentQuestion.getQuestion());
            request.setAttribute("options", currentQuestion.getOptions());
            request.setAttribute("questionIndex", questionIndex);
            request.setAttribute("totalQuestions", questions.size());
            RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
            dispatcher.forward(request, response);
        } else {
            request.setAttribute("score", score);
            request.setAttribute("question", "Quiz completed! Your score: " + score + " out of " + questions.size());
            request.setAttribute("options", Collections.emptyList());
            request.setAttribute("questionIndex", questions.size());
            request.setAttribute("totalQuestions", questions.size());
            request.setAttribute("quizFinished", true);
            RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
            dispatcher.forward(request, response);
        }
        System.out.println("Score: " + score);
    }
}
