/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.acesinc.data.json.generator.config;

import java.util.ArrayList;
import java.util.List;

/**
 * classe che serve per configurare il flusso dei dati
 * che si desidera eseguire
 * un esempio del file json di configurazione del workflow può essere
 * 
 * "workflows": [{
            "workflowName": "test",
            "workflowFilename": "exampleWorkflow.json",
            "instances": 1,
            "customTypeHandlers": ["com.example.types", "org.example2.types"]
        }]
 * 
 * @author andrewserff
 */
public class WorkflowConfig {
	//nome per il flusso di lavoro
    private String workflowName;
    //nome del flie di configurazione del flusso di lavoro da eseguire
    private String workflowFilename;
    //numero di copie indentiche di istanze da eseguire
    private int instances = 1;
    //elenco di pacchetti in cui sono definiti gestori di tipi personalizzati 
    private List<String> customTypeHandlers = new ArrayList<>();
    /**
     * @return the workflowName
     */
    public String getWorkflowName() {
        return workflowName;
    }

    /**
     * @param workflowName the workflowName to set
     */
    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    /**
     * @return the workflowFilename
     */
    public String getWorkflowFilename() {
        return workflowFilename;
    }

    /**
     * @param workflowFilename the workflowFilename to set
     */
    public void setWorkflowFilename(String workflowFilename) {
        this.workflowFilename = workflowFilename;
    }

    public int getInstances() {
        return instances;
    }

    public void setInstances(int instances) {
        this.instances = instances;
    }

    public List<String> getCustomTypeHandlers() {
        return customTypeHandlers;
    }

    public void setCustomTypeHandlers(List<String> customTypeHandlers) {
        this.customTypeHandlers = customTypeHandlers;
    }

}
