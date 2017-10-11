package com.pg.facebook.api.listcustomaudiences;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "ListCustomAudiences" Node.
 * 
 *
 * @author P&G, DataScience
 */
public class ListCustomAudiencesNodeFactory 
        extends NodeFactory<ListCustomAudiencesNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public ListCustomAudiencesNodeModel createNodeModel() {
        return new ListCustomAudiencesNodeModel();
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
    public NodeView<ListCustomAudiencesNodeModel> createNodeView(final int viewIndex,
            final ListCustomAudiencesNodeModel nodeModel) {
        return new ListCustomAudiencesNodeView(nodeModel);
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
        return new ListCustomAudiencesNodeDialog();
    }

}

