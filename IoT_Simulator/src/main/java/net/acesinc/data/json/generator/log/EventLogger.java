/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.acesinc.data.json.generator.log;

import java.util.Map;

/**
 * interfaccia implementata da tutti i tipi di Logger 
 * che sono i tipi di producers
 * @author andrewserff
 */
public interface EventLogger {
    public void logEvent(String event, Map<String, Object> producerConfig);
    public void shutdown();
}
