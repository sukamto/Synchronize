/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.databaseserviceprovider.csvimport.SAF;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author z0006cpz
 */
public class BasicData {

    private Connection con;
    private ResultSet basicDataRS;
//    private List<Integer> idsProcessed = new ArrayList<Integer>();

    public BasicData(Connection con) {
        this.con = con;
    }

    public void saveBasicData() {
        try {
            setBasicDataRS();
            saveBasicRSdata();
        } catch (SQLException ex) {
            System.err.println("Error saving SAF data: " + ex);
        }
    }

    private void setBasicDataRS() throws SQLException {
        basicDataRS = con.createStatement().executeQuery("SELECT * FROM tblSAF_Basic ORDER BY ID");
    }

    private void saveBasicRSdata() throws SQLException {
        while (basicDataRS.next()) {
            int siteIndex = saveSite();
            if (siteIndex > -1) {
                saveCase(siteIndex);
                updateStatus(siteIndex);
            }
        }
    }

//    private boolean idProcessed(int id) {
//        if (idsProcessed.contains(id)) {
//            return true;
//        }
//        idsProcessed.add(id);
//        return false;
//    }
    private int saveSite() throws SQLException {
        String site = getValueFromRS(2);
        String cell = getValueFromRS(3);
        String bsc = getValueFromRS(4);
        String rnc = getValueFromRS(5);
        Cell cell_ = new Cell(con);
        return cell_.saveCell(site, cell, bsc, rnc, basicDataRS.getInt("ID"));
    }

    private String getValueFromRS(int colIndex) throws SQLException {
        if (basicDataRS.getString(colIndex) == null) {
            return "";
        }
        return basicDataRS.getString(colIndex);
    }

    private void saveCase(int siteID) throws SQLException {
        Case case_ = new Case(con);
        case_.saveCase(siteID, basicDataRS.getString("CASE_HISTORY"), basicDataRS.getString("CLARIFY_CASE"), basicDataRS.getInt("ID"));
    }

    private void updateStatus(int siteID) throws SQLException {
        Status status = new Status(con);
        status.setStatus(siteID, basicDataRS.getString("CASE_HISTORY"), basicDataRS.getString("CLARIFY_CASE"), basicDataRS.getInt("ID"));
    }
}
