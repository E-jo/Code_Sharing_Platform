package blockchain;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * The class is used to work with private and public keys through java.security
 */
public class Cryptography {

    /**
     * Sign data with a private key from the file
     *
     * @param  data     The data to sign
     * @param  filename The name of the file with the private key
     * @return          Signed data
     */
    public static byte[] sign(String data, String filename) {
        try {
            Signature rsa = Signature.getInstance("SHA1withRSA");
            rsa.initSign(getPrivate(filename));
            rsa.update(data.getBytes());
            return rsa.sign();
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            return null;
        }
    }

    /**
     * Take the private key from the file
     *
     * @param  filename The name of the file with the private key
     * @return          Private key
     */
    private static PrivateKey getPrivate(String filename) {
        try {
            byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(spec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
            return null;
        }
    }

    /**
     * Take the public key from the file
     *
     * @param  filename The name of the file with the public key
     * @return          public key
     */
    public static PublicKey getPublic(String filename) {
        try {
            byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(spec);
        } catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException e) {
            return null;
        }
    }

    /**
     * Verify that data is valid using its signature and the public key
     *
     * @param  data      The data to verify
     * @param  signature The signature of this data
     * @param  publicKey The public key to verify
     * @return           True if the data is valid
     */
    public static boolean verifySignature(byte[] data, byte[] signature, PublicKey publicKey) {
        try {
            Signature sig = Signature.getInstance("SHA1withRSA");
            sig.initVerify(publicKey);
            sig.update(data);
            return sig.verify(signature);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            return false;
        }
    }
}