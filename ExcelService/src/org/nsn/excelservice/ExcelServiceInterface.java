/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.excelservice;

/**
 *
 * @author z0006cpz
 */
public interface ExcelServiceInterface {

    public abstract String convertXLSXtoCSV(String inputFileName, String outputFileName,boolean mergedCells);
}
