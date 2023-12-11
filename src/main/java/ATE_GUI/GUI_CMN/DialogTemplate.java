package ATE_GUI.GUI_CMN;

import org.jfree.chart.ChartPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public abstract class DialogTemplate {
    public static JDialog d;
    private static ChartPanel chartPanel=null;
    public static int item[]= {0,0,0,0,0}; // template supports up to 5 selection lists
    public JButton start;
    public DialogTemplate(JFrame OwnerFrame, String title, Dimension dimension) {
        d = new JDialog(OwnerFrame , title, true);
        d.setLocationRelativeTo(OwnerFrame);
        d.setSize(dimension);
        d.getContentPane().setLayout(new BoxLayout(d.getContentPane(), BoxLayout.Y_AXIS));
        d.setMaximumSize(dimension);
        d.setMinimumSize(dimension);
        d.setPreferredSize(dimension);
        JButton b = new JButton ("Exit");
        b.addActionListener ( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                d.setVisible(false);
            }
        });
        start = new JButton ("Start");
        d.add(b);
        d.add(start);
    }

    protected void AddStartActionListener()
    {
        // override this in any dialog
        start.addActionListener ( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
            }
        });
    }

    protected JScrollPane SetSelectionList(String[] list, int item_ix) {
        JList SelectionList = new JList(list);
        int max_item_len = 0;
        int N = list.length;
        for (int i=0;i<N;i++)
            if(list[i].length() > max_item_len)
                max_item_len = list[i].length();
        SelectionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        SelectionList.setVisibleRowCount(N);
        SelectionList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                item[item_ix] = SelectionList.getSelectedIndex();
                System.out.println("Control item="+item[item_ix]);
            }
        });
        JScrollPane LSP = new JScrollPane(SelectionList);
        LSP.setMaximumSize(new Dimension(max_item_len*16,18*(N+1)));
        return LSP;
    }


    protected JLabel SetLabel(String text, Dimension dim) {
        JLabel label = new JLabel (text);
        label.setMinimumSize(dim);
        label.setPreferredSize(dim);
        label.setMaximumSize(dim);
        d.add(label );
        return label;
    }

    protected void SetXYChart(int width, int height) {
        chartPanel = new ChartPanel(
                null,width,height,width,height,width, height,
                false,false,false,false,false,false);
        d.add(chartPanel);
    }


}

