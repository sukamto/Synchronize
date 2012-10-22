/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.filemethodsserviceprovider;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.nsn.filemethodsservice.FileNameInterface;

/**
 *
 * @author z0006cpz
 */
@org.openide.util.lookup.ServiceProvider(service = FileNameInterface.class)
public class FileName implements FileNameInterface {

    @Override
    public void storeSuccessfulFile(Connection con, File file) {
        try {
            con.createStatement().executeUpdate("INSERT INTO [tblFilesProcessed] ([fileName],[modified_Date],[path]) "
                    + "VALUES ('" + file.getName() + "','" + getLastModified(file) + "','" + file.getParent() + "')");
        } catch (SQLException ex) {
            System.err.println(getCurrentDateTime() + "->Error saving filename/mod datetime: " + ex);
        }
    }

    @Override
    public void storeErrorFile(Connection con, File file) {
        try {
            con.createStatement().executeUpdate("INSERT INTO [tblErrorFiles] ([fileName],[modified_Date],[path]) "
                    + "VALUES ('" + file.getName() + "','" + getLastModified(file) + "','" + file.getParent() + "')");
        } catch (SQLException ex) {
            System.err.println(getCurrentDateTime() + "->Error saving filename/mod datetime: " + ex);
        }
    }

    private long getLastModified(File file) {
        return file.lastModified();
    }

    private String getCurrentDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
