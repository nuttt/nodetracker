/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nuttt.nodetracker.listener;

import echowand.common.Data;
import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.net.Frame;
import echowand.net.StandardPayload;
import java.util.ArrayList;

/**
 *
 * @author nuttt
 */
public class TransactionListenerUtility {
    public static StandardPayload getPayload(Frame frame) {
        return (StandardPayload) frame.getCommonFrame().getEDATA();
    }
    
    public static int getPropertyNum(StandardPayload payload) {
        return payload.getFirstOPC();
    }
    
    public static ArrayList<EPC> explodeEdtToEpcList(Data edt) {
        ArrayList<EPC> epcList = new ArrayList<EPC>();
        byte[] bytes = edt.toBytes(1, edt.size() - 1);
        
        for(byte b : bytes) {
            epcList.add(EPC.fromByte(b));
        }
        
        return epcList;
    }
    
    public static ArrayList<EOJ> explodeEdtToEojList(Data edt) {
        int instanceNum = edt.get(0);
        ArrayList<EOJ> eojList = new ArrayList<EOJ>(instanceNum);
        
        int startByte;
        final int EOJ_BYTE_LENGTH = 3;
        
        for(short i = 0; i < instanceNum; i++) {
            startByte = i*EOJ_BYTE_LENGTH + 1;
            
            eojList.add(new EOJ(edt.toBytes(startByte, EOJ_BYTE_LENGTH)));
        }
        
        return eojList;
    }
}
