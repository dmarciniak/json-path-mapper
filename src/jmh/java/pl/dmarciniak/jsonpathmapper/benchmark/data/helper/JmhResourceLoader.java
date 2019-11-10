package pl.dmarciniak.jsonpathmapper.benchmark.data.helper;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import java.io.IOException;
import java.net.URL;

public class JmhResourceLoader {

    public static String load(String fullPath) {
        try {
            URL url = Resources.getResource(fullPath);
            return Resources.toString(url, Charsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot load given resource: " + fullPath, e);
        }
    }
}
