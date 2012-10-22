/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.importfiles;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.nsn.filemethodsservice.FileMethodsServiceInterface;
import org.nsn.globalcontextservice.GlobalInterface;
import org.nsn.importfiles.CM.CMRunner;
import org.nsn.importfiles.SAF.SAF_Runner;
import org.nsn.importfiles.alarms.AlarmsRunner;
import org.nsn.importfiles.ericsson.EricssonRunner;
import org.nsn.importfiles.nsn.NSNRunner;
import org.nsn.importfiles.statistics.StatsRunner;
import org.openide.util.Lookup;

/**
 *
 * @author z0006cpz
 */
public class ImportThreading extends ImportSuper {

    private String QUERY_MAPPING = "SELECT [File_Name],[Database_Table],[DateFormat] FROM [tblImportMapping]";
    private String QUERY_DATA_LOCATION = "SELECT [Location] FROM [tblMisc]";
    private String[] ERICSSON_ID = {"2geric", "3geric"};
    private String[] NSN_ID = {"2gnsn", "3gnsn"};
    private String[] STATS_ID = {"_handover", "_ho_adj", "_res_access", "_rx_statistics", "_service", "_timing_advance", "_traffic"};
    private String[] ALARMS_ID = {"_alarms"};
    private String[] CM_ID = {"_c_adj", "_c_bts", "_c_chn", "_c_trx", "_dca_obs-1"};
    private String[] SAF_ID = {"saf tracker"};
    private Statement statement;
    private List<String> ericssonFileNames = new ArrayList<String>();
    private List<String> nsnFileNames = new ArrayList<String>();
    private List<String> statsFileNames = new ArrayList<String>();
    private List<String> alarmsFileNames = new ArrayList<String>();
    private List<String> cmFileNames = new ArrayList<String>();
    private List<String> safFileNames = new ArrayList<String>();
    private List<File> allFileNames = new ArrayList<File>();
    private GlobalInterface gi = Lookup.getDefault().lookup(GlobalInterface.class);
    List<String> allFiles = new ArrayList<String>();
    List<String> allTables = new ArrayList<String>();
    List<String> allFormats = new ArrayList<String>();

    public void importFiles() {
        setStatement();
        try {
            String dataLocation = getDataLocation();
            FileMethodsServiceInterface fm = Lookup.getDefault().lookup(FileMethodsServiceInterface.class);
            fm.setStatement(statement);
            fm.setROOT(dataLocation);
            allFileNames.addAll(fm.getFilesNotYetImported());
            readMappingFromTable();
            setEricssonFileNames();
            setNsnFileNames();
            setStatsFileNames();
            setAlarmsFileNames();
            setCMFileNames();
            setSAFileNames();
            saveGlobalData();
            try {
//                Ericsson ericsson = new Ericsson(ericssonFileNames, getMatchingTableNames(ericssonFileNames));
//                ericsson.stageFiles();
                Runnable ericsson = new EricssonRunner(ericssonFileNames, getMatchingTableNames(ericssonFileNames));
                Runnable nsn = new NSNRunner(gi.getNsnFileNames(), gi.getMatchingNSNTableNames(), gi.getDbConnection());
                Runnable stats = new StatsRunner();
                Runnable alarms = new AlarmsRunner();
                Runnable cm = new CMRunner();
                Runnable saf = new SAF_Runner(safFileNames, gi.getDbConnection());
                Runnable index = new IndexRunner(gi.getDbConnection());
                Thread ericThread = new Thread(ericsson);
                Thread nsnThread = new Thread(nsn);
                Thread statsThread = new Thread(stats);
                Thread alarmsThread = new Thread(alarms);
                Thread cmThread = new Thread(cm);
                Thread safThread = new Thread(saf);
                Thread indexThread = new Thread(index);

                try {
                    ericThread.start();
                    ericThread.join();

                    nsnThread.start();
                    nsnThread.join();

                    statsThread.start();
                    statsThread.join();

                    alarmsThread.start();
                    alarmsThread.join();

                    cmThread.start();
                    cmThread.join();

                    safThread.start();
                    safThread.join();

//                    indexThread.start();
//                    indexThread.join();
                    System.out.println("Process Completed.");
                    System.out.println("Wating...");
                } catch (InterruptedException ex) {
                    System.err.println(ex);
                }
            } catch (SQLException ex) {
                System.err.println(getCurrentDateTime() + "->Error extracting import mapping: " + ex);
            }
        } catch (SQLException ex) {
            System.err.println(getCurrentDateTime() + "->Error extracting import mapping: " + ex);
        }
    }

    static void threadMessage(String message) {
        String threadName = Thread.currentThread().getName();
        System.out.format("%s: %s%n", threadName, message);
    }

    private void setStatement() {
        statement = gi.getDbStatement();
    }

    private String getDataLocation() throws SQLException {
        String path = "";
        ResultSet rs = statement.executeQuery(QUERY_DATA_LOCATION);
        while (rs.next()) {
            path = rs.getString(1);
        }
        rs.close();
        return path;
    }

    private void setEricssonFileNames() {
        for (File file : allFileNames) {
            for (String id : ERICSSON_ID) {
                if (file.getName().toLowerCase().contains(id)) {
                    ericssonFileNames.add(file.getAbsolutePath());
                }
            }
        }
    }

    private void setNsnFileNames() {
        for (File file : allFileNames) {
            for (String id : NSN_ID) {
                if (file.getName().toLowerCase().contains(id)) {
                    nsnFileNames.add(file.getAbsolutePath());
                }
            }
        }
    }

    private void setStatsFileNames() {
        for (File file : allFileNames) {
            for (String id : STATS_ID) {
                if (file.getName().toLowerCase().contains(id)) {
                    statsFileNames.add(file.getAbsolutePath());
                }
            }
        }
    }

    private void setAlarmsFileNames() {
        for (File file : allFileNames) {
            for (String id : ALARMS_ID) {
                if (file.getName().toLowerCase().contains(id)) {
                    alarmsFileNames.add(file.getAbsolutePath());
                }
            }
        }
    }

    private void setCMFileNames() {
        for (File file : allFileNames) {
            for (String id : CM_ID) {
                if (file.getName().toLowerCase().contains(id)) {
                    cmFileNames.add(file.getAbsolutePath());
                }
            }
        }
    }

    private void setSAFileNames() {
        for (File file : allFileNames) {
            for (String id : SAF_ID) {
                if (file.getName().toLowerCase().contains(id)) {
                    safFileNames.add(file.getAbsolutePath());
                }
            }
        }
    }

    private void saveGlobalData() throws SQLException {
        gi.setMatchingNSNTableNames(getMatchingTableNames(nsnFileNames));
        gi.setMatchingStatsTableNames(getMatchingTableNames(statsFileNames));
        gi.setNsnFileNames(nsnFileNames);
        gi.setStatsFileNames(statsFileNames);
        gi.setMatchingStatsFormats(getMatchingFormats(statsFileNames));
        gi.setMatchingAlarmsTableNames(getMatchingTableNames(alarmsFileNames));
        gi.setAlarmFileNames(alarmsFileNames);
        gi.setMatchingAlarmFormats(getMatchingFormats(alarmsFileNames));
        gi.setMatchingCMTableNames(getMatchingTableNames(cmFileNames));
        gi.setCMFileNames(cmFileNames);
        gi.setMatchingCMFormats(getMatchingFormats(cmFileNames));
    }

    private void readMappingFromTable() throws SQLException {
        ResultSet rs = statement.executeQuery(QUERY_MAPPING);
        while (rs.next()) {
            allFiles.add(rs.getString(1).toLowerCase());
            allTables.add(rs.getString(2));
            allFormats.add(rs.getString(3));
        }
        rs.close();
    }

    private List<String> getMatchingTableNames(List<String> list) throws SQLException {
        List<String> matchingTables = new ArrayList<String>();
        for (String file : list) {
            File f = new File(file);
            String fileName = f.getName().toLowerCase().substring(0, f.getName().lastIndexOf("."));
            int index = getIndexOfFileName(allFiles, fileName);
//            int index = allFiles.indexOf(fileName.substring(0, fileName.lastIndexOf(".")));
            if (index > -1) {
                matchingTables.add(allTables.get(index));
            }
        }
        return matchingTables;
    }

    private List<String> getMatchingFormats(List<String> list) throws SQLException {
        List<String> matchingFormats = new ArrayList<String>();
        for (String file : list) {
            File f = new File(file);
            String fileName = f.getName().toLowerCase().substring(0, f.getName().lastIndexOf("."));
            int index = getIndexOfFileName(allFiles, fileName);
            if (index > -1) {
                matchingFormats.add(allFormats.get(index).trim());
            }
        }
        return matchingFormats;
    }

    private int getIndexOfFileName(List<String> allFiles, String name) {
        for (int i = 0; i < allFiles.size(); i++) {
            String f = allFiles.get(i).toLowerCase();
            if (name.toLowerCase().endsWith(f)) {
                return i;
            }
        }
        return -1;
    }
}
