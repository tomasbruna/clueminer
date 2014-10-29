package org.clueminer.export.impl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.prefs.Preferences;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import org.clueminer.clustering.gui.ClusterAnalysis;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbPreferences;
import org.openide.util.RequestProcessor;
import org.openide.util.TaskListener;
import org.openide.windows.WindowManager;

/**
 *
 * @author Tomas Barton
 */
public abstract class AbstractExporter implements ActionListener, PropertyChangeListener {

    protected static final RequestProcessor RP = new RequestProcessor("Export");
    protected RequestProcessor.Task task;
    protected ClusterAnalysis analysis;
    protected static final String prefKey = "last_folder";
    protected DialogDescriptor dialog = null;
    protected File defaultFolder = null;
    protected FileFilter fileFilter;

    public void setAnalysis(ClusterAnalysis analysis) {
        this.analysis = analysis;
    }

    public abstract JPanel getOptions();

    public abstract String getTitle();

    public abstract String getExtension();

    public abstract void updatePreferences(Preferences p);

    public abstract FileFilter getFileFilter();

    public abstract Runnable getRunner(File file, ClusterAnalysis analysis, Preferences pref, final ProgressHandle ph);

    public void showDialog() {

        dialog = new DialogDescriptor(getOptions(), "Export", true, NotifyDescriptor.OK_CANCEL_OPTION,
                                      NotifyDescriptor.OK_CANCEL_OPTION,
                                      DialogDescriptor.BOTTOM_ALIGN, null, this);

        dialog.setClosingOptions(new Object[]{});
        dialog.addPropertyChangeListener(this);
        DialogDisplayer.getDefault().notifyLater(dialog);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == DialogDescriptor.OK_OPTION) {
            Preferences p = NbPreferences.root().node("/clueminer/exporter");
            updatePreferences(p);
            makeExport(p);

            dialog.setClosingOptions(null);
        } else if (event.getSource() == DialogDescriptor.CANCEL_OPTION) {
            dialog.setClosingOptions(null);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        //not much to do
    }

    protected JFileChooser getFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooser.setDialogTitle(getTitle());
        fileChooser.setAcceptAllFileFilterUsed(true);
        fileChooser.setCurrentDirectory(defaultFolder);

        fileChooser.addChoosableFileFilter(getFileFilter());
        return fileChooser;
    }

    public void makeExport(Preferences pref) {
        if (analysis == null) {
            return;
        }
        String folder = pref.get(prefKey, null);
        if (folder != null) {
            defaultFolder = new File(folder);
        }
        JFileChooser fileChooser = getFileChooser();

        defaultFolder = fileChooser.getCurrentDirectory();
        pref.put(prefKey, fileChooser.getCurrentDirectory().getAbsolutePath());

        if (fileChooser.showSaveDialog(WindowManager.getDefault().getMainWindow()) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String filename = file.getName();

            if (fileChooser.getFileFilter() == fileFilter) {
                if (!filename.endsWith(getExtension())) {
                    file = new File(file.getAbsolutePath() + getExtension());
                }
            }

            Object retval = NotifyDescriptor.YES_OPTION;
            if (file.exists()) {
                NotifyDescriptor d = new NotifyDescriptor.Confirmation(
                        "This file already exists. Do you want to overwrite it?",
                        "Overwrite",
                        NotifyDescriptor.YES_NO_OPTION);
                retval = DialogDisplayer.getDefault().notify(d);
            }

            if (retval.equals(NotifyDescriptor.YES_OPTION)) {
                final ProgressHandle ph = ProgressHandleFactory.createHandle(getTitle() + ":" + file.getName());
                createTask(file, analysis, pref, ph);
            } else {
                makeExport(pref);
            }
        }
    }

    /**
     *
     * @param file
     * @param analysis
     * @param pref
     * @param ph
     */
    protected void createTask(File file, ClusterAnalysis analysis, Preferences pref, final ProgressHandle ph) {
        task = RP.create(getRunner(file, analysis, pref, ph));
        task.addTaskListener(analysis);
        task.addTaskListener(new TaskListener() {
            @Override
            public void taskFinished(org.openide.util.Task task) {
                //make sure that we get rid of the ProgressHandle
                //when the task is finished
                ph.finish();
            }
        });
        task.schedule(0);
    }

}
