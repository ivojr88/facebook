package com.pg.facebook.api.removefromcustomaudience;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;

import com.pg.facebook.api.updatecustomaudience.UpdateCustomAudienceNodeDialog;

/**
 * <code>NodeDialog</code> for the "RemoveFromCustomAudience" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author P&G Data Science
 */
public class RemoveFromCustomAudienceNodeDialog extends UpdateCustomAudienceNodeDialog {

    /**
     * New pane for configuring the RemoveFromCustomAudience node.
     */
    protected RemoveFromCustomAudienceNodeDialog() {
    	super();
    }
}

