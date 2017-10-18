package com.pg.facebook.api.createcustomaudience;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
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

/**
 * This is the model implementation of CreateCustomAudience.
 * 
 *
 * @author P&G, DataScience
 */
public class CreateCustomAudienceNodeModel extends NodeModel {
    
	private CreateCustomAudienceConfiguration config = new CreateCustomAudienceConfiguration();
	
    /**
     * Constructor for the node model.
     */
    protected CreateCustomAudienceNodeModel() {
    
    	super(
         		new PortType[] { FacebookApiConnectorPortObject.TYPE, BufferedDataTable.TYPE }, 
         		new PortType[] { BufferedDataTable.TYPE }
         );
    }

    @Override
    protected PortObject[] execute(PortObject[] inObjects, ExecutionContext exec)
    		throws Exception {
    	
    	if ( 
    			!(inObjects[0] instanceof FacebookApiConnectorPortObject) &&
    			!(inObjects[1] instanceof BufferedDataTable)
    	) {
    		throw new Exception("Invalid input port types");
    	}
    	
    	FacebookApiClient client = ((FacebookApiConnectorPortObject)inObjects[0]).getFacebookApiClient();
    	BufferedDataTable personList = (BufferedDataTable)inObjects[1];
    	
    	List<String> schema = Arrays.asList(personList.getDataTableSpec().getColumnNames());
    	for ( String s : schema ) {
    		if ( !CreateCustomAudienceConfiguration.VALID_COL_NAMES.contains(s) ) {
    			throw new InvalidSettingsException("Invalid column in input table.  Valid columns are: " + String.join(",", CreateCustomAudienceConfiguration.VALID_COL_NAMES) );
    		}
    	}
    	
    	
    	List<List<String>> people = new ArrayList<List<String>>();
    	for ( DataRow row : personList ) {
    		
    		List<String> cols = new ArrayList<String>();
    		for ( int i = 0 ; i < row.getNumCells(); i ++ )
    			cols.add(row.getCell(i).toString());
    		
    		people.add(cols);
    	}
    	
    	String audienceId = client.createCustomAudience(
    			config.getAddAccountId(), 
    			config.getAudienceName(), 
    			schema, 
    			people);
    	
    	DataTableSpec outSpec = createSpec();
        BufferedDataContainer outContainer = exec.createDataContainer(outSpec);
    	List<DataCell> cells = new ArrayList<DataCell>(outSpec.getNumColumns());
    	cells.add(new StringCell(config.getAddAccountId()));
		cells.add(new StringCell(audienceId));
		cells.add(new StringCell(config.getAudienceName()));
		outContainer.addRowToTable(new DefaultRow("Row" + 0, cells));
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
         config.save(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        
    	config = new CreateCustomAudienceConfiguration();
        config.load(settings);
        
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
        colSpecs.add(new DataColumnSpecCreator("Custom Audience Id", StringCell.TYPE).createSpec());
        colSpecs.add(new DataColumnSpecCreator("Custom Audience Name", StringCell.TYPE).createSpec());
        
        return new DataTableSpec(colSpecs.toArray(new DataColumnSpec[colSpecs.size()]));
    }

}

