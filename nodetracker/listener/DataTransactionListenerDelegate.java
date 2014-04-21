/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nuttt.nodetracker.listener;

import echowand.net.Property;

/**
 *
 * @author nuttt
 */
public interface DataTransactionListenerDelegate {
    public void process(Property property);
}
