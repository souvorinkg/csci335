package learning.markov;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class MarkovTrans extends MarkovChain<String,Character> {

    public void countFrom(File languageFile, String language) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(languageFile));
        Optional<Character> prev = Optional.empty();
        for (;;) {
            int read = reader.read();
            if (read < 0) {
                break;
            } else {
                Optional<Character> next = usableCharacter((char)read);
                if (next.isPresent()) {
                    char c = next.get();
                    count(prev, language, c);
                    prev = next;
                }
            }
        }
    }

    public static Optional<Character> usableCharacter(char c) {
        c = Character.toLowerCase(c);

        if (c >= 'a' && c <= 'z') { // Restrict to English Latin alphabet
            return Optional.of(c);
        } else if (Character.isSpaceChar(c)) {
            return Optional.of(' ');
        } else {
            return Optional.empty();
        }
    }

    public static ArrayList<Character> usableCharacters(String input) {
        ArrayList<Character> result = new ArrayList<>();
        for (char c: input.toCharArray()) {
            usableCharacter(c).ifPresent(result::add);
        }
        return result;
    }
}
