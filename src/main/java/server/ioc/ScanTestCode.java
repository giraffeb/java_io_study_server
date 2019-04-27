package server.ioc;

import server.interfaces.Bean;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * 의존성 주입을 위한 리플렉션 학습코드
 * 패키지내의 파일명(ex server.imple.xxxx)를 얻고
 * 해당 클래스를 로드하면서 어노테이션 여부를 체크함
 * 어노테이션이 있는 경우, 인스턴스를 생성하고
 * 의존성을 주입해주려고함.
 *
 * 하드코딩된 부분들이 있지만 아이디어는 구현이 가능할 것으로 보임.
 * TODO: 완성 전까지 github에 올리지 말 것.
 */
public class ScanTestCode {

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException {


        ArrayList<File> fileList = new ArrayList<>();

        HashMap<Class, Object> temp_depedency = new HashMap<>();


        File root = new File("out/production/classes/server/").getAbsoluteFile();
        System.out.println(root);
        File currentFile = root;

        MyScanner myScanner = new MyScanner();
        myScanner.scanJavaFile(root);

        for(File f : myScanner.getFiles()){
            String start = "/server";
            int startIndex = f.toPath().toString().indexOf(start) + 1;

            String end = ".class";
            int endIndex = f.toPath().toString().indexOf(end);
            String myClassPath = f.toPath().toString().substring(startIndex, endIndex).replace("/",".");
//            System.out.println(f.toPath());
//            System.out.println(myClassPath);

            Class ct = Class.forName(myClassPath);
            if(ct.getAnnotation(Bean.class) != null){
                System.out.println("NAME -> "+ct.getName());
                if(ct.getConstructors().length > 1){
                    Constructor c = ct.getConstructors()[1];
                    for(Parameter p :  c.getParameters()){
                        System.out.print(p);
                        System.out.print(", ");
                    }
                    System.out.println();
                }
                temp_depedency.put(ct,ct.newInstance());
            }

        }

        System.out.println("###");
        temp_depedency.keySet().stream()
                .forEach((key)->{
                    System.out.println(key +" : "+temp_depedency.get(key));
                });





    }
}
