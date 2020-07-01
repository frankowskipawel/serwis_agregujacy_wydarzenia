package com.sda.storage;

import com.sda.model.Picture;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;

public class FtpService {

    private String FTP_HOSTNAME="wegiel-torun.pl";
    private String FTP_LOGIN="ourmeetup";
    private String FTP_PASSWORD="blaipblaip";

    public FTPClient ftp = null;

    public Boolean uploadFileToFTP(File inputFile, Picture picture) throws SQLException, ClassNotFoundException, IOException {
        FTPClient client = new FTPClient();
        FileInputStream fis = null;
        client.connect(FTP_HOSTNAME);
        client.login(FTP_LOGIN, FTP_PASSWORD);
        client.changeWorkingDirectory("/devices_files");
        client.setFileType(FTP.LOCAL_FILE_TYPE);

        fis = new FileInputStream(inputFile.getAbsolutePath());
        Boolean result = client.storeFile(picture.getFileName(), fis);
        client.logout();

        return true;
    }
}
