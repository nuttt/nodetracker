/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nuttt.nodetracker;

import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.logic.SetGetTransactionConfig;
import echowand.logic.Transaction;
import echowand.logic.TransactionListener;
import echowand.logic.TransactionManager;
import echowand.net.Subnet;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author nuttt
 */
public class TransactionFactory {
    
    TransactionManager transactionManager;
    Subnet subnet;
    
    public static final int TRANSACTION_TIMEOUT = 1000;

    public TransactionFactory(TransactionManager transactionManager, Subnet subnet) {
        this.transactionManager = transactionManager;
        this.subnet = subnet;
    }

    
    
    public Transaction makeMulticastTransaction(String eoj, Collection<EPC> epcs, TransactionListener listener) {
        return makeMulticastTransaction(new EOJ(eoj), epcs, listener, TRANSACTION_TIMEOUT);
    }
    
    public Transaction makeMulticastTransaction(EOJ eoj, Collection<EPC> epcs, TransactionListener listener) {
        return makeMulticastTransaction(eoj, epcs, listener, TRANSACTION_TIMEOUT);
    }
    
    public Transaction makeMulticastTransaction(String eoj, Collection<EPC> epcs, TransactionListener listener, int timeout) {
        return makeMulticastTransaction(new EOJ(eoj), epcs, listener, timeout);
    }
    
    public Transaction makeMulticastTransaction(EOJ eoj, Collection<EPC> epcs, TransactionListener listener, int timeout) {
        SetGetTransactionConfig transactionConfig = new SetGetTransactionConfig();
        transactionConfig.setSenderNode(subnet.getLocalNode());
        transactionConfig.setReceiverNode(subnet.getGroupNode());
        transactionConfig.setSourceEOJ(new EOJ("0ef001"));
        transactionConfig.setDestinationEOJ(eoj);
        
        for(EPC epc : epcs) {
            if(epc != EPC.Invalid) {
                transactionConfig.addGet(epc);
            }
        }
        
        Transaction transaction = transactionManager.createTransaction(transactionConfig);
        transaction.setTimeout(timeout);
        transaction.addTransactionListener(listener);
        
        return transaction;
        
    }
    
}