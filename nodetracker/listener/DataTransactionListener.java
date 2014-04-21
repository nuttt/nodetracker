/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nuttt.nodetracker.listener;

import echowand.logic.Transaction;
import echowand.logic.TransactionListener;
import echowand.net.Frame;
import echowand.net.StandardPayload;
import echowand.net.Subnet;

/**
 *
 * @author nuttt
 */
public class DataTransactionListener implements TransactionListener{

    DataTransactionListenerDelegate delegator;

    public DataTransactionListener(DataTransactionListenerDelegate delegator) {
        this.delegator = delegator;
    }
    
    @Override
    public void begin(Transaction t) {
        
    }

    @Override
    public void receive(Transaction t, Subnet subnet, Frame frame) {
        StandardPayload payload = TransactionListenerUtility.getPayload(frame);
        int propertyNum = TransactionListenerUtility.getPropertyNum(payload);
        for (int propertyIndex = 0; propertyIndex < propertyNum; propertyIndex++) {
            delegator.process(payload.getFirstPropertyAt(propertyIndex));
        }
    }

    @Override
    public void finish(Transaction t) {
        
    }
    
}
