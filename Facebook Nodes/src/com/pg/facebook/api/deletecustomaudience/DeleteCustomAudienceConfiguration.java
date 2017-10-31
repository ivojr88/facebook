package com.pg.facebook.api.deletecustomaudience;

import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

public class DeleteCustomAudienceConfiguration {

	private String audienceId;
	
	private static String CFG_AUDIENCE_ID = "cfg.audience.id";

	
	public String getAudienceId() {
		return audienceId;
	}

	public void setAudienceId(String audienceId) {
		this.audienceId = audienceId;
	}
	
	public void load ( NodeSettingsRO settings ) {
		setAudienceId(settings.getString(CFG_AUDIENCE_ID, ""));
	}
	
	public void save ( NodeSettingsWO settings ) {
		settings.addString(CFG_AUDIENCE_ID, getAudienceId() );
	}
	
}
