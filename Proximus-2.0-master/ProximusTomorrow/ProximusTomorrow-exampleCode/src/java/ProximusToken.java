import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * @Copyright 2012, Proximus Mobility, LLC., all rights reserved
 * @author dshaw
 *
 * Note that this class uses the Apache Commons Codec Library
 * Available here:  http://commons.apache.org/codec/
 */
public class ProximusToken {
     
    private String myCompanySalt = null;
    
    public ProximusToken(String myCompanySalt)
    {
        this.myCompanySalt = myCompanySalt;
    }
    
    public String generateAuthenticationToken(String url)
    {
        return DigestUtils.md5Hex(this.myCompanySalt + url).toUpperCase();
    }
    
    public static void main(String[] args) throws UnsupportedEncodingException
    {
        ProximusToken tokenGenerator = new ProximusToken("MYCOMPANYISAWESOME");
        
        String URL_BASE = "http://devices.proximusmobility.com/api/";
        String username = URLEncoder.encode("user@domain.com", "UTF-8");
        String password = "iwadasnin2012wisawuts";
        
        String encodingUrl = URL_BASE + username + "/" + password + "/params1/params2/params..n";
        String token = tokenGenerator.generateAuthenticationToken(encodingUrl);
        String requestUrl = URL_BASE + username + "/" + token + "/params1/params2/params..n";
            
        System.out.println("Raw URL was " + encodingUrl);
        System.out.println("Token would be " + token);
        System.out.println("Request URL would be " + requestUrl);
    }
}
