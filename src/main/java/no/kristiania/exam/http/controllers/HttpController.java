package no.kristiania.exam.http.controllers;

import no.kristiania.exam.http.HttpMessage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

public interface HttpController {
    HttpMessage handle(HttpMessage request) throws SQLException, UnsupportedEncodingException, IOException;
}
