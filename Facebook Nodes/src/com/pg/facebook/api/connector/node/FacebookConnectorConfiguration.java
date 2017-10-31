package com.pg.facebook.api.connector.node;

import java.util.ArrayList;
import java.util.Arrays;

import org.knime.core.node.ModelContentRO;
import org.knime.core.node.ModelContentWO;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

import com.pg.knime.secure.DefaultVault;

public class FacebookConnectorConfiguration {

	private String accessToken;
	private String authType;
	
	private String[] tokens;
    private String[] authTypes;
    
    /*
	private static final String CFG_APP_ID = "facebook app id";
	private static final String CFG_APP_SECRET = "facebook app secret";
	*/
	
	private static final String CFG_TOKEN = "facebook user token";
	private static final String CFG_TYPE = "facebook auth type";
	
	public static final String DEFAULT_AUTH_TYPE = "User Specific";
    
	private DefaultVault vault;
	
	public FacebookConnectorConfiguration() {
	
		/* If FacebookVault implementation not present default to custom token */
    	try {
    		vault = (DefaultVault)Class.forName("com.pg.knime.vault.FacebookVault").newInstance();	
    	} catch ( ClassNotFoundException | IllegalAccessException | InstantiationException exc ) {
    		vault = new DefaultVault();
    	}
    	
    	tokens = vault.getKey("USER_TOKENS").split(",");
    	authTypes = vault.getKey("TOKEN_TYPES").split(",");
		
	}
	
	
	public String getAppId() {
		return vault.getKey("DEFAULT_APP_ID");
	}
	
	public String getAppSecret() {
		return vault.getKey("DEFAULT_APP_SECRET");
	}
	
	public String getAccessToken() {
		return this.accessToken;
	}
	
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	public String getAuthType() {
		
		if ( this.authType == null || "".equals(this.authType )) {
			return getAuthTypes()[0];
		}
		
		return this.authType;
	}
	
	public void setAuthType( String authType ) {
		this.authType = authType;
	}
	
	public void save(NodeSettingsWO settings) {
		settings.addString(CFG_TOKEN, getAccessToken());
		settings.addString(CFG_TYPE, getAuthType() );
	}
	
	public void save(ModelContentWO settings) {
		settings.addString(CFG_TOKEN, getAccessToken());
		settings.addString(CFG_TYPE, getAuthType());
	}
	
	public String[] getAuthTypes() {
		ArrayList<String> types = new ArrayList<String>();
		
		for ( String t : authTypes ) {
			types.add(t);
		}
		
		// Add User-Specific selection:
		types.add(DEFAULT_AUTH_TYPE);
		
		return types.toArray(new String[] {} );
	}
	
	public int getAuthTypePos( String at ) {
		for ( int i = 0; i < getAuthTypes().length; i++ ) {
			if ( at.equals(getAuthTypes()[i]) ) return i;
		}
		
		return 0;
	}
	
	public String[] getTokens() {
		ArrayList<String> types = new ArrayList<String>();
		
		for ( String t : tokens ) {
			types.add(t);
		}
		
		// Add User-Specific token if exists:
		if ( !Arrays.asList( tokens ).contains( getAccessToken() ) )
			types.add(getAccessToken());
		
		return types.toArray(new String[] {} );
	}
	
	public void load(NodeSettingsRO settings) {
		this.accessToken = settings.getString(CFG_TOKEN, "");
		this.authType = settings.getString(CFG_TYPE, "");
	}

	public void load(ModelContentRO settings) {
		this.accessToken = settings.getString(CFG_TOKEN, "");
		this.authType = settings.getString(CFG_TYPE, "" );
	}	
	
}
