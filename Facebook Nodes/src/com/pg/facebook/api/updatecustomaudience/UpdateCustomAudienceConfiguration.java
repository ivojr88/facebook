package com.pg.facebook.api.updatecustomaudience;



import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

public class UpdateCustomAudienceConfiguration {

	private String audienceId;
	
	
	private static String CFG_AUD_ID = "cfg.audience.id";
	
	public String getAudienceId() {
		return audienceId;
	}
	public void setAudienceId(String audienceName) {
		this.audienceId = audienceName;
	}
	
	public void load ( NodeSettingsRO settings ) {
		setAudienceId(settings.getString(CFG_AUD_ID, ""));
	}
	
	public void save ( NodeSettingsWO settings ) {
		settings.addString(CFG_AUD_ID, getAudienceId());
	}
	
}
