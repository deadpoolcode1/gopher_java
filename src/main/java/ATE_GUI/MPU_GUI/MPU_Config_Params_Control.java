package ATE_GUI.MPU_GUI;

import ATE_GUI.GUI_CMN.DialogTemplate;
import ATE_MAIN.CMN_GD;
import ATE_MAIN.main;
import LMDS_ICD.EnDef;
import LMDS_ICD.MessageBody;
import LMDS_ICD.config_param;
import LMDS_ICD.read_config_param;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class MPU_Config_Params_Control extends DialogTemplate {
    /**
     * buttons: start, exit (standard), write/read mode (toggle)
     * selection list: parameters set:
     *      Rudders Offset (X4, -127 to +127),
     *      Ailerons Offset (X4, -127 to +127),
     *      Save all to flash
     * data text entry (comma separated)
     * Note: reading results shall be displayed on the main LUMO panel (congig params line)
     */
    private static JList SelectionList;
    private static final String[] listItems =
            { "Rudders Offset (X4, -127 to +127)", //0
              "Ailerons Offset (X4, -127 to +127)", //1
              "Save all to flash" //2
            };
    private static JTextField JTF_Parameter_Values;
    private static JToggleButton JTB_Control_Mode;
    private static boolean control_mode = true; // read=true, write=false

    public MPU_Config_Params_Control(JFrame OwnerFrame, String title, Dimension dimension) {
        super(OwnerFrame, title, dimension);
        JLabel l1 = SetLabel("Define Read/Write mode, select parameters set or save all, if write: enter values and hit start",
                new Dimension(600, 20));
        JLabel l2 = SetLabel("For saving to flash, Read/Write mode and values are irrelevant.",
                new Dimension(600, 20));
        AddStartActionListener();
        JTB_Control_Mode = new JToggleButton("Read Mode");
        ItemListener JTB_Control_Mode_itemListener = new ItemListener() {
            public void itemStateChanged(ItemEvent itemEvent)
            {
                int state = itemEvent.getStateChange();
                if (state == ItemEvent.SELECTED) {
                    control_mode = true;
                    JTB_Control_Mode.setText("Read  Mode");
                    JTB_Control_Mode.setSelected(control_mode);
                }
                else {
                    control_mode = false;
                    JTB_Control_Mode.setText("Write Mode");
                    JTB_Control_Mode.setSelected(control_mode);
                }
            }
        };
        JTB_Control_Mode.addItemListener(JTB_Control_Mode_itemListener);
        JTB_Control_Mode.setSelected(true); // initial state is Read = selected
        d.add(JTB_Control_Mode, BorderLayout.NORTH);

        JScrollPane LSP = SetSelectionList(listItems,0);
        d.add(LSP);
        JLabel l3 = SetLabel("Values entry: integers, comma separated.", new Dimension(600, 20));
        JTF_Parameter_Values = new JTextField(24) ;
        JTF_Parameter_Values.setMaximumSize(new Dimension(350,20));
        d.add(JTF_Parameter_Values);
        d.pack();
        d.setVisible(true);
    }
    @Override
    protected void AddStartActionListener() {
        // override this in any dialog
        start.addActionListener ( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                EnDef.msg_code_ce msg_code = EnDef.msg_code_ce.MSG_CODE_UNKNOWN;
                MessageBody msg_body = null;
                System.out.println("MPU Config Params Control!");
                switch(item[0])
                {
                    case 0: // Rudders Offset (X4, -127 to +127)
                        if(control_mode) {
                            // read mode
                            msg_body = SetReadMessage(EnDef.config_params_ce.CP_MPU_TAIL_OFST);
                            msg_code = EnDef.msg_code_ce.MSG_CODE_READ_CNFG_PARAM;
                       }
                        else {
                            // write mode
                            msg_body = SetWriteMessage(EnDef.config_params_ce.CP_MPU_TAIL_OFST, JTF_Parameter_Values.getText());
                            msg_code = EnDef.msg_code_ce.MSG_CODE_WRITE_GET_CNFG_PARAM;
                            if(msg_body == null) {
                                System.out.println("MPU Config Params Control - case 0, illegal write parameter values. Can't send.  "+item[0]);
                                return;
                            }
                        }
                        break;
                    case 1: // Ailerons Offset (X4, -127 to +127)
                        if(control_mode) {
                            // read mode
                            msg_body = SetReadMessage(EnDef.config_params_ce.CP_MPU_AILRN_OFST);
                            msg_code = EnDef.msg_code_ce.MSG_CODE_READ_CNFG_PARAM;
                        }
                        else {
                            // write mode
                            msg_body = SetWriteMessage(EnDef.config_params_ce.CP_MPU_AILRN_OFST, JTF_Parameter_Values.getText());
                            msg_code = EnDef.msg_code_ce.MSG_CODE_WRITE_GET_CNFG_PARAM;
                            if(msg_body == null) {
                                System.out.println("MPU Config Params Control - case 1, illegal write parameter values. Can't send.  "+item[0]);
                                return;
                            }
                        }
                        break;
                    case 2: // Save all to flash
                        msg_code = EnDef.msg_code_ce.MSG_CODE_SAVE_ALL_CNFG_PARAMS;
                        msg_body = null; // NO message body !
                        break;
                    default:
                        System.out.println("MPU Config Params Control - illegal selection item. Can't send.  "+item[0]);
                        return;

                }
                if(CMN_GD.ServicePort < 0) {
                    System.out.println("MPU Config Params Control - Service comm port inactive. Can't send.");
                    return;
                }
                main.SendMessage(CMN_GD.ServicePort,
                        main.GetNewAddress(EnDef.host_name_ce.HOST_MPU, EnDef.process_name_ce.PR_TST_CMND),
                        main.GetNewAddress(EnDef.host_name_ce.HOST_ATE_SERVER, EnDef.process_name_ce.PR_ATE_TF3),
                        msg_code, msg_body);
            }
        });
    }

    private MessageBody SetReadMessage(EnDef.config_params_ce configParamsID) {
        read_config_param RCP = new read_config_param();
        RCP.config_params_id = configParamsID;
        return RCP;
    }

    private MessageBody SetWriteMessage(EnDef.config_params_ce configParamsID, String val_text) {
        config_param CP = new config_param();
        boolean bad_values = false;
        CP.config_params_id = configParamsID;
        switch(configParamsID){
            case CP_MPU_TAIL_OFST:
            case CP_MPU_AILRN_OFST:
                bad_values = SetMessageValues(CP, val_text,4,1,-127,127); break;
            default:
                System.out.println("MPU Config Params Control - illegal parameter ID. Can't send.  "+configParamsID);
                return null;
        }
        if(bad_values) {
            // error parsing the values for the config parameters
            System.out.println("MPU Config Params Control - illegal parameter values. Can't send.  "+val_text);
            return null;
        }
        return CP;
    }

    private boolean SetMessageValues(config_param cp, String txt, int nflds, int fld_size, int min_val, int max_val) {
        String[] valst = txt.split(","); //split the input string into separate strings
        int data_ix=0;
        if(valst.length != nflds) return true; // wrong number of fields found
        for(int i=0;i<nflds;i++){
            double valf = main.GetDouble(valst[i], (double)min_val, (double)max_val);
            if(valf == -999.999) return true; // bad parameter syntax or out of range
            if(fld_size == 1) {
                // single byte values
                byte bv = (byte)((int)valf);
                cp.data[data_ix] = bv;
                data_ix++;
            }
            if(fld_size == 2) {
                // short values (2 bytes)
                short bv = (short)((int)valf);
                cp.data[data_ix] = (byte)(bv& 0xff);
                cp.data[data_ix+1] = (byte)((bv>>> 8)& 0xff);
                data_ix+=2;
            }
        }
        return false; // message data built OK in cp
    }
}

