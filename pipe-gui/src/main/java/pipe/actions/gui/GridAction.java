package pipe.actions.gui;

import pipe.controllers.application.PipeApplicationController;
import pipe.gui.Grid;
import pipe.gui.PetriNetTab;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import uk.ac.imperial.pipe.models.manager.PetriNetManagerImpl;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;
import pipe.views.PipeApplicationView;

public class GridAction extends GuiAction {
    private final PipeApplicationController applicationController;

    public GridAction(PipeApplicationController applicationController) {
        super("Cycle grid", "Change the grid size (alt-G)", KeyEvent.VK_G, InputEvent.ALT_DOWN_MASK);
        this.applicationController = applicationController;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PetriNetTab petriNetTab = applicationController.getActiveTab();
        Grid grid = petriNetTab.getGrid();
        grid.increment();
        petriNetTab.repaint();
        
        
        
        //Saving check
       /* 
        PetriNetManagerImpl test = new PetriNetManagerImpl();
        PetriNet petriNet = (PetriNet) evt.getNewValue();
        test.savePetriNet(petriNet, outFile);
        */
        
        
        
    }

}
