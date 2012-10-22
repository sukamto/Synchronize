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
public class Cell extends Database {

    private Connection con;
    private String site, cell, bsc, rnc;
    int ID;

    public Cell(Connection con) {
        this.con = con;
    }

    public int saveCell(String site, String cell, String bsc, String rnc, int ID) {
        this.site = site;
        this.cell = cell;
        this.bsc = bsc;
        this.rnc = rnc;
        this.ID = ID;
        int id;
        if (!site.isEmpty()) {
            try {
                clearNull();
                id = getID();
                if (id > -1) {
                    return id;
                }
                return saveCell();
            } catch (SQLException ex) {
                System.err.println(getCurrentDateTime() + "->SAF Error getting site ID: " + ex);
            }
        }
        return -1;
    }

    private void clearNull() throws SQLException {
        con.createStatement().executeUpdate("UPDATE tblSAF_Basic SET [CELL_(IF_APPLICABLE)] = '' WHERE [CELL_(IF_APPLICABLE)] is null");
        con.createStatement().executeUpdate("UPDATE tblSAF_Basic SET RNC = '' WHERE RNC is null");
        con.createStatement().executeUpdate("UPDATE tblSAF_Basic SET BSC = '' WHERE BSC is null");
    }

    private int getID() throws SQLException {
        int id = -1;
        ResultSet rs = con.createStatement().executeQuery("SELECT ID FROM tblSAF_Cell WHERE Site = '" + site
                + "' AND [CELL_(IF_APPLICABLE)] =  '" + cell
                + "' AND BSC = '" + bsc
                + "' AND RNC = '" + rnc + "'");
        while (rs.next()) {
            id = rs.getInt("ID");
            break;
        }
        rs.close();
        return id;
    }

    private int saveCell() throws SQLException {
        con.createStatement().executeUpdate("INSERT INTO tblSAF_Cell "
                + "([Site],[CELL_(IF_APPLICABLE)],[BSC],[RNC],[Ref_Cluster],[Cluster],[RCx_Cx],[Technology],[Location],[Post_Code],[Lat],[Lon],[Access]) "
                + "SELECT DISTINCT [Site],[CELL_(IF_APPLICABLE)],[BSC],[RNC],[Ref_Cluster],[Cluster],[RCx_Cx],[Technology],[Location],[Post_Code],[Lat],[Lon],[Access] "
                + "FROM tblSAF_Basic "
                + "WHERE tblSAF_Basic.[ID] = " + ID);
        return getID();
    }
}
