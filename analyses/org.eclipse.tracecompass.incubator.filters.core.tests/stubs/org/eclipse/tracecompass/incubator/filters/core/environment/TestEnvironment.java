/*******************************************************************************
 * Copyright (c) 2019 Ecole Polytechnique de Montreal
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.tracecompass.incubator.filters.core.environment;

import java.io.IOException;
import java.util.concurrent.Semaphore;

import org.eclipse.tracecompass.incubator.filters.core.stubs.TestConnector;
import org.eclipse.tracecompass.incubator.internal.filters.core.client.wrapper.LanguageFilterClientWrapper;
import org.eclipse.tracecompass.incubator.internal.filters.core.server.LSPServer;

/**
 * Create a test environment for testing LSP implementations Use this object in
 * your test case to synchronize and probe transactions
 *
 * This class contains everything to test the real LSP client implementation and
 * the real LSP server implementation.
 *
 * Stubs will need to enhenced in order to support more tests see @link Stub for
 * more information on how to use the Stub
 *
 * Access stubs and real implementations from this class methods.
 *
 * 1.Use Server/Client to call methods invoke real implementations
 *
 * 2.Use stubs to check if value passed between real implementations are
 * correct.
 *
 * @author Maxime Thibault
 *
 */
public class TestEnvironment {

    // The real LSP server implementation
    private LSPServer fServer = null;

    // The real LSP client implementation
    private LanguageFilterClientWrapper fClient = null;

    // Stub that contains the fake server and the face client implementation
    // see @link Stub for more information on how to use the Stub
    private TestConnector fStub;

    // The number of expected transactions between the server and the client
    // The transactions are counted in the stubs
    // see @link Stub for more information on how to use the Stub
    private int fExepectedTransaction;

    // Semaphore to wait the end of expected transactions
    private Semaphore fTransactionsLock;

    /**
     * Create a test environment
     *
     * @param expectedTransaction
     *            The number of transaction expected during the test
     * @throws InterruptedException
     *             Exception thrown by environment initialization
     * @throws IOException
     *             Exception thrown by environment initialization
     */
    public TestEnvironment(int expectedTransaction) throws IOException, InterruptedException {
        initialize(expectedTransaction);
    }

    /**
     * Reset the test environment
     *
     * @param expectedTransaction
     *            The number of transaction expected during the test
     * @throws InterruptedException
     *             Exception thrown by environment initialization
     * @throws IOException
     *             Exception thrown by environment initialization
     */
    public void reset(int expectedTransaction) throws IOException, InterruptedException {
        initialize(expectedTransaction);
    }

    /**
     * Initialize the test environment
     *
     * @param expectedTransaction
     *            The number of transaction expected before completion This
     *            variable is use to create a semaphore that block until the
     *            number of transactions between the LSP Implementations has
     *            been observed. @see TestConnector.count()
     * @throws IOException
     *             Exception thrown by the streams
     * @throws InterruptedException
     *             Exception thrown by the lock
     */
    private void initialize(int expectedTransaction) throws IOException, InterruptedException {

        fExepectedTransaction = expectedTransaction;
        fTransactionsLock = new Semaphore(expectedTransaction);
        // Empty the semaphore
        fTransactionsLock.acquire(fExepectedTransaction);

        // Connect stubs and real implementations

        Stream clientStream = new Stream();
        Stream serverStream = new Stream();
        Stream clientStubStream = new Stream();
        Stream serverStubStream = new Stream();

        // Init stub
        fStub = new TestConnector(fTransactionsLock);

        // Server read from client stub, write its own stream back to it
        fServer = new LSPServer(clientStubStream.read, serverStream.write);

        // Init clientStub: stub read from server and write its own stream back
        // to it
        fStub.initClient(serverStream.read, clientStubStream.write);

        // Init serverStub: stub read from client and write its own stream back
        // to it
        fStub.initServer(clientStream.read, serverStubStream.write);

        // Client read from server stub, write its own stream back to it
        fClient = new LanguageFilterClientWrapper(serverStubStream.read, clientStream.write, fStub.getObserver());

    }

    /**
     * Wait for the transactions to be done
     *
     * @throws InterruptedException
     * @throws IOException
     */
    public void waitForTransactionToTerminate() throws InterruptedException, IOException {
        // @see TestConnector.count()
        fTransactionsLock.acquire(fExepectedTransaction);
        // Do one more for exit call -> This ensure that the last transaction
        // we've expected has finished
        fClient.dispose();
        fTransactionsLock.acquire();
    }

    /**
     * Return the stub
     *
     * Use this to check value passed between the real implementations
     *
     * @return
     */
    public TestConnector getStub() {
        return fStub;
    }

    /**
     * Return the real client implementation
     *
     * @return
     */
    public LanguageFilterClientWrapper getClient() {
        return fClient;
    }

    /**
     * Return the real server implementation
     *
     * @return
     */
    public LSPServer getServer() {
        return fServer;
    }
}
