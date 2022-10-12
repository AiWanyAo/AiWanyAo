package com.xiao.nettydemo;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class TestFilesWalkFileTree {

    public static void main(String[] args) throws IOException {



    }

    /**
     * 目录copy
     * @throws IOException
     */
    private static void m4() throws IOException {
        String source = "D:\\TIM";
        String target = "D:\\Timaaa";
        Files.walk(Paths.get(source)).forEach(path -> {
            try {
                String targetName = path.toString().replace(source,target);
                // 是目录
                if (Files.isDirectory(path)){
                    Files.createDirectory(Paths.get(targetName));
                }else if (Files.isRegularFile(path)){
                    Files.copy(path,Paths.get(targetName));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * 删除文件夹，所有文件
     * @throws IOException
     */
    private static void m3() throws IOException {
        Files.walkFileTree(Paths.get("D:\\delete"),new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                log.debug("OpenDir:{}",dir);
                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                log.debug("deleteFile:{}",file);
                return super.visitFile(file, attrs);
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                log.debug("deleteDir:{}",dir);
                return super.postVisitDirectory(dir, exc);
            }
        });
    }

    /**
     * 查找目录下所有打印Jar,并记录
     * @throws IOException
     */
    private static void m2() throws IOException {
        AtomicInteger jarCount = new AtomicInteger();

        Files.walkFileTree(Paths.get("D:\\Java\\JDK"),new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(".jar")){
                    log.debug("jar:{}",file);
                    jarCount.incrementAndGet();
                }
                return super.visitFile(file, attrs);
            }
        });
        log.debug("jar.len:{}",jarCount);
    }

    /**
     * 遍历目录下所有文件，目录并打印
     * @throws IOException
     */
    private static void m1() throws IOException {
        AtomicInteger dirCount = new AtomicInteger();

        AtomicInteger fileCount = new AtomicInteger();

        Files.walkFileTree(Paths.get("D:\\Java\\JDK"),new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                log.debug("dir:{}",dir);
                dirCount.incrementAndGet();
                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                log.debug("file:{}",file);
                fileCount.incrementAndGet();
                return super.visitFile(file, attrs);
            }
        });
        log.debug("dir count:{}",dirCount);
        log.debug("file count:{}",fileCount);
    }

}
