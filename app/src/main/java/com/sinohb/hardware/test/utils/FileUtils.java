package com.sinohb.hardware.test.utils;

import com.sinohb.hardware.test.constant.Constants;
import com.sinohb.logger.LogTools;
import com.sinohb.logger.utils.IOUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileUtils {
    private static final String TAG = "FileUtils";

    private FileUtils() {
    }

    public static final String getFileContent(String path) throws IOException {
        FileInputStream fis = null;
        BufferedReader reader = null;
        try {
            fis = new FileInputStream(path);
            reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            line = builder.toString();
            LogTools.p(TAG, "文件内容：" + line);
            builder.setLength(0);
            return line;
        } catch (IOException e) {
            LogTools.p(TAG, e, "文件读取出错 path:" + path);
            throw new IOException(e);
        } finally {
            IOUtils.closeQuietly(fis);
            IOUtils.closeQuietly(reader);
        }
    }
}
