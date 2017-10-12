package com.pg.facebook.api.createcustomaudience;

import java.util.Iterator;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.port.PortObjectSpec;

import com.pg.facebook.api.FacebookApiClient;
import com.pg.facebook.api.connector.data.FacebookApiConnectionPortObjectSpec;
import com.pg.knime.node.SortedComboBoxModel;
import com.pg.knime.node.StandardNodeDialogPane;
import com.restfb.Connection;
import com.restfb.types.ads.AdAccount;

/**
 * <code>NodeDialog</code> for the "CreateCustomAudience" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author P&G, DataScience
 */
public class CreateCustomAudienceNodeDialog extends StandardNodeDialogPane {

	private CreateCustomAudienceConfiguration config = new CreateCustomAudienceConfiguration();
	
	private JTextField txtAudienceName = new JTextField();
	private SortedComboBoxModel<String> accounts = new SortedComboBoxModel<String>();
	
    /**
     * New pane for configuring the CreateCustomAudience node.
     */
    protected CreateCustomAudienceNodeDialog() {

    	JComboBox<String> cbxAccounts = new JComboBox<String>(accounts);
    	
    	addTab(
        		"Settings", 
        		buildStandardPanel(
        			new PanelBuilder()
        				.add("Accounts", cbxAccounts )
        				.add("Audience Name", txtAudienceName)
        				.build()
        		)
        	);
    	
    }

	@Override
	protected void saveSettingsTo(NodeSettingsWO settings) throws InvalidSettingsException {
		
		config.setAudienceName(txtAudienceName.getText());
		config.setAddAccountId(accounts.getSelectedItem().toString());
		config.save(settings);
	}
	
	@Override
	protected void loadSettingsFrom(NodeSettingsRO settings, PortObjectSpec[] specs) throws NotConfigurableException {
		
		if (! (specs[0] instanceof FacebookApiConnectionPortObjectSpec ) ) {
			throw new NotConfigurableException("Please connect to Facebook API Connector before configurating");
		}
		      
		FacebookApiClient client = ((FacebookApiConnectionPortObjectSpec)specs[0]).getFacebookClient();
		if ( client == null || !client.isConfigured() ) {
			throw new NotConfigurableException("Please execute Facebook API Connector before configurating");
		}
		
		config = new CreateCustomAudienceConfiguration();
		config.load( settings );
		
		txtAudienceName.setText(config.getAudienceName());
		
		accounts.removeAllElements();
		
		Iterator<List<AdAccount>> accountsIterator = client.getAdAccounts().iterator();
		while ( accountsIterator.hasNext() ) {
			for ( AdAccount account : accountsIterator.next() ) {
				accounts.addElement(account.getAccountId());
			}
		}
		
		if ( config != null && !"".equals(config.getAddAccountId())) {
			accounts.setSelectedItem(config.getAddAccountId());
		}
		
	}
	
}

