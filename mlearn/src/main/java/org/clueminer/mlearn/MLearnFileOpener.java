package org.clueminer.mlearn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.dendrogram.DendrogramTopComponent;
import org.clueminer.importer.ImportController;
import org.clueminer.importer.ImportControllerUI;
import org.clueminer.importer.ImportTask;
import org.clueminer.importer.gui.ImportControllerUIImpl;
import org.clueminer.importer.impl.ImportControllerImpl;
import org.clueminer.io.importer.api.ContainerLoader;
import org.clueminer.openfile.OpenFileImpl;
import org.clueminer.project.ProjectControllerImpl;
import org.clueminer.project.ProjectImpl;
import org.clueminer.project.ProjectInformationImpl;
import org.clueminer.project.api.Project;
import org.clueminer.project.api.Workspace;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.openide.windows.WindowManager;

/**
 *
 * @author Tomas Barton
 */
@org.openide.util.lookup.ServiceProvider(service = org.clueminer.openfile.OpenFileImpl.class, position = 90)
public class MLearnFileOpener implements OpenFileImpl, TaskListener {

    private static Project project;
    private ImportTask importTask;
    private static final RequestProcessor RP = new RequestProcessor("non-interruptible tasks", 1, false);
    private final ImportController controller;
    private final ImportControllerUI controllerUI;
    private static final Logger logger = Logger.getLogger(MLearnFileOpener.class.getName());

    public MLearnFileOpener() {
        controller = new ImportControllerImpl();
        controllerUI = new ImportControllerUIImpl(controller);
    }

    /**
     * Return true is file seems to be in format which is supported by this
     * package
     *
     * @param f
     * @return boolean
     * @throws java.io.FileNotFoundException
     */
    protected boolean isFileSupported(File f) throws FileNotFoundException, IOException {
        return controller.isFileSupported(f);
    }

    @Override
    public boolean open(FileObject fileObject) {
        File f = FileUtil.toFile(fileObject);
        try {
            if (isFileSupported(f)) {
                importTask = controllerUI.importFile(fileObject);
                if (importTask != null) {
                    final RequestProcessor.Task task = RP.create(importTask);
                    task.addTaskListener(this);
                    task.schedule(0);
                } else {
                    logger.log(Level.SEVERE, "failed to create an import task");
                }

                //ImporterUI ui = controller.getUI(im);
                //importer = new MLearnImporter(f);
                //openDataFile(importer);
                return true;
            }
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return false;
    }

    protected String getExtension(String filename) {
        String extension = "";
        int i = filename.lastIndexOf('.');
        int p = Math.max(filename.lastIndexOf('/'), filename.lastIndexOf('\\'));

        if (i > p) {
            extension = filename.substring(i + 1);
        }

        return extension;
    }

    protected String getTitle(String filename) {
        String title = filename;
        int pos = filename.lastIndexOf('.');
        if (pos > -1) {
            title = filename.substring(0, pos).trim();
        }
        return title;
    }

    @Override
    public void taskFinished(Task task) {
        WindowManager.getDefault().invokeWhenUIReady(new Runnable() {
            @Override
            public void run() {
                ContainerLoader container = importTask.getContainer();
                if (container != null) {
                    ProjectControllerImpl pc = Lookup.getDefault().lookup(ProjectControllerImpl.class);
                    project.add(container.getDataset());
                    String filename = importTask.getContainer().getSource();
                    container.getDataset().setName(filename);

                    DendrogramTopComponent tc = new DendrogramTopComponent();

                    tc.setDataset(container.getDataset());
                    //tc.setProject(project);
                    tc.setDisplayName(getTitle(filename));
                    tc.open();
                    tc.requestActive();

                    pc.openProject(project);
                    Workspace workspace = pc.getCurrentWorkspace();
                    if (workspace != null) {
                        workspace.add(container.getDataset());  //add plate to project's lookup
                    }
                    //     DataPreprocessing preprocess = new DataPreprocessing(plate, tc);
                    //     preprocess.start();
                }
            }
        });

    }

    protected void openDataFile(MLearnImporter importer) {
        ProgressHandle ph = ProgressHandleFactory.createHandle("Opening file " + importer.getFile().getName());
        importer.setProgressHandle(ph);
        //Project instance
        project = new ProjectImpl();
        project.getLookup().lookup(ProjectInformationImpl.class).setFile(importer.getFile());
        final RequestProcessor.Task task = RP.create(importer);
        task.addTaskListener(this);
        task.schedule(0);
    }
}
