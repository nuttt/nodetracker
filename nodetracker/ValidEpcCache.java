/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nuttt.nodetracker;

import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.logic.Transaction;
import echowand.logic.TransactionListener;
import echowand.net.Frame;
import echowand.net.Property;
import echowand.net.StandardPayload;
import echowand.net.Subnet;
import echowand.net.SubnetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import static nuttt.nodetracker.ValidEpcCache.GETABLE_EPC;
import static nuttt.nodetracker.ValidEpcCache.OBSERVABLE_EPC;
import static nuttt.nodetracker.ValidEpcCache.SETABLE_EPC;
import nuttt.nodetracker.listener.DataTransactionListener;
import nuttt.nodetracker.listener.DataTransactionListenerDelegate;
import nuttt.nodetracker.listener.TransactionListenerUtility;

/**
 *
 * @author nuttt
 */
public class ValidEpcCache {
    
    private HashMap<String, EpcCache> Mapper;
    private TransactionFactory transactionFactory;
    
    public static final EPC OBSERVABLE_EPC = EPC.x9D;
    public static final EPC SETABLE_EPC = EPC.x9E;
    public static final EPC GETABLE_EPC = EPC.x9F;
    

    public ValidEpcCache(TransactionFactory transactionFactory) {
        this.transactionFactory = transactionFactory;
        Mapper = new HashMap<String, EpcCache>();
    }
    
    private EpcCache get(String eoj) throws EpcCacheNotFoundException {
        EpcCache EpcList;
        //System.out.println("EpcCache: Getting EOJ " + eoj);
        eoj = eoj.substring(0, 4).concat("00");
        EpcList = Mapper.get(eoj);
        if(EpcList == null) {
            EpcList = this.getNewEpcList(eoj);
            
            // Node inactive
            if(EpcList == null) {
                throw new EpcCacheNotFoundException();
            }
            
            Mapper.put(eoj, EpcList);
        }
        return EpcList;
    }
    
    public EpcCache get(EOJ eoj) throws EpcCacheNotFoundException {
        return this.get(eoj.toString());
    }
    
    private EpcCache getNewEpcList(String eoj) {
        
        ArrayList<EPC> epcGetter = new ArrayList<EPC>();
        ArrayList<EPC> epcList = new ArrayList<EPC>();
        EpcCache epcCache = new EpcCache();
            
        try {
            //System.out.println(eoj);
            
            epcGetter.add(OBSERVABLE_EPC);
            epcGetter.add(SETABLE_EPC);
            epcGetter.add(GETABLE_EPC);
            
//            DataTransactionListenerDelegate delegate = new EpcListTransactionListenerDelegate(epcCache);
//            DataTransactionListener listener = new DataTransactionListener(delegate);
            EpcListTransactionListener listener = new EpcListTransactionListener(epcCache);
            Transaction epcListTransaction =
                    transactionFactory.makeMulticastTransaction(eoj, epcGetter, listener);
            listener.setTransaction(epcListTransaction);
            
            epcListTransaction.execute();
            epcListTransaction.join();
            
            //System.out.println(epcCache);
            
        } catch (SubnetException ex) {
            Logger.getLogger(ValidEpcCache.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(ValidEpcCache.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        return epcCache;
    }
    
}

/*
class EpcListTransactionListenerDelegate implements DataTransactionListenerDelegate {

    EpcCache epcCache;
    Transaction transaction;

    public EpcListTransactionListenerDelegate(EpcCache epcList) {
        this.epcCache = epcList;
    }
    
    @Override
    public void process(Property property) {
        
        EPC epc = property.getEPC();
        ArrayList<EPC> data = TransactionListenerUtility.explodeEdtToEpcList(property.getEDT());
        
        if(epc == OBSERVABLE_EPC) {
            epcCache.Observable = data;
        } else if(epc == SETABLE_EPC) {
            epcCache.Setable = data;
        } else if(epc == GETABLE_EPC) {
            epcCache.Getable = data;
        }
        
    }
    
}

 */
class EpcListTransactionListener implements TransactionListener{

    private EpcCache epcCache;
    private Transaction transaction;
    
    public EpcListTransactionListener(EpcCache epcList) {
        this.epcCache = epcList;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
    
    @Override
    public void begin(Transaction t) {
        
    }

    @Override
    public void receive(Transaction t, Subnet subnet, Frame frame) {
        StandardPayload payload = TransactionListenerUtility.getPayload(frame);
        int propertyNum = TransactionListenerUtility.getPropertyNum(payload);
        
        for (int propertyIndex = 0; propertyIndex < propertyNum; propertyIndex++) {
            
            Property property = payload.getFirstPropertyAt(propertyIndex);
            EPC epc = property.getEPC();
            ArrayList<EPC> data = TransactionListenerUtility.explodeEdtToEpcList(property.getEDT());

            if(epc == OBSERVABLE_EPC) {
                epcCache.Observable = data;
            } else if(epc == SETABLE_EPC) {
                epcCache.Setable = data;
            } else if(epc == GETABLE_EPC) {
                epcCache.Getable = data;
            }
            
            epcCache.AllEPC.addAll(data);
            
        }
        
        transaction.finish();
        
    }

    @Override
    public void finish(Transaction t) {
        
    }
    
}
