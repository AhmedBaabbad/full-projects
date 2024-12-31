package net.javaguides.springboot.util;

public class ApiUtils {

    public static String getUrl(String postTitle) {
        // OOPS Concepts Explained in Java
        // oops-concepts-explained-in-java
        if (postTitle == null || postTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("Post title cannot be null or empty");
        }
        String title = postTitle.trim().toLowerCase();
        String url = title.replaceAll("\\s+", "-");
        url = url.replaceAll("[^A-Za-z0-9]", "-");
        return url;
    }
}
