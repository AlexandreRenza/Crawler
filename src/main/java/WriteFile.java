import static java.nio.file.StandardOpenOption.*;
import java.nio.file.*;
import java.io.*;

public class WriteFile {


    public void generateTxt(String txt){

        String s = txt;
        byte data[] = s.getBytes();
        Path p = Paths.get("./cochrane_reviews.txt");

        try (OutputStream out = new BufferedOutputStream(
                Files.newOutputStream(p, CREATE, APPEND))) {
            out.write(data, 0, data.length);
        } catch (IOException x) {
            System.err.println(x);
        }
    }

    public void deleteTxt(){

        Path p = Paths.get("./cochrane_reviews.txt");

        //Delete if file exists
        File temp = new File(String.valueOf(p));
        if(temp.exists()){temp.delete();}

    }




}
