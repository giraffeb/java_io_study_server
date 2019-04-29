package server.impl.repository;

import server.impl.vo.chat.ChatUser;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


/**
 * 로그인용으로 사용할 토큰.
 */
public class TokenRepository {

    private KeyPair keyPair;
    private Cipher cipher;


    public void init(){
        try {
            this.keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
            this.cipher = Cipher.getInstance("RSA");

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

    }

    public TokenRepository() {
        init();
        System.out.println("TOKEN INIT()");
        System.out.println(this.keyPair);
        System.out.println(this.cipher);
    }

    public String generateToken(ChatUser chatUser){
        String time = LocalDateTime.now().toString();
        String body = chatUser.getId()+","+time;
        byte[] signature = null;
        String token = null;

        try {
            this.cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPrivate());

            signature = this.cipher.doFinal(body.getBytes());

            token = Base64.getEncoder().encodeToString(body.getBytes())+"."+Base64.getEncoder().encodeToString(signature);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return token;
    }


    public Map<String, String> parseToken(String token){
        Map<String, String> resultMap = new HashMap<>();

        String time = LocalDateTime.now().toString();

        String body = null;
        String temp_signature = null;
        byte[] encoded_signature = null;
        String signature = null;

        Cipher cipher = null;
        KeyPair keyPair = null;

        try {
            body = token.split("\\.")[0];
            temp_signature = token.split("\\.")[1];

            body = new String(Base64.getDecoder().decode(body));
            encoded_signature = Base64.getDecoder().decode(temp_signature);

            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPublic());
            signature = new String(cipher.doFinal(encoded_signature));

            resultMap.put("body", body);
            resultMap.put("signature", signature);

        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return resultMap;
    }

    public boolean verifyToken(Map<String, String> resultMap){
        boolean flag = false;
        String body = resultMap.get("body");
        String signature = resultMap.get("signature");

        if(body.equals(signature)){
            flag = true;
        }

        //TODO: expire Time구현하기.

        return flag;
    }

    public boolean verify(String token){
        boolean flag  = false;

        Map<String, String> resultMap = parseToken(token);
        flag = verifyToken(resultMap);

        return flag;
    }

}
