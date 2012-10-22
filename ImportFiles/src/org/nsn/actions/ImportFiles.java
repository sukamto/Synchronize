/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nsn.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.nsn.importfiles.ImportThreading;
import org.nsn.importfiles.ScheduledRunner;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "Edit",
id = "org.nsn.actions.ImportFiles")
@ActionRegistration(iconBase = "org/nsn/actions/rsz_graph.png",
displayName = "#CTL_ImportFiles")
@ActionReferences({
    @ActionReference(path = "Menu/Import", position = 3333),
    @ActionReference(path = "Toolbars/Import", position = 3333)
})
@Messages("CTL_ImportFiles=Import files")
public final class ImportFiles implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        ScheduledRunner runner = new ScheduledRunner();
        runner.startScheduler();
//        ImportThreading importFiles = new ImportThreading();
//        importFiles.importFiles();
    }
}
