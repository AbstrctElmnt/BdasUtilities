/**
 * The script  will parse output from ScriptRunner and create user friendly .csv file for you. Useful if you do not have access to DB.
 Requirement:  data.txt - hard coded name.
 Go to Script Console and use the statements below. Put output to the file data.txt, that should be placed to the same dir with script and run corresponding .bat file. Enjoy)

 //FYI: if item's name contains "," or "’" it will be replaced with space
 // for issue types
 import com.atlassian.jira.component.ComponentAccessor;
 import com.atlassian.jira.issue.issuetype.IssueType;
 List<String> issueTypes = new ArrayList<>();
 for (IssueType issueType : ComponentAccessor.getConstantsManager().getAllIssueTypeObjects())
 {
 issueTypes.add(issueType.getId() + "|" + issueType.getName().replaceAll(",", " ").replaceAll("’", " ")  + "|" + issueType.getType());
 }
 return issueTypes.each{};

 //for custom fields.
 import com.atlassian.jira.component.ComponentAccessor;
 import com.atlassian.jira.issue.fields.CustomField;
 List<String> cfs = new ArrayList<>();
 for (CustomField cf : ComponentAccessor.getCustomFieldManager().getCustomFieldObjects())
 {
 cfs.add(cf.getIdAsLong() + "|" + cf.getName().replaceAll(",", " ").replaceAll("’", " ") + "|" + cf.getCustomFieldType().getName());
 }
 return cfs.each{};

 //for workflow statuses
 import com.atlassian.jira.component.ComponentAccessor;
 import com.atlassian.jira.issue.status.Status;
 List<String> statuses = new ArrayList<>();
 for (Status status : ComponentAccessor.getConstantsManager().getStatuses())
 {
 statuses.add(status.getId() + "|" + status.getName().replaceAll(",", " ").replaceAll("’", " ") + "|" + status.getStatusCategory().getName() + " (" + status.getStatusCategory().getColorName()+ ")");
 }
 return statuses.each{};
 */

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FormatSRReport {
    private static String filename = "data";

    public static void main(String[] args) {
        String firstStr = "";
        String outPut = "test";
        if (args.length > 0) {

            if (args[0].equals("it")) {
                firstStr = "ID,NAME,TYPE";
                outPut = "IssueTypes";
            } else if (args[0].equals("cf")) {
                firstStr = "ID,NAME,TYPE";
                outPut = "CustomFields";
            } else if (args[0].equals("ws")) {
                firstStr = "ID,STATUS,CATEGORY(COLOR)";
                outPut = "WorklowStatuses";
            }
        }
        String pathToTheFile = fileCreation(outPut);
        writeData(pathToTheFile, firstStr, (ArrayList<String>) readData());
    }

    private static List<String> readData() {
        System.out.println("Read data...");
        List<String> it = new ArrayList<>();
        InputStream input = FormatSRReport.class.getClassLoader().getResourceAsStream(filename + ".txt");
        Scanner scanner = new Scanner(input);
        StringBuilder builder = new StringBuilder(scanner.nextLine().replace("[", " ").replace("]", ""));
        String[] result = builder.toString().split(",");

        for (String s : result)
        {
            it.add(s.replace("|", ",").trim());
        }
        return it;
    }

    private static String fileCreation(String filename) {
        Path path;
        File file = null;
        try {
            path = Paths.get(FormatSRReport.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            file = new File(path.toString(), filename + ".csv");
            if (file.exists()) {
                file.delete();
                file.createNewFile();
            } else file.createNewFile();

        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    private static void writeData(String path, String firstStr, ArrayList<String> list) {
        System.out.println("Writing data...");
        PrintWriter fw = null;
        try {
            fw = new PrintWriter(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        fw.print('\ufeff');
        fw.print(firstStr);
        fw.print("\n");

        for (String s : list) {
            fw.print(s);
            fw.print("\n");
        }
        fw.close();
    }
}
