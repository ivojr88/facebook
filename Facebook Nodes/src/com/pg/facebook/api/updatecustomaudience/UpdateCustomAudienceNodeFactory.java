package com.pg.facebook.api.updatecustomaudience;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "UpdateCustomAudience" Node.
 * 
 *
 * @author P&G Data Science
 */
public class UpdateCustomAudienceNodeFactory 
        extends NodeFactory<UpdateCustomAudienceNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public UpdateCustomAudienceNodeModel createNodeModel() {
        return new UpdateCustomAudienceNodeModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrNodeViews() {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeView<UpdateCustomAudienceNodeModel> createNodeView(final int viewIndex,
            final UpdateCustomAudienceNodeModel nodeModel) {
        return new UpdateCustomAudienceNodeView(nodeModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasDialog() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeDialogPane createNodeDialogPane() {
        return new UpdateCustomAudienceNodeDialog();
    }

}

