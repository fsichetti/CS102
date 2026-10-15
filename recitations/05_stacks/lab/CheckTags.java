/*  Part 1: checks the tags of HTML files with your Tags.firstError.
 *      javac CheckTags.java
 *      java CheckTags data/page.html          one or more files
 */
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import tags.Tags;
import tags.Tags.Tag;

public class CheckTags {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("usage: java CheckTags file.html ...");
            return;
        }
        for (String file : args) {
            List<Tag> tags = Tags.parse(Files.readString(Path.of(file)));
            int i = Tags.firstError(tags);
            if (i == -1)
                System.out.println(file + ": balanced, " + tags.size() + " tags");
            else if (i == tags.size())
                System.out.println(file + ": end of file, some tags are never closed");
            else if (i >= 0 && i < tags.size())
                System.out.println(file + ": line " + tags.get(i).line() + ", unexpected " + tags.get(i));
            else
                System.out.println(file + ": firstError returned " + i);
        }
    }
}
