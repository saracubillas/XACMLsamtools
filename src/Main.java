import org.w3c.dom.Document;
import org.wso2.balana.Balana;
import org.wso2.balana.ConfigurationStore;
import org.wso2.balana.PDP;
import org.wso2.balana.PDPConfig;
import org.wso2.balana.finder.impl.FileBasedPolicyFinderModule;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    private static Balana balana;

    private static String response;

    public static void main(String[] args) {

        if (args.length < 3) {
            System.out.println("Usage: <config_file> <request_file> <policy_directory>");
            System.exit(1);
        }

        String config_path = args[0];
        String request_path = args[1];
        String policy_path = args[2];   // folder with policies

        initBalana(config_path, policy_path);

        try {

            byte[] encoded = Files.readAllBytes(Paths.get(request_path));
            String request = new String(encoded, StandardCharsets.UTF_8);

            PDP pdp = getPDPNewInstance();

            response = pdp.evaluate(request);

            System.out.println("\n======================== XACML Response ===================");
            System.out.println(response);
            System.out.println("===========================================================");

            readXML(response);

        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
        }

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

    private static void readXML(String response) {
        try {

            Document doc = loadXMLFromString(response);
            doc.getDocumentElement().normalize();
            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            System.out.println("Decision :" + doc.getElementsByTagName("Decision").item(0).getTextContent());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Document loadXMLFromString(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }


}
