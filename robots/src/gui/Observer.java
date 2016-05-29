package gui;

public interface Observer {
    void update(double m_robotPosX, double m_robotPosY, double m_robotDirect, int m_targetPosX, int m_targetPosY);
}
