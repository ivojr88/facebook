package com.pg.facebook.api.deletecustomaudience;

import javax.swing.JTextField;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.port.PortObjectSpec;

import com.pg.knime.node.StandardNodeDialogPane;

/**
 * <code>NodeDialog</code> for the "DeleteCustomAudience" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author P&G, Data Science
 */
public class DeleteCustomAudienceNodeDialog extends StandardNodeDialogPane {

	private DeleteCustomAudienceConfiguration config = new DeleteCustomAudienceConfiguration();
	
	private JTextField txtAudienceId = new JTextField();
	
    /**
     * New pane for configuring the DeleteCustomAudience node.
     */
    protected DeleteCustomAudienceNodeDialog() {

    	addTab(
        		"Settings", 
        		buildStandardPanel(
        			new PanelBuilder()
        				.add("Audience Id", txtAudienceId )
        				.build()
        		)
        	);
    	
    }

	@Override
	protected void saveSettingsTo(NodeSettingsWO settings) throws InvalidSettingsException {
		config.setAudienceId(txtAudienceId.getText());
		config.save(settings);
		
	}
	
	@Override
	protected void loadSettingsFrom(NodeSettingsRO settings, PortObjectSpec[] specs) throws NotConfigurableException {
		config.load(settings);
		txtAudienceId.setText(config.getAudienceId());
	}
	
}

