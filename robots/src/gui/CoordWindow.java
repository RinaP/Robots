package gui;

import java.awt.BorderLayout;
import java.util.*;
import javax.swing.*;

public class CoordWindow extends JInternalFrame implements Observer
{
	private double m_robotPosX;
    private double m_robotPosY;
    private JLabel label;
    @Override
	public void update(double m_robotPosX, double m_robotPosY, double m_robotDirect, int m_targetPosX, int m_targetPosY) {
        this.m_robotPosX = m_robotPosX;
        this.m_robotPosY = m_robotPosY;
        this.label.setText("          Текущие координаты:  " + Math.round(m_robotPosX) + "    " + Math.round(m_robotPosY));
    }
    
    public CoordWindow(Robot robot)
    {
        super("Координаты", true, true, true, true);
        robot.registerObserver(this);

        JPanel panel = new JPanel(new BorderLayout());
        label = new JLabel("          Текущие координаты:  " + m_robotPosX + "   " + m_robotPosY, JLabel.LEFT);
        panel.add(label);
        getContentPane().add(panel);
        setSize(100, 50);
        pack();
    }
}
