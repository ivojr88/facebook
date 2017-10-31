package com.pg.facebook.api.connector.node;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.nio.charset.Charset;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextArea;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
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

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
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
 * 
 * Additional FB Token Creation API Details: https://developers.facebook.com/docs/facebook-login/manually-build-a-login-flow#token
 */
public class FacebookConnectorNodeDialog extends StandardTrackedNodeDialogPane {

	private FacebookConnectorConfiguration configuration = new FacebookConnectorConfiguration();
	
    private JTextArea txtRefreshToken = new JTextArea();
    private DefaultComboBoxModel<String> cbmApiSelection = new DefaultComboBoxModel<String>();
    private JButton btnCreateToken = new JButton("Get User Token");
    private JButton btnExtendToken = new JButton("Extend Token Lifetime");
	
    
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
    	
    	
    	btnExtendToken.setVisible(false);
    	btnExtendToken.addActionListener( new ExtendTokenAction() );
    	
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
    				.add("", btnExtendToken )
    				.build()
    		)
    	);
    	 
    }

    
	@Override
	protected void saveSettingsTo(NodeSettingsWO settings)
			throws InvalidSettingsException {
		
		configuration.setAccessToken(txtRefreshToken.getText());
		configuration.setAuthType((String)cbmApiSelection.getSelectedItem());
		
		configuration.save(settings);
		
	}
	
	@Override
	protected void loadSettingsFrom(NodeSettingsRO settings,
			DataTableSpec[] specs) throws NotConfigurableException {
		
		configuration = new FacebookConnectorConfiguration();
		configuration.load(settings);
		
		if ( cbmApiSelection.getSize() == 0 )
			for ( String type : configuration.getAuthTypes() ) {
				cbmApiSelection.addElement( type );
			}
		
		String selectedItem = configuration.getAuthType();
		cbmApiSelection.setSelectedItem(selectedItem);
		int pos = configuration.getAuthTypePos(selectedItem);
		
		
		btnCreateToken.setVisible(FacebookConnectorConfiguration.DEFAULT_AUTH_TYPE.equals(selectedItem));
		txtRefreshToken.setText( configuration.getTokens()[pos] );
		
	}
    
    class TokenChangeAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			String selectedItem = (String)cbmApiSelection.getSelectedItem();
			if ( selectedItem == null ) return;
			
			/*
			 * Get configuration value:
			 */
			int pos = configuration.getAuthTypePos(selectedItem);
			String token = configuration.getTokens()[pos];
			
			configuration.setAccessToken(token);
			txtRefreshToken.setText(configuration.getAccessToken());
			
			/*
			 * If User Specified show optional button
			 */
			btnCreateToken.setVisible(FacebookConnectorConfiguration.DEFAULT_AUTH_TYPE.equals(selectedItem));
		}
    	
    }
    
    class ExtendTokenAction implements ActionListener {
    	
    	@Override
    	public void actionPerformed(ActionEvent e) {
    	
    		String extensionUrl = "https://graph.facebook.com/oauth/access_token?client_id=" + configuration.getAppId() + 
    								"&client_secret=" + configuration.getAppSecret() + 
    								"&grant_type=fb_exchange_token" + 
    								"&fb_exchange_token=" + txtRefreshToken.getText();
    		
    		HttpClient client = HttpClients.createDefault();
    		HttpGet request = new HttpGet(extensionUrl);
    		String body = "";
    		try {
    			HttpResponse response = client.execute(request);
    			body = EntityUtils.toString(response.getEntity());
    		} catch ( Exception exc ) {}
    		
    		if ( !"".equals(body) ) {
    			JsonElement jsonBody = new JsonParser().parse(body);
        		txtRefreshToken.setText( jsonBody.getAsJsonObject().get("access_token").getAsString() );
        		btnExtendToken.setVisible(false);
    		}
    		
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
			    			browser.setUrl("https://www.facebook.com/v2.10/dialog/oauth?client_id=" + configuration.getAppId() + "&response_type=token&scope=ads_management&redirect_uri=https://www.pg.com/connect/login_success.html");
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
										btnExtendToken.setVisible(true);
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

