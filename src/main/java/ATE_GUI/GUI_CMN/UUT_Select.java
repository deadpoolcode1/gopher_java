package ATE_GUI.GUI_CMN;

import ATE_MAIN.CMN_GD;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UUT_Select extends JFrame { // TODO dead code!
    private static JPanel UUTSelectPanel;
    private static JList UUTSelectionList;
    JFrame ThisFrame = this;

    private static final String[] listItems =
            { "LUMO UUT", "PCM UUT", "CBU UUT"};
    public UUT_Select() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 300);
        setTitle("Select the UUT for testing:");
        getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        //getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        UUTSelectPanel = new JPanel();
        //TestSelectPanel.setLayout(new BoxLayout(TestSelectPanel, BoxLayout.Y_AXIS));
        UUTSelectionList = new JList(listItems);
        UUTSelectionList.setFixedCellHeight(15);
        UUTSelectionList.setFixedCellWidth(150);
        UUTSelectionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        UUTSelectionList.setVisibleRowCount(5);
        UUTSelectionList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                int item = UUTSelectionList.getSelectedIndex();
                System.out.println("item="+item);
                switch(item) {
                    case 0: // LUMO UUT
                        CMN_GD.uut_id = CMN_GD.UUT_ID.UUT_LUMO;
                        break;
                    case 1:
                        CMN_GD.uut_id = CMN_GD.UUT_ID.UUT_PCM;
                        break;
                    case 2:
                        CMN_GD.uut_id = CMN_GD.UUT_ID.UUT_CBU;
                        break;
                    default:
                        System.out.println("UUT Select - illegal item selected: "+item);
                }
            }
        });

        UUTSelectPanel.add(new JScrollPane(UUTSelectionList));
        add(UUTSelectPanel);
        pack();
        setVisible(true);
    }

}
