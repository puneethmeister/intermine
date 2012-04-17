package org.intermine.web.logic.widget;

/*
 * Copyright (C) 2002-2011 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.intermine.api.profile.InterMineBag;
import org.intermine.objectstore.ObjectStore;
import org.intermine.pathquery.PathQuery;
import org.intermine.web.logic.widget.config.GraphWidgetConfig;
import org.intermine.webservice.server.exceptions.ResourceNotFoundException;




/**
 * @author "Xavier Watkins"
 * @author "Alex Kalderimis"
 */
public class GraphWidget extends Widget
{
    private static final Logger LOG = Logger.getLogger(GraphWidget.class);
    private int notAnalysed = 0;
    private GraphWidgetLoader grapgWidgetLdr;
    private InterMineBag bag;
    private ObjectStore os;
    private String filter;


    /**
     * @param config config for widget
     * @param interMineBag bag for widget
     * @param os objectstore
     * @param filter filter
     */
    public GraphWidget(GraphWidgetConfig config, InterMineBag interMineBag, ObjectStore os,
                       String filter) {
        super(config);
        this.bag = interMineBag;
        this.os = os;
        this.filter = filter;
        validateBagType();
        process();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public List getElementInList() {
        return new Vector();
    }

    public void validateBagType() {
        String typeClass = config.getTypeClass();
        if (!typeClass.equals(os.getModel().getPackageName() + "." + bag.getType())) {
            throw new ResourceNotFoundException("Could not find an enrichment widget called \""
                    + config.getId() + "\" with type " + bag.getType());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void process() {
        grapgWidgetLdr = new GraphWidgetLoader(bag, os, (GraphWidgetConfig) config, filter);
        if (grapgWidgetLdr == null || grapgWidgetLdr.getResults() == null) {
            LOG.warn("No data found for graph widget");
            return;
        }
        try {
            notAnalysed = bag.getSize() - grapgWidgetLdr.getWidgetTotal();
        } catch (Exception err) {
            LOG.warn("Error rendering graph widget.", err);
            return;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<List<String>> getExportResults(String[] selected)
        throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
 /*   @Override
    public List<List<String[]>> getFlattenedResults() {
        // TODO Auto-generated method stub
        return null;
    }*/

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getHasResults() {
        return (grapgWidgetLdr != null
                && grapgWidgetLdr.getResults() != null
                && grapgWidgetLdr.getResults().size() > 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNotAnalysed(int notAnalysed) {
        this.notAnalysed = notAnalysed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNotAnalysed() {
        return notAnalysed;
    }

    @Override
    public List<List<Object>> getResults() {
        return grapgWidgetLdr.getResultTable();
    }

    @Override
    public PathQuery getPathQuery() {
        return grapgWidgetLdr.createPathQuery();
    }

    /**
     * class used to format the p-values on the graph
     * @author julie
     */
    public class DivNumberFormat extends DecimalFormat
    {
        /**
         * Generated serial-id.
         */
        private static final long serialVersionUID = 8247038065756921184L;
        private int magnitude;

        /**
         * @param magnitude what to multiply the p-value by
         */
        public DivNumberFormat(int magnitude) {
            this.magnitude = magnitude;
        }

        /**
         * @param number number to format
         * @param result buffer to put the result in
         * @param fieldPosition the field position
         * @return the format
         */
        @Override
        public StringBuffer format(double number, StringBuffer result, FieldPosition fieldPosition)
        {
            return super.format(number * magnitude, result, fieldPosition);
        }

        /**
         * @param number number to format
         * @param result buffer to put the result in
         * @param fieldPosition the field position
         * @return the format
         */
        @Override
        public StringBuffer format(long number, StringBuffer result, FieldPosition fieldPosition) {
            return super.format(number * magnitude, result, fieldPosition);
        }
    }
}

