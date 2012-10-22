/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.excelservice;

import org.openide.util.Lookup;

/**
 *
 * @author z0006cpz
 */
public abstract class ExcelService {

    public static ExcelService getDefault() {
        return Lookup.getDefault().lookup(ExcelService.class);
    }

    public abstract String convertXLSXtoCSV(String filename);
}
