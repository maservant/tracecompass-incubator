/*******************************************************************************
 * Copyright (c) 2019 École Polytechnique de Montréal
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.incubator.internal.filters.ui.lspFilterTextbox;

import java.util.List;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.lsp4j.CompletionItem;
// import org.eclipse.lsp4j.Range;

/**
 * Content Assistant processor to provide auto-completions to the
 * ContentAssistant installed in the LSP filter text box. Provides
 * auto-completions for both local recently used filters and completions
 * provided by the LSP server.
 *
 * @author Marc-Andre Servant
 */
public class FilterBoxContentAssistProcessor implements IContentAssistProcessor {

    // For now, only auto-complete from the list received from the LSP client
    private List<CompletionItem> fFilterCompletions;

    static final ICompletionProposal[] EMPTY_COMPLETION_PROPOSAL_LIST = new ICompletionProposal[] {};

    // For now we activate whenever the user enters a space character. The
    // default behaviour of CompletionAssistant is to not show an empty
    // list, so the window should disappear when the LSP client provides no
    // completions.
    static final char[] AUTO_ACTIVATION_CHARACTERS = new char[] { ' ' };

    /**
     * Constructor
     *
     */
    public FilterBoxContentAssistProcessor() {
        fFilterCompletions = null;
    }

    @Override
    public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
        if (fFilterCompletions != null) {
            ICompletionProposal[] results = new ICompletionProposal[fFilterCompletions.size()];

            for (int i = 0; i < fFilterCompletions.size(); i++) {
                CompletionItem item = fFilterCompletions.get(i);
                String proposal = item.getTextEdit().getNewText();
                // Range range = item.getTextEdit().getRange();

                // TODO fill the "detail" property of the CompletionItem on the
                // server side. Right now, use only the proposal's text.
                ContextInformation info = new ContextInformation(proposal, proposal);

                // TODO the proposed completion should use the range provided by
                // the LSP client when applied. If a proposal is an insertion
                // and not a replacement, then range.getStart() ==
                // range.getEnd(). Right now the server sets the range
                // incorrectly.
                /*
                 * results[i] = new CompletionProposal(
                 *         proposal,
                 *         range.getStart().getCharacter(),
                 *         range.getEnd().getCharacter() -
                 *         range.getStart().getCharacter(),
                 *         range.getEnd().getCharacter() -
                 *         range.getStart().getCharacter(),
                 *         null,
                 *         proposal,
                 *         info,
                 *         item.getDetail());
                 */

                results[i] = new CompletionProposal(
                        proposal,
                        offset,
                        proposal.length(),
                        proposal.length(),
                        null,
                        proposal,
                        info,
                        new String());

            }

            // TODO Add results from local history

            return results;
        }

        return EMPTY_COMPLETION_PROPOSAL_LIST;
    }

    /**
     * Sets the list of completions proposed by the LSP client
     *
     * @param filterCompletions
     *            A list of completions to propose
     *
     */
    public void setFilterCompletions(List<CompletionItem> filterCompletions) {
        fFilterCompletions = filterCompletions;
    }

    @Override
    public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
        // Context information is not provided separately in this processor
        return null;
    }

    @Override
    public char[] getCompletionProposalAutoActivationCharacters() {
        return AUTO_ACTIVATION_CHARACTERS;
    }

    @Override
    public char[] getContextInformationAutoActivationCharacters() {
        // Context information is not used in this processor
        return null;
    }

    @Override
    public String getErrorMessage() {
        // No errors are generated by this processor, so we always return null.
        return null;
    }

    @Override
    public IContextInformationValidator getContextInformationValidator() {
        // Context information is not used, so we do not return a validator.
        return null;
    }

}
