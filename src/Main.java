import org.w3c.dom.Document;
import org.wso2.balana.Balana;
import org.wso2.balana.ConfigurationStore;
import org.wso2.balana.PDP;
import org.wso2.balana.PDPConfig;
import org.wso2.balana.finder.impl.FileBasedPolicyFinderModule;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {
    private static Balana balana;

    private static String response;

    native void view();

    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("Usage: <request_file> <sam_file>");
            /*System.exit(1);*/
        }

        String config_path = "config/config_rbac.xml";
        String policy_path = "policy";
/*
        String request_path = args[0];
        String sam_file = args[1]; */

        String request_path = "requests/XACMLRequest5.xml";
        String sam_file = "toy.sam";

        initBalana(config_path, policy_path);

        try {

            byte[] encoded = Files.readAllBytes(Paths.get(request_path));
            String request = new String(encoded, StandardCharsets.UTF_8);

            PDP pdp = getPDPNewInstance();

            response = pdp.evaluate(request);

            System.out.println("\n======================== XACML Response ===================");
            System.out.println(response);
            System.out.println("===========================================================");


            if (readXML(response).equals("Permit")){
                System.out.println("\n=== OK");
                viewSAM(sam_file);

            } else {
                System.out.println("Permision denied");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
        }

    }

    static public void viewSAM(String args) {
        Main obj = new Main();


        //in mac oxs
        String command = "samtools view -bS /Users/sara/Documents/MIRI/thesis/samtools-1.3.1/examples/toy.sam";

        //in windows
        //String command = "ping -n 3 " + domainName;

        String output = obj.executeCommand(command);

        System.out.println(output);
        System.out.println("\n========OUTPUT======");
    }



    private String executeCommand(String command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            System.out.println("\n========executing======");
            p.waitFor();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("\n========error======");
        }

        return output.toString();

    }
    private static void initBalana(String config_path, String policy_path) {

        System.setProperty(ConfigurationStore.PDP_CONFIG_PROPERTY, config_path);

        System.setProperty(FileBasedPolicyFinderModule.POLICY_DIR_PROPERTY, policy_path);

        balana = Balana.getInstance();
    }

    private static PDP getPDPNewInstance() {

        PDPConfig pdpConfig = balana.getPdpConfig();

        return new PDP(pdpConfig);
    }


    private static String readXML(String response) {
        String decision = "";
        try {

            Document doc = loadXMLFromString(response);
            doc.getDocumentElement().normalize();
            decision = doc.getElementsByTagName("Decision").item(0).getTextContent();
            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            System.out.println("Decision :" + decision);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return decision;
    }

    public static Document loadXMLFromString(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }


}
