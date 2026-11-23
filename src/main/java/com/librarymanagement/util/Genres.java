package com.librarymanagement.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Predefined genres for books in the library system.
 * Contains 10 standard genres to ensure consistency and prevent user errors.
 */
public class Genres {
    public static final List<String> GENRE_LIST = new ArrayList<>(Arrays.asList(
            "Fiction",
            "Science Fiction",
            "History",
            "Technology",
            "Science",
            "Literature",
            "Mystery",
            "Biography",
            "Self-Help",
            "Educational"
    ));

    /**
     * Get all available genres
     */
    public static List<String> getAllGenres() {
        return new ArrayList<>(GENRE_LIST);
    }

    /**
     * Check if a genre exists in the list
     */
    public static boolean isValidGenre(String genre) {
        return GENRE_LIST.contains(genre);
    }

    /**
     * Get genre count
     */
    public static int getGenreCount() {
        return GENRE_LIST.size();
    }
}
