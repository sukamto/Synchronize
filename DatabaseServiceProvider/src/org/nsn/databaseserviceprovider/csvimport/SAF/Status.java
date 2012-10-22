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
public class Status extends Database {

    private Connection con;
    private String caseDescription;
    private int siteID, ID;

    public Status(Connection con) {
        this.con = con;
    }

    public void setStatus(int siteID, String caseDescription, String caseNumber, int ID) {
        this.siteID = siteID;
        this.caseDescription = caseDescription;
        this.ID = ID;
        try {
            int caseID = getCaseID();
            if (caseID > -1) {
                updateStatus(caseID);
            } else {
                System.err.println(getCurrentDateTime() + "->CaseID not found for case" + caseNumber);
            }
        } catch (SQLException ex) {
            System.err.println(getCurrentDateTime() + "->SAF Error saving case: " + ex);
        }
    }

    private int getCaseID() throws SQLException, SQLException, SQLException, SQLException {
        int id = -1;
        ResultSet rs = con.createStatement().executeQuery("SELECT tblSAF_Case.[Case],[ID] FROM tblSAF_Case "
                + "WHERE tblSAF_Cell_ID = " + siteID);
        while (rs.next()) {
            if (rs.getString(1).equalsIgnoreCase(caseDescription)) {
                id = rs.getInt("ID");
                rs.close();
                return id;
            }
        }
        rs.close();
        return id;
    }

    private void updateStatus(int caseID) throws SQLException {
        con.createStatement().executeUpdate("UPDATE tblSAF_Case SET "
                + "Status = (SELECT Status FROM tblSAF_Basic WHERE ID = " + ID + "),"
                + "[Date_of_Fix] = (SELECT [Date_of_Fix] FROM tblSAF_Basic WHERE ID = " + ID + ")"
                + "WHERE tblSAF_Case.[ID] = " + caseID);
    }
}
