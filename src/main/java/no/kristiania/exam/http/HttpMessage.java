package no.kristiania.exam.http;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpMessage {
    private final Map<String, String> headerFields = new HashMap<>();
    private String startLine;
    private String messageBody;

    public HttpMessage(String startLine) {
        this.startLine = startLine;
    }

    public Map<String, String> getHeaderFields() {
        return headerFields;
    }

    public HttpMessage(Socket socket) throws IOException {
        this.startLine = HttpMessage.readLine(socket);

        String headerLine;
        while (!(headerLine = HttpMessage.readLine(socket)).isBlank()) {
            int colonPos = headerLine.indexOf(':');
            String headerField = headerLine.substring(0, colonPos);
            String headerValue = headerLine.substring(colonPos+1).trim();
            headerFields.put(headerField, headerValue);
        }
        if (headerFields.containsKey("Content-Length")) {
            messageBody = readBytes(socket, getContentLength());
        }
    }

    public HttpMessage(String startLine, String messageBody) {
        this.startLine = startLine;
        this.messageBody = messageBody;
    }

    public String getHeader(String headerName) {
        return headerFields.get(headerName);
    }

    public int getContentLength() {
        return Integer.parseInt(getHeader("Content-Length"));
    }


    static String readLine(Socket socket) throws IOException {
        StringBuilder buffer = new StringBuilder();
        int c;
        while ((c = socket.getInputStream().read()) != '\r') {
            buffer.append((char)c);
        }
        int expectedNewline = socket.getInputStream().read();
        assert expectedNewline == '\n';
        return buffer.toString();
    }

    String readBytes(Socket socket, int contentLength) throws IOException {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < contentLength; i++) {
            buffer.append((char)socket.getInputStream().read());
        }
        return buffer.toString();
    }

    public void write(Socket socket) throws IOException {
        String response = startLine + "\r\n" +
                "Content-Length: " + messageBody.length() + "\r\n" +
                "Connection: close\r\n" +
                "\r\n" +
                messageBody;
        socket.getOutputStream().write(response.getBytes());
    }

    public static Map<String, String> parseRequestParameters(String query) {
        Map<String, String> queryMap = new HashMap<>();
        for (String queryParameter : query.split("&")) {
            int equalsPos = queryParameter.indexOf('=');
            String parameterName = queryParameter.substring(0, equalsPos);
            String parameterValue = queryParameter.substring(equalsPos+1);
            queryMap.put(parameterName, parameterValue);
        }
        return queryMap;
    }

    public static Map<String, String> parseMultipleRequestParameters(String query) {
        Map<String, String> queryMap = new HashMap<>();
        int index = 0;
        for (String queryParameter : query.split("&")) {
            int equalsPos = queryParameter.indexOf('=');
            String parameterName = queryParameter.substring(0, equalsPos);
            if (parameterName.equals("questions")) {
                parameterName = parameterName + index;
            } else if (parameterName.equals("answers")) {
                parameterName = parameterName + index;
            }
            String parameterValue = queryParameter.substring(equalsPos+1);
            queryMap.put(parameterName, parameterValue);
            index++;
        }
        return queryMap;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public String getStartLine() {
        return startLine;
    }

    public void writeRedirect(Socket socket, String location) throws IOException {
        String response = "HTTP/1.1 303 See also\r\n" +
                "Location: " + location + "\r\n" +
                "Connection: close\r\n" +
                "\r\n";
        socket.getOutputStream().write(response.getBytes());
    }

}
