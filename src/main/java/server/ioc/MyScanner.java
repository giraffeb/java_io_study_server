package server.ioc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MyScanner {


    private ArrayList<File> files = new ArrayList<>();

    public List<File> getFiles(){
        return this.files;
    }

    public List<File> scanJavaFile(File root){

        if(root.listFiles() == null){
            return this.files;
        }else{
            for(File temp :  root.listFiles()){
                if(temp.isDirectory()){
                    List<File> tempList = new MyScanner().scanJavaFile(temp);
                    this.files.addAll(tempList);
                }else{
                    if(temp.getAbsolutePath().endsWith(".class")){
                        this.files.add(temp);
                    }
                }
            }
        }

        return this.files;
    }

}
