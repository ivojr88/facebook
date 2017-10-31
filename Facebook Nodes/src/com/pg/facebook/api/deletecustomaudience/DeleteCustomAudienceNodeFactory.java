package com.pg.facebook.api.deletecustomaudience;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "DeleteCustomAudience" Node.
 * 
 *
 * @author P&G, Data Science
 */
public class DeleteCustomAudienceNodeFactory 
        extends NodeFactory<DeleteCustomAudienceNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public DeleteCustomAudienceNodeModel createNodeModel() {
        return new DeleteCustomAudienceNodeModel();
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
    public NodeView<DeleteCustomAudienceNodeModel> createNodeView(final int viewIndex,
            final DeleteCustomAudienceNodeModel nodeModel) {
        return new DeleteCustomAudienceNodeView(nodeModel);
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
        return new DeleteCustomAudienceNodeDialog();
    }

}

