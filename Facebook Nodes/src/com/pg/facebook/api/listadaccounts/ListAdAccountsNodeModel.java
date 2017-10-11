package com.pg.facebook.api.listadaccounts;

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
import org.knime.core.data.def.IntCell;
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

/**
 * This is the model implementation of ListAdAccounts.
 * 
 *
 * @author P&G, Data Science
 */
public class ListAdAccountsNodeModel extends NodeModel {
    
    /**
     * Constructor for the node model.
     */
    protected ListAdAccountsNodeModel() {
    
    	   super(
           		new PortType[] { FacebookApiConnectorPortObject.TYPE }, 
           		new PortType[] { BufferedDataTable.TYPE }
           );
    }

    /**
     * {@inheritDoc}
     */
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
    	Iterator<List<AdAccount>> iterator = accountsConnection.iterator();
    	
    	int i = 0;
    	do {
    		exec.checkCanceled();
    		
    		for ( AdAccount account : iterator.next() ) {
    			
    			List<DataCell> cells = new ArrayList<DataCell>(outSpec.getNumColumns());
            	
        		cells.add(new StringCell(account.getId()));
        		cells.add(new IntCell(account.getAccountStatus()));
        		cells.add(new StringCell(account.getAmountSpent()));
            	
        		outContainer.addRowToTable(new DefaultRow("Row" + i++, cells));
    		}
    		
    	} while ( iterator.hasNext() );
        
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
        
    }
    
    private DataTableSpec createSpec() {
        List<DataColumnSpec> colSpecs = new ArrayList<DataColumnSpec>(2);
        
        // Add additional columns to DataSpec
        colSpecs.add(new DataColumnSpecCreator("Account Id", StringCell.TYPE).createSpec());
        colSpecs.add(new DataColumnSpecCreator("Account Status", IntCell.TYPE).createSpec());
        colSpecs.add(new DataColumnSpecCreator("Amount Spent", StringCell.TYPE).createSpec());
                
        return new DataTableSpec(colSpecs.toArray(new DataColumnSpec[colSpecs.size()]));
    }

}

