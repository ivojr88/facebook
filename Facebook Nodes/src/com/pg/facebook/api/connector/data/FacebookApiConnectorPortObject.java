package com.pg.facebook.api.connector.data;

import java.io.IOException;
import java.io.Serializable;

import javax.swing.JComponent;

import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.ModelContentRO;
import org.knime.core.node.ModelContentWO;
import org.knime.core.node.port.AbstractSimplePortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortObjectZipInputStream;
import org.knime.core.node.port.PortObjectZipOutputStream;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.PortTypeRegistry;

import com.pg.facebook.api.FacebookApiClient;


public class FacebookApiConnectorPortObject extends AbstractSimplePortObject implements Serializable {

	private FacebookApiConnectionPortObjectSpec spec;
	
	private static final long serialVersionUID = 1L;
	
	public static final PortType TYPE = PortTypeRegistry.getInstance().getPortType(FacebookApiConnectorPortObject.class);
		
	public static final class Serializer extends AbstractSimplePortObjectSerializer<FacebookApiConnectorPortObject>{};
	
	public FacebookApiConnectorPortObject() {
		
	}
	
	public FacebookApiClient getFacebookApiClient() {
		return spec.getFacebookClient();
	}
	
	public void setObjectId( String accountId) {
		spec.setObjectId(accountId);
	}
	
	public void setImpersonationAccessToken( String token ) {
		spec.setImpersonationAccessToken(token);
	}
	
	public FacebookApiConnectorPortObject(FacebookApiConnectionPortObjectSpec spec) {
		this.spec = spec;
	}
	
	@Override
	public String getSummary() {
		return spec.getFacebookClient().toString();
	}

	@Override
	public PortObjectSpec getSpec() {
		return spec;
	}

	@Override
	public JComponent[] getViews() {
		// TODO Auto-generated method stub
		return null;
	}

	protected void save(PortObjectZipOutputStream out, ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
		
		
	}

	protected void load(PortObjectZipInputStream in, PortObjectSpec spec,
			ExecutionMonitor exec) throws IOException,
			CanceledExecutionException {
		
		this.spec = (FacebookApiConnectionPortObjectSpec)spec;
		
	}

	@Override
	protected void save(ModelContentWO model, ExecutionMonitor exec) throws CanceledExecutionException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void load(ModelContentRO model, PortObjectSpec spec, ExecutionMonitor exec)
			throws InvalidSettingsException, CanceledExecutionException {
		
		this.spec = (FacebookApiConnectionPortObjectSpec)spec;
		
	}

}
