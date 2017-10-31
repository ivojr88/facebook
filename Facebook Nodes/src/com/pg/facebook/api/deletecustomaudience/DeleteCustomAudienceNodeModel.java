package com.pg.facebook.api.deletecustomaudience;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.def.DefaultRow;
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

import com.facebook.ads.sdk.APIException;
import com.pg.facebook.api.FacebookApiClient;
import com.pg.facebook.api.connector.data.FacebookApiConnectorPortObject;

/**
 * This is the model implementation of DeleteCustomAudience.
 * 
 *
 * @author P&G, Data Science
 */
public class DeleteCustomAudienceNodeModel extends NodeModel {
    
	private DeleteCustomAudienceConfiguration config = new DeleteCustomAudienceConfiguration();
	
    /**
     * Constructor for the node model.
     */
    protected DeleteCustomAudienceNodeModel() {
    
    	super(
         		new PortType[] { FacebookApiConnectorPortObject.TYPE }, 
         		new PortType[] { BufferedDataTable.TYPE }
         );
    }

    @Override
    protected PortObject[] execute(PortObject[] inObjects, ExecutionContext exec) throws Exception {
    	
    	if ( 
    			!(inObjects[0] instanceof FacebookApiConnectorPortObject)
    	) {
    		throw new Exception("Invalid input port types");
    	}
    	
    	FacebookApiClient client = ((FacebookApiConnectorPortObject)inObjects[0]).getFacebookApiClient();
    	
    	DataTableSpec outSpec = createSpec();
        
    	BufferedDataContainer outContainer = exec.createDataContainer(outSpec);
    	List<DataCell> cells = new ArrayList<DataCell>(outSpec.getNumColumns());
    	cells.add(new StringCell(config.getAudienceId()));
    	
    	
    	try { 
    		client.deleteCustomAudience(config.getAudienceId());
    		cells.add(new StringCell("deleted"));
        	
    	} catch ( APIException apiexc ) {
    		cells.add(new StringCell("failed"));
    	}
    	
    	outContainer.addRowToTable(new DefaultRow("Row" + 0, cells));
        outContainer.close();
    	
        return new PortObject[]{outContainer.getTable()};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        
    }

    @Override
    protected PortObjectSpec[] configure(PortObjectSpec[] inSpecs) throws InvalidSettingsException {
    	return new PortObjectSpec[] { createSpec() };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
         config.save(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        config.load(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        
    	config.load(settings);
    	
    	if ( config.getAudienceId() == null || "".equals(config.getAudienceId() ))
    		throw new InvalidSettingsException("Audience Id is required");
    	
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
       
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
       
    }

    private DataTableSpec createSpec() {
        List<DataColumnSpec> colSpecs = new ArrayList<DataColumnSpec>(2);
        
        // Add additional columns to DataSpec
        colSpecs.add(new DataColumnSpecCreator("Audience Id", StringCell.TYPE).createSpec());
        colSpecs.add(new DataColumnSpecCreator("Status", StringCell.TYPE).createSpec());
        
        return new DataTableSpec(colSpecs.toArray(new DataColumnSpec[colSpecs.size()]));
    }
    
}

