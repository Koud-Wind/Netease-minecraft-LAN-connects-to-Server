package com.netease.mc.mod.authlib;

import com.mojang.authlib.exceptions.AuthenticationException;
import com.netease.mc.mod.network.common.Common;
import com.netease.mc.mod.network.common.Library;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AuthenticationCpp {
    private static final Logger LOGGER = LogManager.getLogger();

    private Boolean SafeLoadLibrary(String path) {
        try {
            System.load(path);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

        public Boolean LoadLibrary() throws AuthenticationException {
        try {
            String javaLibPath = System.getProperty("java.library.path");
            File runtime = new File(javaLibPath, "runtime");
            File[] files = runtime.listFiles();
            List<File> failedFiles = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                if (i > 0) {
                    if (files.length <= 0) {
                        break;
                    }
                }
                for (File file : files) {
                    if (file.isFile() && file.getName().contains("dll") && !SafeLoadLibrary(file.getPath()).booleanValue()) {
                        failedFiles.add(file);
                    }
                }
                files = (File[]) failedFiles.toArray(new File[failedFiles.size()]);
                failedFiles.clear();
                LOGGER.info(Integer.valueOf(files.length));
            }
            return true;
        } catch (Exception e) {
            throw new AuthenticationException(e.getMessage());
        }
    }

        public Boolean Authentication(int port, String serverId) throws AuthenticationException {
        try {
            int code = Library.AuthenticationAccessToken(port, serverId);
            AuthenticationCpp2 error = AuthenticationCpp2.GetErrorCode(code);
            if (error != AuthenticationCpp2.SUCCESS) {
                throw new AuthenticationException(error.getDescription());
            }
            return Boolean.TRUE;
        } catch (Exception e) {
            Common.CatchException(e);
            throw new AuthenticationException(e.getMessage());
        }
    }

    public static void main(String[] args) throws AuthenticationException {
        AuthenticationCpp test = new AuthenticationCpp();
        String port = System.getProperty("launcherControlPort");
        LOGGER.info(port);
        test.Authentication(Integer.parseInt(port), "");
    }
}
