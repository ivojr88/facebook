package com.pg.facebook.api.insights.node;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.date.DateAndTimeCell;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;

import com.pg.facebook.api.FacebookApiClient;
import com.pg.facebook.api.FacebookInsight;
import com.pg.facebook.api.FacebookInsightFactory;
import com.pg.facebook.api.connector.data.FacebookApiConnectorPortObject;
import com.restfb.Connection;
import com.restfb.types.Insight;

/**
 * This is the model implementation of Insights.
 * 
 *
 * @author P&G, eBusiness
 */
public class InsightsNodeModel extends NodeModel {
    
	private FacebookInsightsConfiguration config;
	
	private static final NodeLogger LOGGER = NodeLogger.getLogger(InsightsNodeModel.class);
	
    /**
     * Constructor for the node model.
     */
    protected InsightsNodeModel() {
    	super(
    		new PortType[] {FacebookApiConnectorPortObject.TYPE},
    		new PortType[] {BufferedDataTable.TYPE,BufferedDataTable.TYPE_OPTIONAL,BufferedDataTable.TYPE_OPTIONAL,BufferedDataTable.TYPE_OPTIONAL }
    	);
    	
    }
    
    @Override
    protected PortObject[] execute(PortObject[] inObjects, ExecutionContext exec)
    		throws Exception {
    	
    	if (!( inObjects[0] instanceof FacebookApiConnectorPortObject ) ) {
    		throw new Exception("Invalid input class type");
    	}
    	
    	FacebookApiConnectorPortObject portObject = (FacebookApiConnectorPortObject)inObjects[0];
    	FacebookApiClient client = portObject.getFacebookApiClient();
    	
    	HashMap<String, Set<String>> metricGroups = config.getMetricGroups();
    	BufferedDataTable[] tables = new BufferedDataTable[] {EMPTY_TABLE(exec),EMPTY_TABLE(exec),EMPTY_TABLE(exec),EMPTY_TABLE(exec) };
    	
    	String startDate = config.getUseStartDate() ? config.getAdjustedStartDate() : "";
    	String endDate = config.getUseEndDate() ? config.getAdjustedEndDate() : "";
    	
    	int tableCount = 0;
    	for ( String group : metricGroups.keySet() ) {
    		
    		Set<String> metrics = metricGroups.get(group);
    		DataTableSpec tableSpec = createSpec();
    		BufferedDataContainer outContainer = exec.createDataContainer(tableSpec);
    		
    		exec.checkCanceled();
    		
    		Connection<Insight> connection = client.getInsights(config.getFacebookPeriodIdentifier(), metrics.toArray(new String[]{}), startDate, endDate);
    		List<Insight> insights = connection.getData();
    		
    		int i = 0;
    		for ( Insight insight : insights ) {
    			List<FacebookInsight> facebookInsights = null;
    			try {
    				facebookInsights = FacebookInsightFactory.getFacebookInsights(insight);
    			} catch ( Exception exc ) {
    				LOGGER.error("Error from FacebookInsightsFactory: " + exc.getMessage());
    				
    				throw exc; 
    			}
    			for ( FacebookInsight facebookInsight: facebookInsights ) {
    				
    				LOGGER.debug(facebookInsight.toString());
    				
    				List<DataCell> cells = new ArrayList<DataCell>(tableSpec.getNumColumns());
    				cells.add(new StringCell(portObject.getFacebookApiClient().getImpersonationAccountId()));
    	        	cells.add(new DateAndTimeCell(facebookInsight.getDateAsAdjustedDate().getTime(), true, false, false));
    	        	cells.add(new StringCell(facebookInsight.getPeriod()));
    	        	cells.add(new StringCell(facebookInsight.getMetricname()));
    	        	cells.add(new StringCell(facebookInsight.getDimensionname()));
    	        	cells.add(new DoubleCell(facebookInsight.getValue()));
    	        	cells.add(new StringCell(facebookInsight.getDate()));
    	        	
    	        	outContainer.addRowToTable(new DefaultRow("Row" + i++, cells));
    			}
    		}
    		
    		outContainer.close();
    		tables[tableCount++] = outContainer.getTable();    	
    	}
    	
        return tables;
    	
    }

    private static final BufferedDataTable EMPTY_TABLE(ExecutionContext exec) {
    	DataTableSpec spec = new DataTableSpec();
    	BufferedDataContainer container = exec.createDataContainer(spec);
    	container.close();
    	return container.getTable();
    }
    
    private DataTableSpec createSpec( ) {
    	
    	List<DataColumnSpec> colSpecs = new ArrayList<DataColumnSpec>(5);
    	
    	colSpecs.add(new DataColumnSpecCreator("Object Id", StringCell.TYPE).createSpec());
    	colSpecs.add(new DataColumnSpecCreator("Date", DateAndTimeCell.TYPE).createSpec());
    	colSpecs.add(new DataColumnSpecCreator("Period", StringCell.TYPE).createSpec());
    	colSpecs.add(new DataColumnSpecCreator("Metric Name", StringCell.TYPE).createSpec());
    	colSpecs.add(new DataColumnSpecCreator("Dimension", StringCell.TYPE).createSpec());
    	colSpecs.add(new DataColumnSpecCreator("Value", DoubleCell.TYPE).createSpec());
    	colSpecs.add(new DataColumnSpecCreator("End Time", StringCell.TYPE).createSpec());
    	
        return new DataTableSpec(colSpecs.toArray(new DataColumnSpec[colSpecs.size()]));
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        // TODO: generated method stub
    }

    @Override
    protected PortObjectSpec[] configure(PortObjectSpec[] inSpecs)
    		throws InvalidSettingsException {
    	return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
         if ( config != null ) 
        	 config.save(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        
    	config = new FacebookInsightsConfiguration();
    	config.load(settings);
    	
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        config = new FacebookInsightsConfiguration();
        config.load(settings);
        
        if ( config.getMetricGroups().size() > 4 ) {
        	throw new InvalidSettingsException("Max number of metric types is 4");
        }
        
        if ( config.getUseStartDate() && config.getUseEndDate() ) {
        	try {
            	SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
                if ( SDF.parse(config.getEndDate()).compareTo(SDF.parse(config.getStartDate())) < 0 ) {
                	throw new InvalidSettingsException("Start date must be before End date");
                }
            } catch ( ParseException pexc ) {
            	LOGGER.error ( pexc.getMessage() );
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        // TODO: generated method stub
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        // TODO: generated method stub
    }

}

