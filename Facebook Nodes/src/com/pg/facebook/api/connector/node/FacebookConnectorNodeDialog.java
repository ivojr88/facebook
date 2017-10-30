package com.pg.facebook.api.connector.node;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.nio.charset.Charset;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextArea;

import org.apache.http.client.utils.URLEncodedUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;

import com.pg.knime.node.StandardTrackedNodeDialogPane;

/**
 * <code>NodeDialog</code> for the "FacebookConnector" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author P&G, eBusiness Analytics
 */
public class FacebookConnectorNodeDialog extends StandardTrackedNodeDialogPane {

	private FacebookConnectorConfiguration configuration = new FacebookConnectorConfiguration();
	
    private JTextArea txtRefreshToken = new JTextArea();
    private DefaultComboBoxModel<String> cbmApiSelection = new DefaultComboBoxModel<String>();
    private JButton btnCreateToken = new JButton("Get User Token");
	
    
    /**
     * New pane for configuring FacebookConnector node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    protected FacebookConnectorNodeDialog() {
    	
    	
    	// Setup user authentication select drop-down
    	JComboBox<String> cbxApiSelection = new JComboBox<String>(cbmApiSelection);
    	cbxApiSelection.addActionListener(new TokenChangeAction());
    	
    	btnCreateToken.addActionListener(new ShowBrowserAction());
    	btnCreateToken.setVisible(false);
    	
    	for (String type: configuration.getAuthTypes()) {
    		cbmApiSelection.addElement(type);
    	}
    	
    	addTab(
    		"Settings",
    		buildStandardPanel(
    			new PanelBuilder()
    				.add("Credentials", cbxApiSelection )
    				.add("User Token", txtRefreshToken )
    				.add("", btnCreateToken )
    				.build()
    		)
    	);
    	 
    }

    
	@Override
	protected void saveSettingsTo(NodeSettingsWO settings)
			throws InvalidSettingsException {
		
		configuration.setAccessToken(txtRefreshToken.getText());
		configuration.save(settings);
		
	}
	
	@Override
	protected void loadSettingsFrom(NodeSettingsRO settings,
			DataTableSpec[] specs) throws NotConfigurableException {
		
		configuration = new FacebookConnectorConfiguration();
		configuration.load(settings);
		
		// Initialize
		String selectedItem = (String)cbmApiSelection.getSelectedItem();
		btnCreateToken.setVisible("User Specific".equals(selectedItem));
		
		
		if ( selectedItem != null && ( configuration.getAccessToken() == null || configuration.getAccessToken().isEmpty() ) ) {
			configuration.setAccessToken(configuration.getTokenFromAuthType(selectedItem) );
		}
		
		txtRefreshToken.setText(configuration.getAccessToken());
		
	}
    
    class TokenChangeAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			String selectedItem = (String)cbmApiSelection.getSelectedItem();
			if ( selectedItem == null ) return;
			
			/*
			 * Get configuration value:
			 */
			String token = configuration.getTokenFromAuthType(selectedItem);
			configuration.setAccessToken(token);
			txtRefreshToken.setText(configuration.getAccessToken());
			
			/*
			 * If User Specified show optional button
			 */
			btnCreateToken.setVisible("User Specific".equals(selectedItem));
		}
    	
    }
    
    class ShowBrowserAction implements ActionListener {
    	
    	@Override
    	public void actionPerformed(ActionEvent e) {
    		
    		// Create new thread for SWT Browser instance:
    		Thread t = new Thread(new Runnable() {
				
				@Override
				public void run() {
				
					final Display display = new Display();
		    		final Shell shell = new Shell ( display );
		    		final Browser browser = new Browser ( shell, SWT.NONE);
					
		    		// Setup Shell:
		    		display.asyncExec(new Runnable() {
			    		public void run() {
			    			
			    			shell.setSize(800, 600);
			    			browser.setLayoutData(new GridData(GridData.FILL_BOTH));
			    			browser.setSize(800, 600);
			    			browser.setUrl("https://www.facebook.com/v2.10/dialog/oauth?client_id=1600065610276063&response_type=token&scope=ads_management&redirect_uri=https://www.pg.com/connect/login_success.html");
			    			shell.open();
			    		}
		    		});
					
		    		// Check to see if has arrived at confirmation URL:
	    			while (!shell.isDisposed()) {
		    			
						display.asyncExec(new Runnable() {
							@Override
							public void run() {
								try {
									if ( browser != null && !browser.isDisposed() && browser.getUrl().startsWith("https://www.pg.com/connect/login_success.html") ) {
										String token = URLEncodedUtils.parse( new URI(browser.getUrl()).getFragment(), Charset.forName("UTF-8") )
															.stream()
															.filter( pair -> "access_token".equals(pair.getName()))
															.map(pair -> pair.getValue())
															.findFirst()
															.orElse("");
															
										txtRefreshToken.setText( token );
										shell.close();
										display.dispose();
									}
								} catch ( Exception exc ) {
									System.out.println("Error...");
								}
							}
						});
		    			
		    			if ( !display.readAndDispatch()  )
							display.sleep();
					}					
					
				}
			});
    		
    		t.start();
    		    			
    	}	
    }
	
}

