/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.databaseservice;

import java.sql.Connection;
import org.openide.util.Lookup;

/**
 *
 * @author Z0006CPZ
 */
public abstract class Database {

    protected String dbUrl = "jdbc:sqlserver://localhost:1433;databaseName=synix;user=sa;password=un1xUN!X";
    protected String dbClass = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    public static Database getDefault() {
        return Lookup.getDefault().lookup(Database.class);
    }

    public abstract Connection Connect();

}