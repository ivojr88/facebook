package com.pg.facebook.api.createcustomaudience;

import java.util.Arrays;
import java.util.List;

import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

public class CreateCustomAudienceConfiguration {

	private String audienceName;
	private String addAccountId;
	
	
	private static String CFG_NAME = "cfg.audience.name";
	private static String CFG_AD_ACCOUNT_ID = "cfg.ad.account.id";
	
	public static List<String> VALID_COL_NAMES = Arrays.asList(new String[] { "UID", "EMAIL_SHA256", "PHONE_SHA256", "MOBILE_ADVERTISER_ID" } );
	
	public String getAudienceName() {
		return audienceName;
	}
	public void setAudienceName(String audienceName) {
		this.audienceName = audienceName;
	}
	public String getAddAccountId() {
		return addAccountId;
	}
	public void setAddAccountId(String addAccountId) {
		this.addAccountId = addAccountId;
	}
	
	public void load ( NodeSettingsRO settings ) {
		
		setAudienceName(settings.getString(CFG_NAME, ""));
		setAddAccountId(settings.getString(CFG_AD_ACCOUNT_ID, ""));
		
	}
	
	public void save ( NodeSettingsWO settings ) {
		
		settings.addString(CFG_NAME, getAudienceName());
		settings.addString(CFG_AD_ACCOUNT_ID, getAddAccountId() );
	}
	
}
