/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.globalcontextservice;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

/**
 *
 * @author z0006cpz
 */
public interface GlobalInterface {

    public void setDbConnection(Connection con);

    public Connection getDbConnection();

    public void setDbStatement(Statement dbStatement);

    public Statement getDbStatement();

    public List<String> getMappingFiles();

    public List<String> getMappingTables();

    public void setMappingFiles(List<String> mappingFiles);

    public void setMappingTables(List<String> mappingTables);

    public void setDataInserted(boolean inserted);

    public boolean isDataInserted();

    public void addTableToUpdatedTables(String name);

    public void removeTableFromUpdatedTables(String name);

    public List<String> getUpdatedTables();

    public void setNsnFileNames(List<String> nsnFileNames);

    public void setStatsFileNames(List<String> statsFileNames);

    public List<String> getNsnFileNames();

    public List<String> getStatsFileNames();

    public void setMatchingNSNTableNames(List<String> matchingNSNTableNames);

    public void setMatchingStatsTableNames(List<String> matchingStatsTableNames);

    public List<String> getMatchingNSNTableNames();

    public List<String> getMatchingStatsTableNames();

    public void setMatchingStatsFormats(List<String> matchingStatsFormats);

    public List<String> getMatchingStatsFormats();

    public List<String> getAlarmsFileNames();

    public void setAlarmFileNames(List<String> alarmsFileNames);

    public void setMatchingAlarmFormats(List<String> matchingAlarmsFormats);

    public List<String> getMatchingAlarmsFormats();

    public List<String> getMatchingAlarmsTableNames();

    public void setMatchingAlarmsTableNames(List<String> matchingAlarmsTableNames);

    public void setCMFileNames(List<String> cmFileNames);

    public List<String> getCMFileNames();

    public void setMatchingCMFormats(List<String> matchingCMFormats);

    public List<String> getMatchingCMFormats();

    public void setMatchingCMTableNames(List<String> matchingCMTableNames);

    public List<String> getMatchingCMTableNames();
}
