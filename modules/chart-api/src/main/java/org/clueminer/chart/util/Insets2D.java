package org.clueminer.chart.util;

import java.io.Serializable;
import java.util.Locale;

/**
 * Abstract class that stores insets for all four directions.
 * <p>
 * Please use this instead of java.awt.Insets, as the java class does not
 * support double values.</p>
 */
public abstract class Insets2D implements Serializable {

    /**
     * Version id for serialization.
     */
    private static final long serialVersionUID = 7585228413052838087L;

    /**
     * Creates a new Insets2D object.
     */
    public Insets2D() {
    }

    /**
     * Returns the insets at the top.
     *
     * @return Top insets.
     */
    public abstract double getTop();

    /**
     * Returns the insets at the left.
     *
     * @return Left insets.
     */
    public abstract double getLeft();

    /**
     * Returns the insets at the bottom.
     *
     * @return Bottom insets.
     */
    public abstract double getBottom();

    /**
     * Returns the insets at the right.
     *
     * @return Right insets.
     */
    public abstract double getRight();

    /**
     * Returns the sum of horizontal insets.
     *
     * @return Horizontal insets.
     */
    public double getHorizontal() {
        return getRight() + getLeft();
    }

    /**
     * Returns the sum of vertical insets.
     *
     * @return Vertical insets.
     */
    public double getVertical() {
        return getTop() + getBottom();
    }

    /**
     * Sets the insets according to the specified insets.
     *
     * @param insets Insets to be set.
     */
    public abstract void setInsets(Insets2D insets);

    /**
     * Sets the insets to the specified values.
     *
     * @param top    Top insets.
     * @param left   Left insets.
     * @param bottom Bottom insets.
     * @param right  Right insets.
     */
    public abstract void setInsets(double top, double left,
            double bottom, double right);

    /**
     * Class that stores insets as double values.
     */
    public static class Double extends Insets2D {

        /**
         * Version id for serialization.
         */
        private static final long serialVersionUID = -6637052175330595647L;

        /**
         * Top.
         */
        private double top;
        /**
         * Left.
         */
        private double left;
        /**
         * Bottom.
         */
        private double bottom;
        /**
         * Right.
         */
        private double right;

        /**
         * Creates a new Insets2D object with zero insets.
         */
        public Double() {
            this(0.0);
        }

        /**
         * Creates a new Insets2D object with the specified insets in all
         * directions.
         *
         * @param inset Inset value.
         */
        public Double(double inset) {
            this(inset, inset, inset, inset);
        }

        /**
         * Creates a new Insets2D object with the specified insets.
         *
         * @param top    Top insets.
         * @param left   Left insets.
         * @param bottom Bottom insets.
         * @param right  Right insets.
         */
        public Double(double top, double left, double bottom, double right) {
            setInsets(top, left, bottom, right);
        }

        @Override
        public double getTop() {
            return top;
        }

        @Override
        public double getLeft() {
            return left;
        }

        @Override
        public double getBottom() {
            return bottom;
        }

        @Override
        public double getRight() {
            return right;
        }

        @Override
        public void setInsets(Insets2D insets) {
            if (insets == null) {
                return;
            }
            setInsets(insets.getTop(), insets.getLeft(),
                      insets.getBottom(), insets.getRight());
        }

        @Override
        public void setInsets(double top, double left,
                double bottom, double right) {
            this.top = top;
            this.left = left;
            this.bottom = bottom;
            this.right = right;
        }

        @Override
        public String toString() {
            return String.format(Locale.US,
                                 "%s[top=%f, left=%f, bottom=%f, right=%f]", //$NON-NLS-1$
                                 getClass().getName(), top, left, bottom, right);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Insets2D)) {
                return false;
            }
            Insets2D insets = (Insets2D) obj;
            return (getTop() == insets.getTop())
                    && (getLeft() == insets.getLeft())
                    && (getBottom() == insets.getBottom())
                    && (getRight() == insets.getRight());
        }

        @Override
        public int hashCode() {
            long bits = java.lang.Double.doubleToLongBits(getTop());
            bits += java.lang.Double.doubleToLongBits(getLeft()) * 37;
            bits += java.lang.Double.doubleToLongBits(getBottom()) * 43;
            bits += java.lang.Double.doubleToLongBits(getRight()) * 47;
            return ((int) bits) ^ ((int) (bits >> 32));
        }
    }

}
