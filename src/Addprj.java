import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;


/**
 * Use this script only for newly created projects. Due to the bug in JIRA it was not possible to use api methods for updating roles, only override them.
 *
 * Create config.properties file in the same dir with the following lines:
 * user=bdas_admin
 * password=
 * password=**********
 */

public class Addprj {
    private final static String BaseJiraMSQ = "";
    private final static String BaseJiraEU = "";
    private final static String BaseJiraPCT = "";
    private final static String restApiProject = "/rest/api/latest/project/";

    public static void main(String[] args) throws IOException {

        System.out.println("usage: msq epmbdas epm-bdas");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Select JIRA instance(eu, msq, pct):");
        String jira = scanner.nextLine().toUpperCase();
        System.out.println("Enter JIRA project key:");
        String key = scanner.nextLine().toUpperCase();
        System.out.println("Enter UPSA project code:");
        String project = scanner.nextLine().toUpperCase();
        add(selectJIRA(jira, key), selectPrj(project));
    }

    private static String auth() {
        Properties prop = new Properties();
        String filename = "config.properties";
        InputStream input;
        input = Addprj.class.getClassLoader().getResourceAsStream(filename);
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

        String user = prop.getProperty("user");
        String password = prop.getProperty("password");
        String userCredentials = user + ":" + password;
        String basicAuth;
        basicAuth = "Basic " + new Base64().encode(userCredentials.getBytes()).toString();
        return basicAuth;
    }

    private static List<String> selectJIRA(String jira, String key) {
        List<String> urls = new ArrayList<>();
        if (jira.equals("MSQ"))
        {
            urls.add(BaseJiraMSQ+restApiProject+key+"/role/10400");
            urls.add(BaseJiraMSQ+restApiProject+key+"/role/10401");
            urls.add(BaseJiraMSQ+restApiProject+key+"/role/10402");
            urls.add(BaseJiraMSQ+restApiProject+key+"/role/10403");
            urls.add(BaseJiraMSQ+restApiProject+key+"/role/10500");
            urls.add(BaseJiraMSQ+restApiProject+key+"/role/10404");
            urls.add(BaseJiraMSQ+restApiProject+key+"/role/10405");
            System.out.println("Check: "+BaseJiraMSQ+"/plugins/servlet/project-config/"+key+"/roles");
        }
        else if (jira.equals("EU"))
        {
            urls.add(BaseJiraEU+restApiProject+key+"/role/10400");
            urls.add(BaseJiraEU+restApiProject+key+"/role/10401");
            urls.add(BaseJiraEU+restApiProject+key+"/role/10402");
            urls.add(BaseJiraEU+restApiProject+key+"/role/10403");
            urls.add(BaseJiraEU+restApiProject+key+"/role/10500");
            urls.add(BaseJiraEU+restApiProject+key+"/role/10404");
            urls.add(BaseJiraEU+restApiProject+key+"/role/10405");
            System.out.println("Check: "+BaseJiraEU+"/plugins/servlet/project-config/"+key+"/roles");
        }

        else if (jira.equals("PCT")) {
            urls.add(BaseJiraPCT+restApiProject+key+"/role/10400");
            urls.add(BaseJiraPCT+restApiProject+key+"/role/10401");
            urls.add(BaseJiraPCT+restApiProject+key+"/role/10402");
            urls.add(BaseJiraPCT+restApiProject+key+"/role/10403");
            urls.add(BaseJiraPCT+restApiProject+key+"/role/10500");
            urls.add(BaseJiraPCT+restApiProject+key+"/role/10404");
            urls.add(BaseJiraPCT+restApiProject+key+"/role/10405");
            System.out.println("Check: "+BaseJiraPCT+"/plugins/servlet/project-config/"+key+"/roles");
        }

        return urls;
    }

    private static List<String> selectPrj(String projectCode){
        List<String> groups = new ArrayList<>();
        groups.add("prj_"+projectCode+"-administrators");
        groups.add("prj_"+projectCode+"-developers");
        groups.add("prj_"+projectCode+"-external-users");
        groups.add("prj_"+projectCode+"-members");
        groups.add("prj_"+projectCode+"-team-leaders");
        groups.add("prj_"+projectCode+"-testers");
        groups.add("prj_"+projectCode+"-users");
        return groups;
    }

    private static void add(List<String> urls, List<String> groups) throws IOException {
        int countAddedGroups = 0;
        for (String jiraUrl : urls) {
            URL url = new URL(jiraUrl);
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestProperty("Authorization", auth());
            httpCon.setRequestProperty("Content-Type", "Application/json");
            httpCon.setRequestMethod("PUT");
            OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());

            //adds special user. Uncomment if required.
            //
            //if (countAddedGroups == 6) {
            //out.write("{\"categorisedActors\":{\"atlassian-user-role-actor\":[\"jira_pmctj_sync\"], \"atlassian-group-role-actor\":[\"" + groups.get(countAddedGroups) + "\"]}}");
            //out.write("{\"categorisedActors\":{\"atlassian-group-role-actor\":[\"" + groups.get(countAddedGroups) + "\"]}}");
            //out.write("{\"categorisedActors\":{\"atlassian-user-role-actor\":[\"jira_pmctj_sync\"]}}");
            //}
            // else {
            out.write("{\"categorisedActors\":{\"atlassian-group-role-actor\":[\"" + groups.get(countAddedGroups) + "\"]}}");

            // }
            countAddedGroups++;
            out.close();
            httpCon.getInputStream();
        }

    }
}
