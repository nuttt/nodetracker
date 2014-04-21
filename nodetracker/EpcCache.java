/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nuttt.nodetracker;

import echowand.common.EPC;
import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author nuttt
 */
public class EpcCache {
    public ArrayList<EPC> Observable;
    public ArrayList<EPC> Setable;
    public ArrayList<EPC> Getable;
    public HashSet<EPC> AllEPC;

    public EpcCache() {
        this.Observable = new ArrayList<EPC>();
        this.Setable = new ArrayList<EPC>();
        this.Getable = new ArrayList<EPC>();
        this.AllEPC = new HashSet<EPC>();
    }

    @Override
    public String toString() {
        return "EpcCache{" + "Observable=" + Observable + ", Setable=" + Setable + ", Getable=" + Getable + ", AllEPC=" + AllEPC + '}';
    }

    
    
    
    
}
