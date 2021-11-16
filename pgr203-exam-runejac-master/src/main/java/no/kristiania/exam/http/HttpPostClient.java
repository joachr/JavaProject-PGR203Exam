package no.kristiania.exam.http;

import java.io.IOException;
import java.net.Socket;

public class HttpPostClient {

    private final HttpMessage httpMessage;
    private final int statusCode;

    public HttpPostClient(String host, int port, String requestTarget, String messageBody) throws IOException {
        Socket socket = new Socket(host, port);

        String request = "GET " + requestTarget + " HTTP/1.1\r\n" +
                "Host: " + host + "\r\n" +
                "Content-Length: " + messageBody.length() + "\r\n" +
                "Connection: close\r\n" +
                "\r\n" +
                messageBody;
        socket.getOutputStream().write(request.getBytes());

        httpMessage = new HttpMessage(socket);
        this.statusCode = Integer.parseInt(httpMessage.getStartLine().split(" ")[1]);
    }

    public HttpPostClient(String host, int port, String requestTarget, final String httpMethod, String messageBody) throws IOException {
        Socket socket = new Socket(host, port);

        String request = httpMethod + " " + requestTarget + " HTTP/1.1\r\n" +
                "Host: " + host + "\r\n" +
                "Content-Length: " + messageBody.length() + "\r\n" +
                "Connection: close\r\n" +
                "\r\n" +
                messageBody;
        socket.getOutputStream().write(request.getBytes());

        httpMessage = new HttpMessage(socket);
        this.statusCode = Integer.parseInt(httpMessage.getStartLine().split(" ")[1]);
    }




    public int getResponseCode() {
        return statusCode;
    }
}
