/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.databaseservice;

import java.sql.Connection;

/**
 *
 * @author z0006cpz
 */
public interface IndexInterface {

    public void reOrganizeAll(Connection con);
    
    public void reOrganizeIndex(Connection con,String tableName);
}
