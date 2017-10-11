package com.pg.facebook.api.listcustomaudiences;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.LongCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;

import com.pg.facebook.api.FacebookApiClient;
import com.pg.facebook.api.connector.data.FacebookApiConnectorPortObject;
import com.restfb.Connection;
import com.restfb.types.ads.AdAccount;
import com.restfb.types.ads.CustomAudience;

/**
 * This is the model implementation of ListCustomAudiences.
 * 
 *
 * @author P&G, DataScience
 */
public class ListCustomAudiencesNodeModel extends NodeModel {
    
    /**
     * Constructor for the node model.
     */
    protected ListCustomAudiencesNodeModel() {
    
    	 super(
         		new PortType[] { FacebookApiConnectorPortObject.TYPE }, 
         		new PortType[] { BufferedDataTable.TYPE }
         );
    }

    @Override
    protected PortObject[] execute(PortObject[] inObjects, ExecutionContext exec)
    		throws Exception {
    	
    	if ( !(inObjects[0] instanceof FacebookApiConnectorPortObject) ) {
    		throw new Exception("Invalid input port type");
    	}
    	
    	DataTableSpec outSpec = createSpec();
        BufferedDataContainer outContainer = exec.createDataContainer(outSpec);
    	
    	FacebookApiClient client = ((FacebookApiConnectorPortObject)inObjects[0]).getFacebookApiClient();
    	Connection<AdAccount> accountsConnection = client.getAdAccounts();
    	Iterator<List<AdAccount>> accountIterator = accountsConnection.iterator();
    	
    	int i = 0;
    	while ( accountIterator.hasNext() ) {
    		for ( AdAccount account : accountIterator.next() ) {
    		
    			Iterator<List<CustomAudience>> audienceIterator = client.getCustomAudiences(account.getId()).iterator();
    			while ( audienceIterator.hasNext() ) {
    				for ( CustomAudience audience : audienceIterator.next() ) {
    					
    					List<DataCell> cells = new ArrayList<DataCell>(outSpec.getNumColumns());
    	            	
    	        		cells.add(new StringCell(account.getId()));
    	        		
    	        		cells.add(new StringCell(audience.getId()));
    	        		cells.add(new StringCell(audience.getName()));
    	        		cells.add(new StringCell(audience.getDeliveryStatus().getDescription()));
    	        		cells.add(new StringCell(audience.getDescription()));
    	        		cells.add(new LongCell(audience.getApproximateCount()));
    	        		
    	        		
    	        		outContainer.addRowToTable(new DefaultRow("Row" + i++, cells));	
    					
        			}
    			}
    		}
    	}
    	
    	outContainer.close();
    	return new PortObject[]{outContainer.getTable()};
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
    	
    	return new PortObjectSpec[] { createSpec() };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
         // TODO: generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        // TODO: generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        // TODO: generated method stub
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
    
    private DataTableSpec createSpec() {
        List<DataColumnSpec> colSpecs = new ArrayList<DataColumnSpec>(2);
        
        // Add additional columns to DataSpec
        colSpecs.add(new DataColumnSpecCreator("Ad Account Id", StringCell.TYPE).createSpec());
        
        colSpecs.add(new DataColumnSpecCreator("Audience Id", StringCell.TYPE).createSpec());
        colSpecs.add(new DataColumnSpecCreator("Audience Name", StringCell.TYPE).createSpec());
        colSpecs.add(new DataColumnSpecCreator("Delivery Status", StringCell.TYPE).createSpec());
        colSpecs.add(new DataColumnSpecCreator("Description", StringCell.TYPE).createSpec());
        colSpecs.add(new DataColumnSpecCreator("Approximate Count", LongCell.TYPE).createSpec());
        
        return new DataTableSpec(colSpecs.toArray(new DataColumnSpec[colSpecs.size()]));
    }

}

