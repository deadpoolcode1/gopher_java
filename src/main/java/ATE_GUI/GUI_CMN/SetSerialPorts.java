package ATE_GUI.GUI_CMN;


import ATE_MAIN.CMN_GD;
import LMDS_ICD.EnDef;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SetSerialPorts extends DialogTemplate {
    String[] port_names;
    public SetSerialPorts(JFrame OwnerFrame, String title, Dimension dimension, String[] port_names) {
        super(OwnerFrame, title, dimension);
        AddStartActionListener();
        this.port_names = port_names;
        JLabel l0 = SetLabel("Up to two serial ports are supported. Select the service Port and the other port.", new Dimension(dimension.width-10, 20));
        JLabel l2 = SetLabel("Hit START (Other port shall allow serial testing only).", new Dimension(dimension.width-10, 20));
        JScrollPane LSP0 = SetSelectionList(port_names,0);
        JLabel l1 = SetLabel("Select the service Port: ", new Dimension(dimension.width-10, 20));
        d.add(LSP0);
        JScrollPane LSP1 = SetSelectionList(port_names,1);
        JLabel l3 = SetLabel("Select the other Port: ", new Dimension(dimension.width-10, 20));
        d.add(LSP1);
        d.setVisible(true);
    }
    @Override
    protected void AddStartActionListener() {
        start.addActionListener ( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                // this test program supports up to 2 serial ports; two selection lists are used for defining the service and other port
                // item[0] is the index returned from the service port list; item[1] is the index returned from the other port list;
                if(port_names.length ==0) {
                    System.out.println("No Serial ports found.");
                    return;
                }
                CMN_GD.ServicePort = item[0]; // set the index of the service port
                if((port_names.length ==1) || (item[0]) == item[1]) {
                    System.out.println("Serial ports selection: Service="+port_names[CMN_GD.ServicePort]+"  Other=None");
                    return;
                }
                CMN_GD.OtherPort = item[1]; // set the index of the other port
                System.out.println("Serial ports selection: Service="+port_names[CMN_GD.ServicePort]+"  Other="+port_names[CMN_GD.OtherPort]);
            }
        });
    }

}
