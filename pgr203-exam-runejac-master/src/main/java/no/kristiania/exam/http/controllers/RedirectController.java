package no.kristiania.exam.http.controllers;

import no.kristiania.exam.http.HttpMessage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

public class RedirectController implements HttpController {

    public final String PATH;
    private final String body;

    public RedirectController(String PATH, String body) {
        this.PATH = PATH;
        this.body = body;
    }

    @Override
    public HttpMessage handle(HttpMessage request) throws SQLException, IOException {
        HttpMessage httpMessage = new HttpMessage("HTTP/1.1 302 Redirect",
                Files.readString(Path.of("src/main/resources/" + body)));
        httpMessage.getHeaderFields().put("Location", "http://localhost:9090/" + PATH);
        return httpMessage;
    }
}
