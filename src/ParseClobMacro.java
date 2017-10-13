import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


/**
* parses CLOB data from bodycontent row, that was retrieved from Confluence database in order to find macros that were used on pages. Generates a list of macros. Was tested on xml export.
* select body from confluence.bodycontent where CONTENTID in ( );
*/

public class ParseClobMacro {
    private static String target = "ac:structured-macro ac:macro-id=";

    public static void main(String[] args) throws IOException {
        System.out.println("Path to the file?");
        Scanner scanner = new Scanner(System.in);
        String source = scanner.nextLine();
        Set<String> set = parse(target, source);
        writeOutput(fileOut("output"), set);
    }

    private static Set parse(String target, String source) throws IOException {
        Set<String> set = new HashSet<>();
        List<String> lines=Files.readAllLines(Paths.get(source), Charset.forName("UTF-8"));
        boolean isMacro = false;
        int macroLenth = 120;
        String result = "";
        for(String line:lines){
            if (line.contains(target))
            {
                int index = line.indexOf(target);
                isMacro = true;
                while (isMacro) {
                    if (line.length() > index + macroLenth) {
                        result = line.substring(index, index + macroLenth);
                        result = result.substring(80);
                        if (result.indexOf(34) > -1) {
                            result = result.substring(0,result.indexOf(34));
                        }
                        set.add(result);
                        line = line.substring(index+macroLenth);
                        isMacro = line.contains(target);
                        index = line.indexOf(target);
                    }
                    else {
                        result = line;
                        set.add(result);
                        isMacro = false;
                    }
                }
            }
        }

        return set;
    }

    private static String fileOut(String filename) {
        Path path = null;
        try {
            path = Paths.get(ParseClobMacro.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        File file = new File(path.toString(), filename + ".txt");
        if (!file.exists())
        {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (file.exists())
        {
            try {
                file.delete();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file.getAbsolutePath();
    }
    private static void writeOutput(String path, Set<String> list) {

        System.out.println("Writing data...");
        PrintWriter fw = null;
        try {
            fw = new PrintWriter(path, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        for (String s : list) {
            fw.print(s);
            fw.println();
        }
        fw.close();
    }
}