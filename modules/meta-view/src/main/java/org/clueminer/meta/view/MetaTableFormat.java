package org.clueminer.meta.view;

import ca.odell.glazedlists.gui.TableFormat;
import org.clueminer.meta.api.MetaResult;

/**
 *
 * @author Tomas Barton
 */
public class MetaTableFormat implements TableFormat<MetaResult> {

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "k";
            case 1:
                return "score";
            case 2:
                return "fingerprint";
            case 3:
                return "template";
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public Object getColumnValue(MetaResult e, int column) {
        switch (column) {
            case 0:
                return e.getK();
            case 1:
                return e.getScore();
            case 2:
                return e.getFingerprint();
            case 3:
                return e.getTemplate();
            default:
                throw new IllegalStateException();
        }
    }
}
