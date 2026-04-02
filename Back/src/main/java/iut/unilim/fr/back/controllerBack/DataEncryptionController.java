package iut.unilim.fr.back.controllerBack;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class DataEncryptionController {

    public static String encodeData(String userName, String tag) throws NoSuchAlgorithmException {
        StringBuilder sb = new StringBuilder();
        String mdp=userName+tag;
        MessageDigest md=MessageDigest.getInstance("SHA-256");
        md.update(mdp.getBytes(StandardCharsets.UTF_8));
        byte[] digest=md.digest();

        String hash= Base64.getEncoder().encodeToString(digest);

        int hashMaxLength = 15;
        for(int indexHash = 0; indexHash< hashMaxLength; indexHash++){
            sb.append(hash.charAt(indexHash));
        }

        return sb.toString();
    }
}
