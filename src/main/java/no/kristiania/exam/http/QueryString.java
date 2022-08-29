package no.kristiania.exam.http;

import java.util.LinkedHashMap;
import java.util.Map;

public class QueryString {
    private final Map<String, String> parameters = new LinkedHashMap<>();

    public QueryString(String queryString) {
        for (String parameter : queryString.split("&")) {
            int equalsPos = parameter.indexOf('=');
            String key = parameter.substring(0, equalsPos);
            String value = parameter.substring(equalsPos+1);
            this.parameters.put(key, value);
        }
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }
}
