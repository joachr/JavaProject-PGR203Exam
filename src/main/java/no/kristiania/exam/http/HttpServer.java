package no.kristiania.exam.http;

import no.kristiania.exam.http.controllers.*;
import no.kristiania.exam.survey.Question;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.*;

public class HttpServer {

    private final ServerSocket serverSocket;
    private final HashMap<String, HttpController> controllers = new HashMap<>();
    private Path rootDirectory;
    private final String statusOk = "HTTP/1.1 200 OK\r\n";

    public HttpServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        new Thread(this::handleClients).start();
    }

    private void handleClients() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                handleClient(clientSocket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleClient(Socket clientSocket) throws IOException {
        HttpMessage httpMessage = new HttpMessage(clientSocket);
        try {
            String[] requestLine = httpMessage.getStartLine().split(" ");
            String requestTarget = requestLine[1];

            int questionMarkPos = requestTarget.indexOf('?');
            String fileTarget;
            String query = null;
            if (questionMarkPos != -1) {
                fileTarget = requestTarget.substring(0, questionMarkPos);
                query = requestTarget.substring(questionMarkPos + 1);
            } else {
                fileTarget = requestTarget;
            }
            if (controllers.containsKey(fileTarget)) {
                responseChooser(clientSocket, httpMessage, fileTarget);
            }
            else if (rootDirectory != null && Files.exists(rootDirectory.resolve(fileTarget.substring(1)))) {

                InputStream fileResource = getClass().getResourceAsStream(fileTarget);
                if (fileResource != null) {
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    fileResource.transferTo(buffer);
                    String responseText = buffer.toString();

                    String contentType = "text/plain";
                    if (requestTarget.endsWith(".html")) {
                        contentType = "text/html";
                    }
                    if (requestTarget.endsWith(".css")) {
                        contentType = "text/css";
                    }
                    contentResponse(clientSocket, responseText, contentType, statusOk);
                }

            } else {
                String htmlResponse = "<h1>Sorry! This page doesn't exist!</h1> " +
                        "<br>" + "<h3>Go to <a href='/index.html'>/index.html instead</a></h3>";
                contentResponse(clientSocket, htmlResponse, "text/html; charset=utf-8", "HTTP/1.1 404 Not found\r\n");
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void contentResponse(Socket clientSocket, String htmlResponse, String contentType, String status) throws IOException {
        String responseMessage = status +
                "Content-Type: " + contentType + "\r\n" +
                "Content-Length: " + htmlResponse.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                "Connection: close\r\n" + "\r\n" + htmlResponse;
        clientSocket.getOutputStream().write(responseMessage.getBytes());
    }

    private void responseChooser(Socket clientSocket, HttpMessage httpMessage, String fileTarget) throws SQLException, IOException {
        switch (fileTarget) {
            case QuestionPostController.PATH:
            case OptionPostController.PATH:
            case QuestionDeleteController.PATH:{
                HttpMessage response = controllers.get(fileTarget).handle(httpMessage);
                response.writeRedirect(clientSocket, "/addQuestion.html");
                break;
            }
            case SurveyPostController.PATH: {
                HttpMessage response = controllers.get(fileTarget).handle(httpMessage);
                response.writeRedirect(clientSocket, "/newSurvey.html");
                break;
            }
            case UserPostController.PATH: {
                HttpMessage response = controllers.get(fileTarget).handle(httpMessage);
                response.writeRedirect(clientSocket, "/newUser.html");
                break;
            }
            case UserUpdateController.PATH: {
                HttpMessage response = controllers.get(fileTarget).handle(httpMessage);
                response.writeRedirect(clientSocket, "/updateUser.html");
                break;
            }
            case QuestionUpdateController.PATH: {
                HttpMessage response = controllers.get(fileTarget).handle(httpMessage);
                response.writeRedirect(clientSocket, "/editQuestions.html");
                break;
            }
            case SurveyCompletePostController.PATH:
            case SurveyDeleteController.PATH: {
                HttpMessage response = controllers.get(fileTarget).handle(httpMessage);
                response.writeRedirect(clientSocket, "/takeSurvey.html");
                break;
            }
            default: {
                HttpMessage response = controllers.get(fileTarget).handle(httpMessage);
                response.write(clientSocket);
                break;
            }
        }
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public void setRoot(Path rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public void addController(String path, HttpController controller) {
        controllers.put(path, controller);
    }

}
