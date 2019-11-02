package pl.dmarciniak.jsonpathmapper.test.helper;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ResourceLoader {

    public static String load(String fullPath) {
        try {
            return Files.readString(Paths.get(ResourceLoader.class.getClassLoader().getResource(fullPath).toURI()));
        } catch (IOException | URISyntaxException | NullPointerException e) {
            throw new IllegalArgumentException("Cannot load given resource: " + fullPath, e);
        }
    }
}
