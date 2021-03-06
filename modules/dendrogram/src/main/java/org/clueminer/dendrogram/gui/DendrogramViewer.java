package org.clueminer.dendrogram.gui;

import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.EventListenerList;
import org.clueminer.clustering.api.Clustering;
import org.clueminer.clustering.api.ClusteringListener;
import org.clueminer.clustering.api.HierarchicalResult;
import org.clueminer.clustering.api.dendrogram.TreeCluster;
import org.clueminer.clustering.api.dendrogram.TreeListener;
import org.clueminer.clustering.gui.ClusterPreviewer;
import org.clueminer.clustering.api.dendrogram.DendroViewer;
import org.clueminer.clustering.api.dendrogram.DendrogramDataEvent;
import org.clueminer.clustering.api.dendrogram.DendrogramDataListener;
import org.clueminer.clustering.api.dendrogram.DendrogramMapping;
import org.clueminer.project.api.ProjectController;
import org.clueminer.project.api.Workspace;
import org.clueminer.utils.Exportable;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Barton
 */
public class DendrogramViewer extends JPanel implements Exportable, AdjustmentListener, DendroViewer {

    private static final long serialVersionUID = -9145028094444482028L;
    protected ArrayList<TreeCluster> clusters;
    protected DendrogramPanel dendrogramPanel;
    private JScrollPane scroller;
    protected Dimension elementSize;
    protected DendrogramMapping data;
    private boolean fitToPanel = true;
    private final transient EventListenerList datasetListeners = new EventListenerList();
    private final transient EventListenerList clusteringListeners = new EventListenerList();
    private static final Logger logger = Logger.getLogger(DendrogramViewer.class.getName());

    public DendrogramViewer() {
        setBackground(Color.WHITE);
        elementSize = new Dimension(15, 15);
        initComponents();
        this.setDoubleBuffered(true);
    }

    @Override
    public String getName() {
        if (data != null) {
            return data.getDataset().getName();
        } else {
            return DendrogramViewer.class.getName();
        }
    }

    @Override
    public void setDataset(DendrogramMapping dataset) {
        this.data = dataset;
        fireDatasetChanged(new DendrogramDataEvent(this));
        updateLayout();
    }

    public DendrogramMapping getDendrogramData() {
        return this.data;
    }

    public void setClusters(ArrayList<TreeCluster> clusters) {
        this.clusters = clusters;
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 1;
        dendrogramPanel = new DendrogramPanel(this);
        // header = new ExperimentHeader();
        // add(header);
        //    colorBar.setClusters(clusters); //
        // add(colorBar);
        scroller = new JScrollPane();
        scroller.getViewport().add(dendrogramPanel);
        scroller.getViewport().setDoubleBuffered(true);
        scroller.setVisible(true);
        scroller.getVerticalScrollBar().addAdjustmentListener(this);
        setVisible(true);
        this.addComponentListener(new ViewerComponentListener(this));
        this.addDendrogramDataListener(dendrogramPanel);
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        add(scroller, c);
    }

    private void updateLayout() {
        int heatmapSize = this.getSize().width - dendrogramPanel.getVerticalTreeSize().width - dendrogramPanel.getAnnotationWidth();
        /*  System.out.println("annotation panel = " + dendrogramPanel.getAnnotationWidth());

         System.out.println("vertical tree = " + dendrogramPanel.getVerticalTreeSize().width);
         System.out.println("heatmap " + dendrogramPanel.getHeatmapSize());
         System.out.println("clustering view size" + this.getSize() + " heatmap size= " + heatmapSize);

         //System.out.println("feature length = "+featuresLenght+ " heat map width = "+dendrogramPanel.getHeatmapWidth() );
         //dendrogramPanel.getHeatmapWidth() / featuresLenght;///data.getFeaturesSize();
         System.out.println("new element size " + elementSize);*/
        //setMinimumSize(dendrogramPanel.getSize());
        //update size of scrollbars
        scroller.getViewport().revalidate();
        repaint();
    }

    public void setCellHeight(int height, boolean isAdjusting, Object source) {
        if (height > 0) {
            elementSize.height = height;
            fireCellHeightChanged(new DendrogramDataEvent(this), height, isAdjusting, source);
            updateLayout();
        }
    }

    @Override
    public void setFitToPanel(boolean fitToPanel) {
        this.fitToPanel = fitToPanel;
        updateLayout();
    }

    public boolean isFitToPanel() {
        return fitToPanel;
    }

    public int getCellHeight() {
        return elementSize.height;
    }

    @Override
    public void setCellWidth(int width, boolean isAdjusting, Object source) {
        if (width > 0) {
            elementSize.width = width;
            fireCellWidthChanged(new DendrogramDataEvent(this), width, isAdjusting, source);
            updateLayout();
        }
    }

    public int getCellWidth() {
        return elementSize.width;
    }

    public void setHorizontalTreeVisible(boolean show) {
        dendrogramPanel.setHorizontalTreeVisible(show);
    }

    public boolean isHorizontalTreeVisible() {
        return dendrogramPanel.isHorizontalTreeVisible();
    }

    public void setVerticalTreeVisible(boolean show) {
        dendrogramPanel.setVerticalTreeVisible(show);
    }

    public boolean isVerticalTreeVisible() {
        return dendrogramPanel.isVerticalTreeVisible();
    }

    public void setLegendVisible(boolean show) {
        dendrogramPanel.setLegendVisible(show);
    }

    public boolean isLegendVisible() {
        return dendrogramPanel.isLegendVisible();
    }

    public void setLabelsVisible(boolean show) {
        dendrogramPanel.setLabelsVisible(show);
    }

    public boolean isLabelVisible() {
        return dendrogramPanel.isLabelVisible();
    }

    public boolean fireDatasetChanged(DendrogramDataEvent evt) {
        DendrogramDataListener[] listeners;

        if (datasetListeners != null) {
            listeners = datasetListeners.getListeners(DendrogramDataListener.class);
            for (DendrogramDataListener listener : listeners) {
                listener.datasetChanged(evt, data);
            }
        }
        return true;
    }

    public boolean fireCellWidthChanged(DendrogramDataEvent evt, int width, boolean isAdjusting, Object source) {
        DendrogramDataListener[] listeners;

        if (datasetListeners != null) {
            listeners = datasetListeners.getListeners(DendrogramDataListener.class);
            for (DendrogramDataListener listener : listeners) {
                if (listener != source) {
                    listener.cellWidthChanged(evt, width, isAdjusting);
                }
            }
        }
        return true;
    }

    protected boolean fireCellHeightChanged(DendrogramDataEvent evt, int height, boolean isAdjusting, Object source) {
        DendrogramDataListener[] listeners;

        if (datasetListeners != null) {
            listeners = datasetListeners.getListeners(DendrogramDataListener.class);
            for (DendrogramDataListener listener : listeners) {
                if (listener != source) {
                    listener.cellHeightChanged(evt, height, isAdjusting);
                }
            }
        }
        return true;
    }

    @Override
    public void addDendrogramDataListener(DendrogramDataListener listener) {
        datasetListeners.add(DendrogramDataListener.class, listener);
    }

    @Override
    public void removeDendrogramDataListener(DendrogramDataListener listener) {
        datasetListeners.remove(DendrogramDataListener.class, listener);
    }

    public void addRowsTreeListener(TreeListener listener) {
        dendrogramPanel.addRowsTreeListener(listener);
    }

    public void addClusteringListener(ClusteringListener listener) {
        clusteringListeners.add(ClusteringListener.class, listener);
    }

    public void removeClusteringListener(ClusteringListener listener) {
        clusteringListeners.remove(ClusteringListener.class, listener);
    }

    public void fireClusteringChanged(Clustering clust) {
        ClusteringListener[] listeners;

        listeners = clusteringListeners.getListeners(ClusteringListener.class);
        for (ClusteringListener listener : listeners) {
            listener.clusteringChanged(clust);
        }
    }

    @Override
    public Dimension getElementSize() {
        return elementSize;
    }

    public void setElementSize(Dimension elementSize) {
        this.elementSize = elementSize;
    }

    public void fireResultUpdate(HierarchicalResult clust) {
        ClusteringListener[] listeners;

        listeners = clusteringListeners.getListeners(ClusteringListener.class);
        System.out.println("fireing results update, listeners size: " + listeners.length);
        for (ClusteringListener listener : listeners) {
            listener.resultUpdate(clust);
            System.out.println("listerner: " + listener.getClass().toString());
        }
    }

    @Override
    public BufferedImage getBufferedImage(int w, int h) {
        Dimension dendroDim = dendrogramPanel.getPreferredSize();
        int width, height;

        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        Workspace workspace = pc.getCurrentWorkspace();
        JPanel previews = null;
        if (workspace != null) {
            previews = (JPanel) workspace.getLookup().lookup(ClusterPreviewer.class);
        }
        Dimension dimPrev = null;
        BufferedImage prev = null;
        if (previews != null) {
            dimPrev = previews.getSize();
            prev = new BufferedImage(dimPrev.width, dimPrev.height, BufferedImage.TYPE_INT_RGB);
            Graphics2D gp = prev.createGraphics();
            previews.paint(gp);
        }

        width = dendroDim.width;
        height = dendroDim.height;
        if (dimPrev != null) {
            width += dimPrev.width;
            height = Math.max(dendroDim.height, dimPrev.height);
        }

        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bi.createGraphics();
        g.setPaint(Color.WHITE);
        g.fillRect(0, 0, width, height);
        dendrogramPanel.paint(g);
        logger.log(Level.INFO, "exporting dendrogram to bitmap, export size: {0}x{1}", new Object[]{width, height});
        if (prev != null) {
            //combine images
            g.drawImage(prev, dendroDim.width, 0, null);
        }

        g.dispose();
        return bi;
    }

    @Override
    public void adjustmentValueChanged(AdjustmentEvent e) {
        // System.out.println("adjusted");
     /*
         * dendrogramPanel.updateSize();
         * scroller.setPreferredSize(dendrogramPanel.getSize());
         * scroller.getViewport().revalidate();
         * scroller.getViewport().repaint();
         */
        //  dendrogramPanel.heatmap.invalidate();
    }

    @Override
    public void setClustering(Clustering clustering) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setEvaluationVisible(boolean show) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void fireRowMappingChanged(Object source, HierarchicalResult rows) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void fireColumnsMappingChanged(Object source, HierarchicalResult columns) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public DendrogramMapping getDendrogramMapping() {
        return data;
    }

    @Override
    public boolean isEvaluationVisible() {
        return false;
    }

    private class ViewerComponentListener implements ComponentListener {

        DendrogramViewer viewer;

        ViewerComponentListener(DendrogramViewer inst) {
            viewer = inst;
        }

        @Override
        public void componentResized(ComponentEvent ce) {
            Component c = (Component) ce.getSource();
            //this method invalidate buffered image which is usually necessary
            //on component dimensions change (in case that the heatmap panel is bigger
            //than the frame)
            dendrogramPanel.getHeatmap().resetCache();
            viewer.updateLayout();
            repaint();
        }

        @Override
        public void componentMoved(ComponentEvent ce) {
            //throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void componentShown(ComponentEvent ce) {
            Component c = (Component) ce.getSource();
            System.out.println(c.getName() + " component shown");
            viewer.updateLayout();
        }

        @Override
        public void componentHidden(ComponentEvent ce) {
            //throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
