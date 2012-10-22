/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.databaseserviceprovider.csvimport.SAF;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.nsn.databaseserviceprovider.Database;

/**
 *
 * @author z0006cpz
 */
public class Case extends Database {

    private Connection con;
    private String caseDescription, caseNumber;
    private int siteID, ID;

    public Case(Connection con) {
        this.con = con;
    }

    public void saveCase(int siteID, String caseDescription, String caseNumber, int ID) {
        this.siteID = siteID;
        this.caseDescription = caseDescription;
        this.caseNumber = caseNumber;
        this.ID = ID;
        try {
            if (validCaseNumber()) {
                if (!caseExists()) {
                    saveCase();
                }
            }
        } catch (SQLException ex) {
            System.err.println(getCurrentDateTime() + "->SAF Error saving case: " + ex);
        }
    }

    private boolean validCaseNumber() throws SQLException {
        String site = getSite();
        String cell = getCell();
        if (caseNumber.isEmpty() && cell.isEmpty()) {
            System.err.println("Invalid case number for site: " + site);
            return false;
        }
        if (caseNumber.isEmpty() && !cell.isEmpty()) {
            System.err.println("Invalid case number for site: " + site + " cell: " + cell);
            return false;
        }
        return true;
    }

    private String getSite() throws SQLException {
        ResultSet rs = con.createStatement().executeQuery("SELECT Site FROM tblSAF_Cell "
                + "WHERE ID = " + siteID);
        rs.next();
        String site = rs.getString(1);
        rs.close();
        return site;
    }

    private String getCell() throws SQLException {
        ResultSet rs = con.createStatement().executeQuery("SELECT [CELL_(IF_APPLICABLE)] FROM tblSAF_Cell "
                + "WHERE ID = " + siteID);
        rs.next();
        String cell = rs.getString(1);
        rs.close();
        return cell;
    }

    private boolean caseExists() throws SQLException, SQLException, SQLException, SQLException {
        ResultSet rs = con.createStatement().executeQuery("SELECT tblSAF_Case.[Case] FROM tblSAF_Case "
                + "WHERE tblSAF_Cell_ID = " + siteID);
        while (rs.next()) {
            if (rs.getString(1).equalsIgnoreCase(caseDescription)) {
                rs.close();
                return true;
            }
        }
        rs.close();
        return false;
    }

    private void saveCase() throws SQLException {
        con.createStatement().executeUpdate("INSERT INTO tblSAF_Case "
                + "([tblSAF_Cell_ID],[Raised_By],[Clarify_Case],[NSN_Priority],[NPO_Priority],[NPO_Owner],[Engineer],[Date_Raised],[Status],[Date_of_Fix],[Current_Owner],[Symptom],[Suspected_Issue],[frequency_retune],[Case],[Outage_ID],[Clarify_Priority]) "
                + "(SELECT " + siteID + ",[Raised_By],[Clarify_Case],[NSN_Priority],[NPO_Priority],[NPO_Owner],[Engineer],[Date_Raised],[Status],[Date_of_Fix],[Current_Owner],[Symptom],[Suspected_Issue],[frequency_retune.],[Case_History],[Outage_ID],[Clarify_Priority] "
                + "FROM tblSAF_Basic "
                + "WHERE tblSAF_Basic.[ID] = " + ID + ")");
    }

//    private int getCaseID() throws SQLException {
//        ResultSet rs = con.createStatement().executeQuery("SELECT MAX(ID) FROM tblSAF_Case WHERE SiteID = " + siteID);
//        rs.next();
//        return getValueFromRS(rs, 1) + 1;
//    }
//    private int getValueFromRS(ResultSet rs, int colIndex) throws SQLException {
//        if (rs.getString(colIndex) == null) {
//            return -1;
//        }
//        return rs.getInt(colIndex);
//    }
//    private String getItemFromCellTable(String colName) throws SQLException {
//        ResultSet rs = con.createStatement().executeQuery("SELECT " + colName + " FROM tblSAF_Cell WHERE ID = " + siteID);
//        rs.next();
//        String res = rs.getString(1);
//        rs.close();
//        return res;
//    }
}
