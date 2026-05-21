package dk.rsyd.jap.dashboard.nomad;

import jakarta.inject.Singleton;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Singleton
public class LogScaleUrlBuilder {

    private static final String BASE_URL =
        "https://logs.rsyd.net/nomad-jap/search" +
        "?columns=%5B%7B%22type%22%3A%22field%22%2C%22fieldName%22%3A%22%40timestamp%22%2C%22format%22%3A%22datetime%22%2C%22width%22%3A143%7D%2C%7B%22type%22%3A%22field%22%2C%22fieldName%22%3A%22nomad_job%22%2C%22format%22%3A%22text%22%2C%22width%22%3A308%7D%2C%7B%22type%22%3A%22field%22%2C%22fieldName%22%3A%22message%22%2C%22format%22%3A%22logline%22%2C%22width%22%3A2684%7D%2C%7B%22type%22%3A%22field%22%2C%22fieldName%22%3A%22%22%2C%22format%22%3A%22text%22%7D%5D" +
        "&live=false" +
        "&newestAtBottom=false" +
        "&showOnlyFirstLine=false" +
        "&start=24h" +
        "&tz=Europe%2FCopenhagen" +
        "&widgetType=list-view";

    public static String buildLogsUrl(String nomadJob) {
        String queryValue = "\"nomad_job\" = \"" + nomadJob + "\"\n" +
                            "| !health\n" +
                            "| !DEBUG";

        String encodedQuery = URLEncoder.encode(queryValue, StandardCharsets.UTF_8);

        return BASE_URL + "&query=" + encodedQuery;
    }

    public static String buildLogsUrlWithImage(String nomadJob, String image) {
        String queryValue = "\"nomad_job\" = \"" + nomadJob + "\"\n" +
            "| image = \"" + image + "\"\n" +
            "| !health\n" +
            "| !DEBUG";

        String encodedQuery = URLEncoder.encode(queryValue, StandardCharsets.UTF_8);

        return BASE_URL + "&query=" + encodedQuery;
    }
}
