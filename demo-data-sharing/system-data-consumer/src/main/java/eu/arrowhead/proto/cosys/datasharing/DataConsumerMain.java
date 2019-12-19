package eu.arrowhead.proto.cosys.datasharing;

import eu.arrowhead.client.library.config.ApplicationInitListener;

// Java imports
import java.nio.charset.StandardCharsets;
import java.util.Dictionary;
import java.util.Hashtable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// Spring framework imports
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

// Arrowhead imports
import eu.arrowhead.client.library.ArrowheadService;


@Component
public class DataConsumerMain extends ApplicationInitListener {
    private int threshold;
    private Dictionary<String, String> inMemoryMap;

    @Autowired
    private ArrowheadService arrowheadService;


    private DataConsumerMain() {
        threshold = 1000;
        inMemoryMap = new Hashtable();

        // loading random data into the hashtable for testing
        String[] fruits = new String[] { "Orange", "Apple", "Pear", "Strawberry" };

        for (String fruit : fruits) {
            try {
               this.putIntoHashTable(this.hashKey(fruit), fruit);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
    }

    // Easier way to change hash function
    private String hashKey(String key) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashInBytes = md.digest(key.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hashInBytes.length; i++) {
            sb.append(Integer.toString((hashInBytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        String s = sb.toString();
        return s;
    }

    public int getTreshold() {
        return threshold;
    }

    public void setThreshold(int newThreshold) {
        threshold = newThreshold;
    }

    public String getValueFromKey(String key) {
        return inMemoryMap.get(key);
    }

    public void putIntoHashTable(String key, String value) {
        inMemoryMap.put(key, value);
    }

    public void registerInServiceRegistry() {

    }

    public static void main(final String[] args) {
        DataConsumerMain a = new DataConsumerMain();

        String orangeHash = "909cea0c97058cfe2e3ea8d675cb08e1";

        System.out.println(a.getValueFromKey(orangeHash));
    }
}
