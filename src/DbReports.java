import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

/**
 *
 Compile source file and create config.properties file in the same dir. File config.properties contains sql queries, db username, db password and connection properties. Feel free to modify it in case of any changes on prod.
 Output of you sql query should have 3 columns only!

 SQL:
 issueTypes=select ID, PNAME, decode (PSTYLE, null, 'issue', 'jira_subtask', 'sub-task') from jira.issuetype where PNAME != 'Old_Risk' order by PSTYLE, PNAME
 statuses=select ID, PNAME, decode(STATUSCATEGORY, 2, 'To Do', 4, 'In progress', 3, 'Done')||'('||decode(STATUSCATEGORY, 2, 'Grey', 4, 'Yellow', 3, 'Green')||')' from jira.ISSUESTATUS order by PNAME
 cfs=select ID, CFNAME, replace(substr(CUSTOMFIELDTYPEKEY, instr(CUSTOMFIELDTYPEKEY,':')),':','') from jira.CUSTOMFIELD order by CFNAME
 msqCon=
 euCon=
 pctCon=
 userProd=
 passwordProd=

 Requirements:
 Check if JAVA variable is set in your system. If no, set it.  Copy oracle driver to your JAVA virtual machine. Note: if you have JDK installed (JDK package has its own JRE) and JRE that is installed separately, copy driver to both virtual machines (to /lib/ext/ folders).

 Usage:
 java DbReports jm it //JIRA MSQ - Issue Types.
 java DbReports jm ws //JIRA MSQ - Workflow Statuses.
 java DbReports jm cf //JIRA MSQ - Custom fields.
 je - JIRA EU
 jp - JIRA PCT

 */

public class DbReports {
    private static String userProd;
    private static String passwordProd;
    private static String p1;
    private static String p2;
    private static String con;
    private static String sql;
    private static String firstStr;
    private static String reportName;

    public static void main(String[] args) throws InterruptedException {
        p1 = args[0];
        p2 = args[1];
        loadProperties();
        String pathToTheFile = fileOut(reportName);
        writeOutput(pathToTheFile, firstStr, runSql(connect(con), sql));
        System.out.println("Done!");
    }

    private static void loadProperties(){
        Properties prop = new Properties();
        String filename = "config.properties";
        InputStream input = DbReports.class.getClassLoader().getResourceAsStream(filename);
        if (input == null) {
            try {
                throw new FileNotFoundException();
            } catch (FileNotFoundException e) {
                System.out.println("Sorry, unable to find " + filename);
                e.printStackTrace();
            }
        }
        try {
            prop.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        userProd = prop.getProperty("userProd");
        passwordProd = prop.getProperty("passwordProd");

        if (p1.equals("jm") && p2.equals("it"))
        {
            con = prop.getProperty("msqCon");
            sql = prop.getProperty("issueTypes");
            firstStr = "id, Issue Type, Type";
            reportName = "JIRAMSQ-IssueTypes";
        }
        else if (p1.equals("jm") && p2.equals("ws"))
        {
            con = prop.getProperty("msqCon");
            sql = prop.getProperty("statuses");
            firstStr = "id, Status, Category and Colour";
            reportName = "JIRAMSQ-Statuses";
        }
        else if (p1.equals("jm") && p2.equals("cf"))
        {
            con = prop.getProperty("msqCon");
            sql = prop.getProperty("cfs");
            firstStr = "id, Custom Field, Type";
            reportName = "JIRAMSQ-CustomFields";
        }
        else if (p1.equals("je") && p2.equals("it"))
        {
            con = prop.getProperty("euCon");
            sql = prop.getProperty("issueTypes");
            firstStr = "id, Issue Type, Type";
            reportName = "JIRAEU-IssueTypes";

        }
        else if (p1.equals("je") && p2.equals("ws"))
        {
            con = prop.getProperty("euCon");
            sql = prop.getProperty("statuses");
            firstStr = "id, Status, Category and Colour";
            reportName = "JIRAEU-Statuses";
        }
        else if (p1.equals("je") && p2.equals("cf"))
        {
            con = prop.getProperty("euCon");
            sql = prop.getProperty("cfs");
            firstStr = "id, Custom Field, Type";
            reportName = "JIRAEU-CustomFields";
        }
        else if (p1.equals("jp") && p2.equals("it"))
        {
            con = prop.getProperty("pctCon");
            sql = prop.getProperty("issueTypes");
            firstStr = "id, Issue Type, Type";
            reportName = "JIRAPCT-IssueTypes";

        }
        else if (p1.equals("jp") && p2.equals("ws"))
        {
            con = prop.getProperty("pctCon");
            sql = prop.getProperty("statuses");
            firstStr = "id, Status, Category and Colour";
            reportName = "JIRAPCT-Statuses";
        }
        else if (p1.equals("jp") && p2.equals("cf"))
        {
            con = prop.getProperty("pctCon");
            sql = prop.getProperty("cfs");
            firstStr = "id, Custom Field, Type";
            reportName = "JIRAPCT-CustomFields";
        }

    }

    private static Connection connect(String con) {
        try
        {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("JDBC Driver is not installed!");
            e.printStackTrace();
        }
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(con, userProd, passwordProd);
        }
        catch (SQLException e)
        {
            System.out.println("Connection Failed!");
            e.printStackTrace();
        }
        if (connection != null) System.out.println("Connection to DB has been established!");
        return connection;
    }

    private static ArrayList<String> runSql(Connection connection, String sql) {
        Statement statement = null;
        ResultSet result = null;
        try
        {
            statement = connection.createStatement();
            result = statement.executeQuery(sql);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        String output = null;
        ArrayList<String> list = new ArrayList<>();
        System.out.println("Report creation...");
        try
        {
            while (result.next())
            {
                output = result.getString(1)+","+result.getString(2).replace(",", " ")+","+result.getString(3);
                list.add(output);

            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return list;
    }

    private static String fileOut(String filename) {
        Path path = null;
        try {
            path = Paths.get(DbReports.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        File file = new File(path.toString(), filename + ".csv");
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

    static void writeOutput(String path, String firstStr, ArrayList<String> list) {

        System.out.println("Writing data...");
        PrintWriter fw = null;
        try {
            fw = new PrintWriter(path, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
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


