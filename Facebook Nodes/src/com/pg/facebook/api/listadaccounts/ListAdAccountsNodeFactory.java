package com.pg.facebook.api.listadaccounts;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "ListAdAccounts" Node.
 * 
 *
 * @author P&G, Data Science
 */
public class ListAdAccountsNodeFactory 
        extends NodeFactory<ListAdAccountsNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public ListAdAccountsNodeModel createNodeModel() {
        return new ListAdAccountsNodeModel();
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
    public NodeView<ListAdAccountsNodeModel> createNodeView(final int viewIndex,
            final ListAdAccountsNodeModel nodeModel) {
        return new ListAdAccountsNodeView(nodeModel);
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
        return new ListAdAccountsNodeDialog();
    }

}

