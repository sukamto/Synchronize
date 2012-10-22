/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.filemethodsservice;

import java.io.File;
import java.sql.Connection;

/**
 *
 * @author z0006cpz
 */
public interface FileNameInterface {

    public void storeSuccessfulFile(Connection con, File file);

    public void storeErrorFile(Connection con, File file);
}
