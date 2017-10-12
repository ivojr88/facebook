package com.pg.facebook.api.createcustomaudience;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "CreateCustomAudience" Node.
 * 
 *
 * @author P&G, DataScience
 */
public class CreateCustomAudienceNodeFactory 
        extends NodeFactory<CreateCustomAudienceNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public CreateCustomAudienceNodeModel createNodeModel() {
        return new CreateCustomAudienceNodeModel();
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
    public NodeView<CreateCustomAudienceNodeModel> createNodeView(final int viewIndex,
            final CreateCustomAudienceNodeModel nodeModel) {
        return new CreateCustomAudienceNodeView(nodeModel);
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
        return new CreateCustomAudienceNodeDialog();
    }

}

