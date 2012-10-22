/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.filemethodsservice;

import java.io.File;
import java.sql.Statement;
import java.util.List;

/**
 *
 * @author z0006cpz
 */
public interface FileMethodsServiceInterface {

    public void setStatement(Statement statement);

    public void setROOT(String ROOT);

    public List<File> getFilesNotYetImported();
}
