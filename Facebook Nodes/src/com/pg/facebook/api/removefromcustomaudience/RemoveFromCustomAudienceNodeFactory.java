package com.pg.facebook.api.removefromcustomaudience;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "RemoveFromCustomAudience" Node.
 * 
 *
 * @author P&G Data Science
 */
public class RemoveFromCustomAudienceNodeFactory 
        extends NodeFactory<RemoveFromCustomAudienceNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public RemoveFromCustomAudienceNodeModel createNodeModel() {
        return new RemoveFromCustomAudienceNodeModel();
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
    public NodeView<RemoveFromCustomAudienceNodeModel> createNodeView(final int viewIndex,
            final RemoveFromCustomAudienceNodeModel nodeModel) {
        return new RemoveFromCustomAudienceNodeView(nodeModel);
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
        return new RemoveFromCustomAudienceNodeDialog();
    }

}

