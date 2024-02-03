/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.acesinc.data.json.generator.workflow;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *Gli step del flusso di lavoro sono la base degli eventi json generati. 
 *Specificano come apparirà il tuo json e come verranno generati. 
 *A seconda di quello stepRunModeche hai scelto, il Generatore eseguirà i tuoi passi in diversi ordini. 
 *Le possibilità sono le seguenti:
 *
 *sequenziale: gli step verranno eseguiti nell'ordine in cui sono specificati nella matrice.
 *
 *casuale: gli step verranno mescolati ed eseguiti in ordine casuale. 
 *Gli step verranno rimescolati ogni volta che il flusso di lavoro viene ripetuto.
 *
 *random-pick-one: verrà scelto uno step casuale dalla configurazione e verrà eseguito. 
 *Nessun altro step verrà eseguito finché il workflow non verrà ripetuto. 
 *Quando il workflow si ripete, verrà selezionato un passaggio casuale diverso.
 *
 *Gli step sono poi specificati nella classe WorkflowStep
 *
 *exampleWorkflow.json:
{ questo fa parte del workflow
    "eventFrequency": 4000,
    "varyEventFrequency": true,
    "repeatWorkflow": true,
    "iterations": 5,
    "timeBetweenRepeat": 15000,
    "varyRepeatFrequency": true,
    "steps": [{
    			Questo fa parte degli steps
        "config": [{
    		"timestamp": "nowTimestamp()",
		    "system": "random('BADGE', 'AUDIT', 'WEB')",
		    "actor": "bob",
		    "action": "random('ENTER','LOGIN','EXIT', 'LOGOUT')",
		    "objects": ["Building 1"],
		    "location": {
		    	"lat": "double(-90.0, 90.0)",
		    	"lon": "double(-180.0, 180.0)"
		    },
		    "message": "Entered Building 1"
		}],
        "producerConfig": {
        },
        "duration": 0
    },{
        "config": [{
    		"timestamp": "nowTimestamp()",
		    "system": "random('BADGE', 'AUDIT', 'WEB')",
		    "actor": "jeff",
		    "action": "random('ENTER','LOGIN','EXIT', 'LOGOUT')",
		    "objects": ["Building 2"],
		    "location": {
		    	"lat": "double(-90.0, 90.0)",
		    	"lon": "double(-180.0, 180.0)"
		    },
		    "message": "Entered Building 2"
		}],
        "iterations": 2
    }]
}
 * @author andrewserff
 */
public class Workflow {
	// //Un elenco di step del  workflow da eseguire in questo workflow 
    private List<WorkflowStep> steps;
    /** how often events should be generated.  i.e. time between steps */
    //Il tempo in millisecondi degli eventi tra i passaggi dovrebbe essere emesso a
    private long eventFrequency;
    // Se true, una quantità di tempo casuale (tra 0 e la metà della frequenza dell'evento) verrà aggiunta o sottratta alla frequenza dell'evento
    private boolean varyEventFrequency;
    //Se è vero, il flusso di lavoro si ripeterà al termine
    private boolean repeatWorkflow;
    //Il tempo in millisecondi da attendere prima che il flusso di lavoro venga riavviato
    private long timeBetweenRepeat;
    //Se true, una quantità casuale di tempo (tra 0 e la metà della frequenza dell'evento) verrà aggiunta / sottratta al timeBewteenRepeat
    private boolean varyRepeatFrequency;
    //Valori possibili: sequential, random, random-pick-one. L'impostazione predefinita è sequenziale
    private String stepRunMode;
    //Il numero di volte in cui il flusso di lavoro verrà ripetuto. repeatWorkflowdeve essere impostato su true . 
    //Il valore predefinito è -1 (nessun limite).
    private long iterations = -1;
   
    public Workflow() {
    	
        steps = new ArrayList<>();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Workflow) {
            Workflow w = (Workflow)obj;
            if (w.getEventFrequency() != eventFrequency) { 
                return false;
            } 
            if (w.isVaryEventFrequency() != varyEventFrequency) {
                return false;
            }
            if (!w.getStepRunMode().equals(stepRunMode)) {
                return false;
            }
            if (w.getIterations() != iterations) {
                return false;
            }
            
           
            List<WorkflowStep> compSteps = w.getSteps();
            if (compSteps.size() != steps.size()) {
                return false;
            }
            for (int i = 0; i < compSteps.size(); i++) {
                WorkflowStep s = steps.get(i);
                WorkflowStep compS = compSteps.get(i);
                
                if (s.getDuration() != compS.getDuration()) {
                    return false;
                }
                
                List<Map<String, Object>> configs = s.getConfig();
                List<Map<String, Object>> compConfigs = compS.getConfig();
                
                if (configs.size() != compConfigs.size()) {
                    return false;
                }
                
                for (int j = 0; j < compConfigs.size(); j++) {
                    Map<String, Object> config = configs.get(j);
                    Map<String, Object> compConfig = compConfigs.get(j);
                    
                    if (config.size() != compConfig.size()) {
                        return false;
                    }
                    
                    Set<String> keys1 = new HashSet<>(config.keySet());
                    Set<String> keys2 = new HashSet<>(compConfig.keySet());
                    if (!keys1.equals(keys2)) {
                        return false;
                    }
                    
                    Set<Object> values1 = new HashSet<>(config.values());
                    Set<Object> values2 = new HashSet<>(compConfig.values());
                    if (!values1.equals(values2)) {
                        return false;
                    }
                }
            }
        } else {
            return false;
        }
        return true;
    }
    
    
    
    public void addStep(WorkflowStep step) {
        getSteps().add(step);
    }

    /**
     * @return the steps
     */
    public List<WorkflowStep> getSteps() {
        return steps;
    }

    /**
     * @param steps the steps to set
     */
    public void setSteps(List<WorkflowStep> steps) {
        this.steps = steps;
    }

    /**
     * @return the eventFrequency
     */
    public long getEventFrequency() {
        return eventFrequency;
    }

    /**
     * @param eventFrequency the eventFrequency to set
     */
    public void setEventFrequency(long eventFrequency) {
        this.eventFrequency = eventFrequency;
    }

    /**
     * @return the varyEventFrequency
     */
    public boolean isVaryEventFrequency() {
        return varyEventFrequency;
    }

    /**
     * @param varyEventFrequency the varyEventFrequency to set
     */
    public void setVaryEventFrequency(boolean varyEventFrequency) {
        this.varyEventFrequency = varyEventFrequency;
    }

    /**
     * @return the repeatWorkflow
     */
    public boolean isRepeatWorkflow() {
        return repeatWorkflow;
    }

    /**
     * @param repeatWorkflow the repeatWorkflow to set
     */
    public void setRepeatWorkflow(boolean repeatWorkflow) {
        this.repeatWorkflow = repeatWorkflow;
    }
    
    public boolean shouldRepeat(int currentIteration) {
        return repeatWorkflow && (iterations < 0 || currentIteration < iterations);
    }

    /**
     * @return the timeBetweenRepeat
     */
    public long getTimeBetweenRepeat() {
        return timeBetweenRepeat;
    }

    /**
     * @param timeBetweenRepeat the timeBetweenRepeat to set
     */
    public void setTimeBetweenRepeat(long timeBetweenRepeat) {
        this.timeBetweenRepeat = timeBetweenRepeat;
    }

    /**
     * @return the varyRepeatFrequency
     */
    public boolean isVaryRepeatFrequency() {
        return varyRepeatFrequency;
    }

    /**
     * @param varyRepeatFrequency the varyRepeatFrequency to set
     */
    public void setVaryRepeatFrequency(boolean varyRepeatFrequency) {
        this.varyRepeatFrequency = varyRepeatFrequency;
    }

    /**
     * @return the stepRunMode
     */
    public String getStepRunMode() {
        return stepRunMode;
    }

    /**
     * @param stepRunMode the stepRunMode to set
     */
    public void setStepRunMode(String stepRunMode) {
        this.stepRunMode = stepRunMode;
    }
    
    public long getIterations() {
        return iterations;
    }

    public void setIterations(long runCount) {
        this.iterations = runCount;
    }
    
}
