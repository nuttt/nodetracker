/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nuttt.nodetracker;

import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.logic.MainLoop;
import echowand.logic.Transaction;
import echowand.logic.TransactionListener;
import echowand.logic.TransactionManager;
import echowand.net.Frame;
import echowand.net.Inet4Subnet;
import echowand.net.Node;
import echowand.net.Property;
import echowand.net.StandardPayload;
import echowand.net.Subnet;
import echowand.net.SubnetException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import nuttt.nodetracker.listener.TransactionListenerUtility;

/**
 *
 * @author nuttt
 */
public class EchonetNodeTracker implements Runnable{
    
    ConcurrentHashMap<Node, ArrayList<EOJ>> nodeTracker;
    HashSet<EOJ> eojTracker;

    public EchonetNodeTracker() {
        this.nodeTracker = new ConcurrentHashMap<Node, ArrayList<EOJ>>();
        eojTracker = new HashSet<EOJ>();
    }
    
    public static void main(String[] args) throws SubnetException {
        
        Thread t = new Thread(new EchonetNodeTracker());
        t.start();
        
    }

    @Override
    public void run() {
        try {
            Subnet subnet = new Inet4Subnet();
            TransactionManager transactionManager = new TransactionManager(subnet);
            
            MainLoop loop = new MainLoop();
            loop.setSubnet(subnet);
            loop.addListener(transactionManager);
            Thread loopThread = new Thread(loop);
            loopThread.start();
            
            
            TransactionFactory transactionFactory = new TransactionFactory(transactionManager, subnet);
            ValidEpcCache validEpcCache = new ValidEpcCache(transactionFactory);
            Pinger pinger = new Pinger(transactionFactory, nodeTracker, eojTracker);
            Thread pingerThread = new Thread(pinger);
            
            pingerThread.start();
            
            System.out.println("Setting up...");
            Thread.sleep(5000);
            System.out.println("Start tracking");
            
            ArrayList<EOJ> eojRemoveList = new ArrayList<EOJ>();
            
            while(true) {
                Object[] eojs = eojTracker.toArray();
                for(Object o : eojs) {
                    EOJ eoj = (EOJ) o;
                    try {
                        EpcCache epcCache = validEpcCache.get(eoj);
                        System.out.println("==============================");
                        System.out.println(eoj + ": " + epcCache);
                        System.out.println("==============================");
                        
                        NodeTrackListener listener = new NodeTrackListener();
                        Transaction t = transactionFactory.makeMulticastTransaction(eoj, epcCache.AllEPC, listener);
                        
                        t.execute();
                        t.join();
                        
                        if(!listener.isEojAlive()) {
                            eojRemoveList.add(eoj);
                        }
                        
                        
                    } catch (EpcCacheNotFoundException ex) {
                        Logger.getLogger(EchonetNodeTracker.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                }
                
                for(EOJ eoj : eojRemoveList) {
                    eojTracker.remove(eoj);
                    System.out.println(">> EOJ " + eoj + " removed from eojTracker");
                }
                
                eojRemoveList.clear();
                
                Thread.sleep(1000);
            }
            
            
        } catch (SubnetException ex) {
            Logger.getLogger(EchonetNodeTracker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(EchonetNodeTracker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}

class NodeTrackListener implements TransactionListener {

    boolean eojAlive;
    NodeTrackListener() {
        this.eojAlive = false;
    }

    @Override
    public void begin(Transaction t) {
        
    }

    @Override
    public void receive(Transaction t, Subnet subnet, Frame frame) {
        
        eojAlive = true;
        
        System.out.println("---------------");
        
        Node sender = frame.getSender();
        
        StandardPayload payload = TransactionListenerUtility.getPayload(frame);
        int propertyNum = TransactionListenerUtility.getPropertyNum(payload);
        
        System.out.println(sender + " / " + payload.getSEOJ());
        
        for (int propertyIndex = 0; propertyIndex < propertyNum; propertyIndex++) {
            Property property = payload.getFirstPropertyAt(propertyIndex);
            System.out.println(property.getEPC() + ": " + property.getEDT());
        }
        System.out.println("---------------");
    }

    public boolean isEojAlive() {
        return eojAlive;
    }

    @Override
    public void finish(Transaction t) {
        
    }
    
}