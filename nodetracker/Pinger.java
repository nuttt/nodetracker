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
import echowand.net.Node;
import echowand.net.Property;
import echowand.net.StandardPayload;
import echowand.net.Subnet;
import echowand.net.SubnetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import nuttt.nodetracker.listener.TransactionListenerUtility;

/**
 *
 * @author nuttt
 */
public class Pinger implements Runnable{

    TransactionFactory transactionFactory;
    ConcurrentHashMap<Node, ArrayList<EOJ>> nodeTracker;
    HashSet<EOJ> eojTracker;
            
    public static final EPC INSTANCE_LIST_EPC = EPC.xD6;
    public static final int PING_INTERVAL = 0;

    public Pinger(TransactionFactory transactionFactory, ConcurrentHashMap<Node, ArrayList<EOJ>> nodeTracker, HashSet<EOJ> eojTracker) {
        this.transactionFactory = transactionFactory;
        this.nodeTracker = nodeTracker;
        this.eojTracker = eojTracker;
    }

    
    @Override
    public void run() {
        try {
            while(true) {
                ArrayList<EPC> instanceListGetter = new ArrayList<EPC>();
                instanceListGetter.add(INSTANCE_LIST_EPC);

                ArrayList<EOJ> epcList = new ArrayList<EOJ>();
                
                PingerTransactionListener listener = new PingerTransactionListener(nodeTracker, eojTracker);
                Transaction transaction = transactionFactory.makeMulticastTransaction(new EOJ("0ef001"), instanceListGetter, listener);

                transaction.execute();
                transaction.join();
                
                Thread.sleep(PING_INTERVAL);
            }
            
        } catch (SubnetException ex) {
            Logger.getLogger(Pinger.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Pinger.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}

class PingerTransactionListener implements TransactionListener {

    
    ConcurrentHashMap<Node, ArrayList<EOJ>> nodeTracker;
    HashSet<EOJ> eojTracker;

    public PingerTransactionListener(ConcurrentHashMap<Node, ArrayList<EOJ>> nodeTracker, HashSet<EOJ> eojTracker) {
        this.nodeTracker = nodeTracker;
        this.eojTracker = eojTracker;
    }
    
    
    
    @Override
    public void begin(Transaction t) { 
       
    }

    @Override
    public void receive(Transaction t, Subnet subnet, Frame frame) {
        
        ArrayList<EOJ> eojList = new ArrayList<EOJ>();
        Node sender = frame.getSender();
//        System.out.println("Pinger: " + sender);
        
        StandardPayload payload = TransactionListenerUtility.getPayload(frame);
        int propertyNum = TransactionListenerUtility.getPropertyNum(payload);
        
        for (int propertyIndex = 0; propertyIndex < propertyNum; propertyIndex++) {
            Property property = payload.getFirstPropertyAt(propertyIndex);
            ArrayList<EOJ> eojs = TransactionListenerUtility.explodeEdtToEojList(property.getEDT());
            eojList.addAll(eojs);
            
            for(EOJ eoj : eojs) {
                EOJ genericEoj = new EOJ(eoj.toString().substring(0, 4).concat("00"));
                eojTracker.add(genericEoj);
//                eojTracker.addAll(eojs);
            }
        }
        
        nodeTracker.put(sender, eojList);
    }

    @Override
    public void finish(Transaction t) {
        
    }


    
}
