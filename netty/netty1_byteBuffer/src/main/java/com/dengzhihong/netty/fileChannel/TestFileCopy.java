package com.dengzhihong.netty.fileChannel;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 文件拷贝,目标文件夹不存在会自动创建
 */
public class TestFileCopy {

    public static void main(String[] args) throws IOException {
        String source="D:\\window\\source";
        String target="D:\\window\\target";

        Files.walk(Paths.get(source)).forEach(path -> {
            try {
                String targetName = path.toString().replace(source, target);
                if (Files.isDirectory(path)) {
                    //是一个目录
                    Files.createDirectory(Paths.get(targetName));
                }else if(Files.isRegularFile(path)){
                    //是一个普通文件
                    Files.copy(path,Paths.get(targetName));
                }
            } catch (Exception e) {
                if (! (e instanceof FileAlreadyExistsException)){
                    e.printStackTrace();
                }
            }
        });
    }
}
