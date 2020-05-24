package com.example.autocoach.Sync;

import java.io.File;

public class FileUtils {
    private String path = "";

    public FileUtils(String path) {
        this.path = path;
        File file = new File(path);
        /**
         *如果文件夹不存在就创建
         */
        if (!file.exists()) {
//            file.mkdirs();
            System.out.println("folder not exist");
        }
    }

    /**
     * 创建一个文件
     * @param FileName 文件名
     * @return
     */
    public File createFile(String FileName) {
        return new File(path, FileName);
    }
}
