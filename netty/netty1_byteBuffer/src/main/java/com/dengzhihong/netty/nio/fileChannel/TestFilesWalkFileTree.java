package com.dengzhihong.netty.nio.fileChannel;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 文件操作
 */
@Slf4j
public class TestFilesWalkFileTree {

    public static void main(String[] args) throws IOException {

    }

    /**
     * 删除多级目录及文件
     * @throws IOException
     */
    private static void deleteFiles() throws IOException {
        Files.walkFileTree(Paths.get("D:\\window\\temp"),new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println("进入目录----->"+dir);
                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                System.out.println("删除文件----->"+file);
                return super.visitFile(file, attrs);
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                System.out.println("删除并退出目录----->"+dir);
                return super.postVisitDirectory(dir, exc);
            }
        });
    }

    /**
     * 统计文件数量
     * @throws IOException
     */
    private static void statisticsLrc() throws IOException {
        AtomicInteger lrcCount=new AtomicInteger();
        Files.walkFileTree(Paths.get("D:\\KuGou\\Lyric"),new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(".lrc")) {
                    System.out.println(file);
                    lrcCount.incrementAndGet();
                }
                return super.visitFile(file, attrs);
            }
        });
        System.out.println(lrcCount);
    }

    /**
     * 遍历目录文件
     */
    private static void printFiles() throws IOException {
        AtomicInteger dirCount=new AtomicInteger();
        AtomicInteger fileCount=new AtomicInteger();
        Files.walkFileTree(Paths.get("D:\\KuGou\\Lyric"),new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println("---->"+dir);
                dirCount.incrementAndGet();
                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println(file);
                fileCount.incrementAndGet();
                return super.visitFile(file, attrs);
            }
        });
        System.out.println("dir count:"+dirCount);
        System.out.println("file count:"+fileCount);
    }
}
