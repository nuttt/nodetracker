/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nuttt.nodetracker;

import echowand.common.EOJ;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author nuttt
 */
public class NodeStatus {
    ArrayList<EOJ> eoj;
    Date latestResponseTime;
    
    public ArrayList<EOJ> getEoj() {
        return eoj;
    }

    public void setEoj(ArrayList<EOJ> eoj) {
        this.eoj = eoj;
    }

    public Date getLatestResponseTime() {
        return latestResponseTime;
    }

    public void updateTime() {
        latestResponseTime = CurrentTime();
    }
    
    public boolean isDead(int timeout) {
        long diff = CurrentTime().getTime() - latestResponseTime.getTime();
        return diff > timeout;
    }
    
    private Date CurrentTime() {
        Calendar cal = Calendar.getInstance();
        return cal.getTime();
    }
    
    
}
