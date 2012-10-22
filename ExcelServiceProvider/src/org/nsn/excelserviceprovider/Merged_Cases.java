/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.excelserviceprovider;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author z0006cpz
 */
public class Merged_Cases {

    private String cellValue;
    private List<String> cases = new ArrayList<String>();

    public Merged_Cases(String cellValue) {
        this.cellValue = cellValue;
    }

    public List<String> getCases() {
        setCases();
        return cases;
    }

    private void setCases() {
        String str = "";
        for (char c : cellValue.toCharArray()) {
            if (c != '\n') {
                str += c;
            } else if (stringStartsWithNumber(str)) {
                cases.add(str);
                str = "";
            } else {
                if (!str.isEmpty()) {
                    updateLastCase(str);
                    str = "";
                }
            }
        }
        if (!str.isEmpty()) {
            cases.add(str);
        }
    }

    private boolean stringStartsWithNumber(String str) {
        if (str.isEmpty()) {
            return false;
        }
        str=str.trim();
        char c = str.charAt(0);
        for (int i = 48; i <= 57; i++) {
            if (c == i) {
                return true;
            }
        }
        return false;
    }

    private void updateLastCase(String str) {
        if (!cases.isEmpty()) {
            int lastItem = cases.size() - 1;
            cases.set(lastItem, cases.get(lastItem).trim() + " " + str);
        }
    }
}
