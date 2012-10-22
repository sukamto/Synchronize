/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.globalcontextserviceprovider;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.nsn.globalcontextservice.GlobalInterface;

/**
 *
 * @author z0006cpz
 */
@org.openide.util.lookup.ServiceProvider(service = GlobalInterface.class)
public class GlobalContext implements GlobalInterface {

    private Connection con;
    private Statement dbStatement;
    private List<String> mappingFiles = new ArrayList<String>();
    private List<String> mappingTables = new ArrayList<String>();
    private List<String> updatedTables = new ArrayList<String>();
    private List<String> nsnFileNames = new ArrayList<String>();
    private List<String> statsFileNames = new ArrayList<String>();
    private List<String> matchingStatsFormats = new ArrayList<String>();
    private List<String> matchingNSNTableNames = new ArrayList<String>();
    private List<String> matchingStatsTableNames = new ArrayList<String>();
    private List<String> alarmsFileNames = new ArrayList<String>();
    private List<String> matchingAlarmsTableNames = new ArrayList<String>();
    private List<String> matchingAlarmsFormats = new ArrayList<String>();
    private List<String> matchingCMTableNames = new ArrayList<String>();
    private List<String> matchingCMFormats = new ArrayList<String>();
    private List<String> cmFileNames = new ArrayList<String>();
    private boolean dataInserted;

    @Override
    public List<String> getMappingFiles() {
        return mappingFiles;
    }

    @Override
    public List<String> getMappingTables() {
        return mappingTables;
    }

    @Override
    public void setMappingFiles(List<String> mappingFiles) {
        this.mappingFiles.clear();
        for (String s : mappingFiles) {
            this.mappingFiles.add(s);
        }
    }

    @Override
    public void setMappingTables(List<String> mappingTables) {
        this.mappingTables.clear();
        for (String s : mappingTables) {
            this.mappingTables.add(s);
        }
    }

    @Override
    public void setDbConnection(Connection con) {
        this.con = con;
    }

    @Override
    public Connection getDbConnection() {
        return con;
    }

    @Override
    public void setDbStatement(Statement dbStatement) {
        this.dbStatement = dbStatement;
    }

    @Override
    public Statement getDbStatement() {
        return dbStatement;
    }

    @Override
    public void setDataInserted(boolean inserted) {
        dataInserted = inserted;
    }

    @Override
    public boolean isDataInserted() {
        return dataInserted;
    }

    @Override
    public void addTableToUpdatedTables(String name) {
        if (!updatedTables.contains(name)) {
            updatedTables.add(name);
        }
    }

    @Override
    public void removeTableFromUpdatedTables(String name) {
        updatedTables.remove(name);
    }

    @Override
    public List<String> getUpdatedTables() {
        return updatedTables;
    }

    @Override
    public List<String> getMatchingStatsTableNames() {
        return matchingStatsTableNames;
    }

    @Override
    public List<String> getMatchingNSNTableNames() {
        return matchingNSNTableNames;
    }

    @Override
    public void setMatchingStatsTableNames(List<String> matchingStatsTableNames) {
        this.matchingStatsTableNames.clear();
        this.matchingStatsTableNames.addAll(matchingStatsTableNames);
    }

    @Override
    public void setMatchingNSNTableNames(List<String> matchingNSNTableNames) {
        this.matchingNSNTableNames.clear();
        this.matchingNSNTableNames.addAll(matchingNSNTableNames);
    }

    @Override
    public List<String> getStatsFileNames() {
        return statsFileNames;
    }

    @Override
    public List<String> getNsnFileNames() {
        return nsnFileNames;
    }

    @Override
    public void setStatsFileNames(List<String> statsFileNames) {
        this.statsFileNames.clear();
        this.statsFileNames.addAll(statsFileNames);
    }

    @Override
    public void setNsnFileNames(List<String> nsnFileNames) {
        this.nsnFileNames.clear();
        this.nsnFileNames.addAll(nsnFileNames);
    }

    @Override
    public void setMatchingStatsFormats(List<String> matchingStatsFormats) {
        this.matchingStatsFormats.clear();
        this.matchingStatsFormats.addAll(matchingStatsFormats);
    }

    @Override
    public List<String> getMatchingStatsFormats() {
        return matchingStatsFormats;
    }

    @Override
    public void setAlarmFileNames(List<String> alarmsFileNames) {
        this.alarmsFileNames.clear();
        this.alarmsFileNames.addAll(alarmsFileNames);
    }

    @Override
    public List<String> getAlarmsFileNames() {
        return alarmsFileNames;
    }

    @Override
    public void setMatchingAlarmFormats(List<String> matchingAlarmsFormats) {
        this.matchingAlarmsFormats.clear();
        this.matchingAlarmsFormats.addAll(matchingAlarmsFormats);
    }

    @Override
    public List<String> getMatchingAlarmsFormats() {
        return matchingAlarmsFormats;
    }

    @Override
    public void setMatchingAlarmsTableNames(List<String> matchingAlarmsTableNames) {
        this.matchingAlarmsTableNames.clear();
        this.matchingAlarmsTableNames.addAll(matchingAlarmsTableNames);
    }

    @Override
    public List<String> getMatchingAlarmsTableNames() {
        return matchingAlarmsTableNames;
    }

    @Override
    public void setCMFileNames(List<String> cmFileNames) {
        this.cmFileNames.clear();
        this.cmFileNames.addAll(cmFileNames);
    }

    @Override
    public List<String> getCMFileNames() {
        return cmFileNames;
    }

    @Override
    public void setMatchingCMFormats(List<String> matchingCMFormats) {
        this.matchingCMFormats.clear();
        this.matchingCMFormats.addAll(matchingCMFormats);
    }

    @Override
    public List<String> getMatchingCMFormats() {
        return matchingCMFormats;
    }

    @Override
    public void setMatchingCMTableNames(List<String> matchingCMTableNames) {
        this.matchingCMTableNames.clear();
        this.matchingCMTableNames.addAll(matchingCMTableNames);
    }

    @Override
    public List<String> getMatchingCMTableNames() {
        return matchingCMTableNames;
    }
}
