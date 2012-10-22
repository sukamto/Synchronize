/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.importfiles;

import java.sql.Connection;
import org.databaseservice.IndexInterface;
import org.openide.util.Lookup;

/**
 *
 * @author z0006cpz
 */
public class IndexRunner implements Runnable {

    private Connection con;

    public IndexRunner(Connection con) {
        this.con = con;
    }

    @Override
    public void run() {
        IndexInterface iI = Lookup.getDefault().lookup(IndexInterface.class);
        iI.reOrganizeAll(con);
    }
}
