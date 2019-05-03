import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import server.impl.repository.TokenRepository;
import server.impl.vo.chat.ChatRoom;
import server.impl.vo.chat.ChatUser;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReflectionTest {

    @Test
    public void classtypeTest(){

        Object obj = new Object();
        Class<Object> cobj = Object.class;
        System.out.println("type : "+ Object.class);
        System.out.println("instance : "+ obj);
        System.out.println("instance : "+ cobj);
    }


    @Test
    public void file(){
        File file = new File("./hello.db");
        System.out.println(file.exists());

//        try {
//            FileOutputStream fos = new FileOutputStream(file.getPath());
//            fos.write(new String("Hello").getBytes());
//            fos.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


    @Test
    public void classloaderTest() throws IOException {
        int t = LocalDateTime.now().hashCode();
        int o = new Object().hashCode();
        int o2 = new Object().hashCode();
        System.out.println(t);
        System.out.println(o);
        System.out.println(o2);
        System.out.println(t+o);


    }


    @Test
    public void chatRoomTest(){
        ChatRoom cr = new ChatRoom()
                .setChatRoomId(1)
                .setChatRoomTitle("Hello new world");
        System.out.println(cr);
    }


    @Test
    public void rsaTest() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        String rawdata = "Hello new world";

        KeyPair keys = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        Cipher cipher = Cipher.getInstance("RSA");

        cipher.init(Cipher.ENCRYPT_MODE, keys.getPrivate());
        byte[] encodedData = cipher.doFinal(rawdata.getBytes());

        cipher.init(Cipher.DECRYPT_MODE, keys.getPublic());
        byte[] decodedData = cipher.doFinal(encodedData);

        System.out.println(new String(decodedData));

    }


    @Test
    public void tokenTest(){

        ChatUser chatUser = new ChatUser().setId("Hello").setPw("NoName");

        Cipher cipher;
        KeyPair keyPair;

        try {
            LocalDateTime localDateTime = LocalDateTime.now();

            cipher = Cipher.getInstance("RSA");
            keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();

            //generatetoken
            //generate token
            String body = chatUser.getId()+","+localDateTime.toString();
            System.out.println("body-> "+body);

            //generate signature
            cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPrivate());
            byte[] signature = cipher.doFinal(body.getBytes());
            System.out.println("signature-> "+signature);

            //base64 encoded
            String base64Body = Base64.getEncoder().encodeToString(body.getBytes());
            String base64signature = Base64.getEncoder().encodeToString(signature);

            //body + signature
            String token = base64Body+"."+base64signature;
            System.out.println("token-> "+token);


            //decode
            String[] strarr = token.split(".");
            System.out.println(strarr.length);
            String parseBody = token.split("\\.")[0];
            String parseSignature = token.split("\\.")[1];

            String reBody = new String(Base64.getDecoder().decode(parseBody));
            byte[] reSignature = Base64.getDecoder().decode(parseSignature);

            System.out.println("reBody-> "+reBody);
            System.out.println("reSignature-> "+reSignature);

            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPublic());
            byte[] decodedSignature = cipher.doFinal(reSignature);
            System.out.println("decodedSignature-> "+new String(decodedSignature));


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }


    }

    @Test
    public void tokenTest2(){
        TokenRepository tokenRepository = new TokenRepository();
        String token = tokenRepository.generateToken(new ChatUser().setId("Hello"));
        System.out.println(token);
    }
}
