/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.filemethodsserviceprovider;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.nsn.filemethodsservice.FileMethodsServiceInterface;

/**
 *
 * @author z0006cpz
 */
@org.openide.util.lookup.ServiceProvider(service = FileMethodsServiceInterface.class)
public class FileLookup implements FileMethodsServiceInterface {

    private String[] VALID_EXTENSIONS = {".csv", ".xls", ".xlsx"};
    private String QUERY_FILES_PROCESSED = "SELECT [fileName],[modified_Date] FROM [dbo].[tblFilesProcessed]";
    private String QUERY_ERROR_FILES = "SELECT [fileName],[modified_Date] FROM [dbo].[tblErrorFiles]";
    private String QUERY_IMPORT_MAPPING = "SELECT [File_Name] FROM [synix].[dbo].[tblImportMapping]";
    private String QUERY_IMPORT_MAPPING_PATTERN = "SELECT [Search_Pattern] FROM [synix].[dbo].[tblImportMapping]";
    private String root;
    private Statement statement;
    private List<String> importMappingFileNames;
    private List<String> importMappingPatterns;
    private List<String> processedFileNames;
    private List<Long> processedModified_Dates;
    private List<String> errorFileNames;
    private List<Long> errorModified_Dates;
    private List<File> filesNotYetImported;

    @Override
    public void setStatement(Statement statement) {
        this.statement = statement;
    }

    @Override
    public void setROOT(String ROOT) {
        this.root = ROOT;
    }

    @Override
    public List<File> getFilesNotYetImported() {
        initLists();
        List<File> allFiles = getAllFiles();
        try {
            setProcessedLists();
            setErrorLists();
            setImportMappingFileNames();
            setImportMappingPatterns();
            for (File file : allFiles) {
                if (file != null) {
//                    if (file.canRead() && importCandidate(file)) {
                    if (importCandidate(file)) {
                        if (importFile(file) && !isErrorFile(file)) {
                            filesNotYetImported.add(file);
                        }
                    }
                }
            }
            System.out.println(getCurrentDateTime() + "->Search completed");
        } catch (SQLException ex) {
            System.err.println(getCurrentDateTime() + "->Error looking up new files: " + ex);
        }
        return filesNotYetImported;
    }

    private void initLists() {
        importMappingFileNames = new ArrayList<String>();
        importMappingPatterns = new ArrayList<String>();
        processedFileNames = new ArrayList<String>();
        processedModified_Dates = new ArrayList<Long>();
        filesNotYetImported = new ArrayList<File>();
        errorFileNames = new ArrayList<String>();
        errorModified_Dates = new ArrayList<Long>();
    }

    private List<File> getAllFiles() {
        System.out.println(getCurrentDateTime() + "->Searching for new files @ " + root);
        List<File> filesList = new ArrayList<File>();
        File dir = new File(root);
        File[] files = dir.listFiles();
        filesList.addAll(Arrays.asList(files));
        for (int i = 0; i < filesList.size(); i++) {
            File file = filesList.get(i);
            if (file.isDirectory()) {
                files = getFiles(file);
                filesList.addAll(Arrays.asList(files));
            }
        }
        return remDirs(filesList);
    }

    private File[] getFiles(File dir) {
        return dir.listFiles();
    }

    private List<File> remDirs(List<File> list) {
        for (int i = 0; i < list.size(); i++) {
            if (!isValidFile(list.get(i).getName())) {
                list.set(i, null);
            }
        }
        return list;
    }

    private boolean isValidFile(String name) {
        for (String ext : VALID_EXTENSIONS) {
            if (name.toLowerCase().endsWith(ext.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private void setProcessedLists() throws SQLException {
        ResultSet rs = statement.executeQuery(QUERY_FILES_PROCESSED);
        while (rs.next()) {
            processedFileNames.add(rs.getString(1).toLowerCase());
            processedModified_Dates.add(rs.getLong(2));
        }
        rs.close();
    }

    private void setErrorLists() throws SQLException {
        ResultSet rs = statement.executeQuery(QUERY_ERROR_FILES);
        while (rs.next()) {
            errorFileNames.add(rs.getString(1).toLowerCase());
            errorModified_Dates.add(rs.getLong(2));
        }
        rs.close();
    }

    private void setImportMappingFileNames() throws SQLException {
        ResultSet rs = statement.executeQuery(QUERY_IMPORT_MAPPING);
        while (rs.next()) {
            importMappingFileNames.add(rs.getString(1).toLowerCase());
        }
        rs.close();
    }

    private void setImportMappingPatterns() throws SQLException {
        ResultSet rs = statement.executeQuery(QUERY_IMPORT_MAPPING_PATTERN);
        while (rs.next()) {
            importMappingPatterns.add(rs.getString(1).toLowerCase());
        }
        rs.close();
    }

    private boolean importCandidate(File f) {
        String fileName = f.getName().toLowerCase();
        fileName = fileName.substring(0, fileName.lastIndexOf("."));
        for (int i = 0; i < importMappingFileNames.size(); i++) {
            String patterns = importMappingPatterns.get(i);
            if (patterns.equalsIgnoreCase("beginswith")) {
                if (fileName.startsWith(importMappingFileNames.get(i))) {
                    return true;
                }
            } else {
                if (fileName.endsWith(importMappingFileNames.get(i))) {
                    return true;
                }
            }
        }
//          return importMappingFileNames.contains(fileName.substring(0, fileName.lastIndexOf(".")));
        return false;
    }

    private boolean isErrorFile(File f) {
        long fileModifiedDate = f.lastModified();
        String name = f.getName();
        if (errorFileNames.indexOf(name.toLowerCase()) < 0) {
            return false;
        }
        for (int i = 0; i < errorFileNames.size(); i++) {
            if (errorFileNames.get(i).equalsIgnoreCase(name)) {
                long lastDateProcessed = errorModified_Dates.get(i);
                if (lastDateProcessed == fileModifiedDate) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean importFile(File f) {
        long fileModifiedDate = f.lastModified();
        String name = f.getName();
        if (processedFileNames.indexOf(name.toLowerCase()) < 0) {
            return true;
        }
        for (int i = 0; i < processedFileNames.size(); i++) {
            if (processedFileNames.get(i).equalsIgnoreCase(name)) {
                long lastDateProcessed = processedModified_Dates.get(i);
                if (lastDateProcessed == fileModifiedDate) {
                    return false;
                }
            }
        }
        return true;
    }

    private String getCurrentDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
