package gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

import javax.swing.*;
import log.Logger;

/**
 * Что требуется сделать:
 * 1. Метод создания меню перегружен функционалом и трудно читается. 
 * Следует разделить его на серию более простых методов (или вообще выделить отдельный класс).
 *
 */
public class MainApplicationFrame extends JFrame
{
    private final JDesktopPane desktopPane = new JDesktopPane();
    private LogWindow logWindow;
    private GameWindow gameWindow;
    private Rectangle panePosition = new Rectangle();
    private Rectangle logPosition = new Rectangle();
    private Rectangle gamePosition = new Rectangle();
    ArrayList<String> strings = new ArrayList<String>();
    
    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;
        try(BufferedReader reader = new BufferedReader(new FileReader("../config.rbt"))) {
       	 	String line;
       	 	while ((line = reader.readLine()) != null) {
       	 		strings.add(line);
       	 	}
    	}
    	catch(IOException e) {
    		System.out.println(e.getMessage());
    	}
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
            screenSize.width  - inset*2,
            screenSize.height - inset*2);
        
        setContentPane(desktopPane);
        
        logWindow = createLogWindow();
        addWindow(logWindow);

        gameWindow = new GameWindow();
        gameWindow.setSize(400,  400);
        addWindow(gameWindow);
        
        getPosition();
        setBounds(panePosition);
        logWindow.setBounds(logPosition);
        gameWindow.setBounds(gamePosition);
        
        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        JMenuBar menuBar = generateMenuBar();
        menuBar.add(createMenuBar());
        setJMenuBar(menuBar);
    }
    
    protected void savePosition() {
    	String str = "";
    	
    	Rectangle paneRect = this.getBounds();
    	str += paneRect.x + ":" + paneRect.y + ":" + paneRect.width + ":" + paneRect.height + "\n";
    	Rectangle logRect = logWindow.getBounds();
    	str += logRect.x + ":" + logRect.y + ":" + logRect.width + ":" + logRect.height + "\n";
    	Rectangle gameRect = gameWindow.getBounds();
    	str += gameRect.x + ":" + gameRect.y + ":" + gameRect.width + ":" + gameRect.height;
    	
    	try(FileWriter writer = new FileWriter("../config.rbt", false)) {
    		writer.write(str);
    		writer.flush();
    		System.out.println(str);
    	}
    	catch(IOException e) {
    		System.out.println(e.getMessage());
    	}
    }
    
    protected void getPosition(){
    	String[] lp = strings.get(0).split(":");
    	String[] ll = strings.get(1).split(":");
    	String[] lg = strings.get(2).split(":");
    	panePosition = new Rectangle(Integer.parseInt(lp[0]), Integer.parseInt(lp[1]), Integer.parseInt(lp[2]), Integer.parseInt(lp[3])) ;
    	logPosition = new Rectangle(Integer.parseInt(ll[0]), Integer.parseInt(ll[1]), Integer.parseInt(ll[2]), Integer.parseInt(ll[3])) ;
    	gamePosition = new Rectangle(Integer.parseInt(lg[0]), Integer.parseInt(lg[1]), Integer.parseInt(lg[2]), Integer.parseInt(lg[3])) ;
    }
    
    protected void exit(){
    	Object[] options = { "Да", "Нет" }; 
    	int selectedOption = JOptionPane.showOptionDialog(null, "Вы уверены, что хотите выйти?", 
    			"Подтверждение", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options,
                options[0]); 
    	if (selectedOption == JOptionPane.YES_OPTION) {
    		savePosition();
    		System.exit(0);
    		}
    }
    
    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        //logWindow.setBounds();
        logWindow.setLocation(10,10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }
    
    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }
    
    protected JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
 
        //Set up the lone menu.
        JMenu menu = new JMenu("Document");
        menu.setMnemonic(KeyEvent.VK_D);
        menuBar.add(menu);
 
        //Set up the first menu item.
        JMenuItem menuItem = new JMenuItem("New");
        menuItem.setMnemonic(KeyEvent.VK_N);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_N, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("new");
        menuItem.addActionListener((event) -> {});
        menu.add(menuItem);
  
        //Set up the second menu item.
        menuItem = new JMenuItem("Quit");
        menuItem.setMnemonic(KeyEvent.VK_Q);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_Q, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("quit");
        menuItem.addActionListener((event) -> {
        	exit();});
        menu.add(menuItem);
 
        return menuBar;
    }
   
    private JMenuBar generateMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");
        
        {
            JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
            systemLookAndFeel.addActionListener((event) -> {
                setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                this.invalidate();
            });
            lookAndFeelMenu.add(systemLookAndFeel);
        }

        {
            JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_S);
            crossplatformLookAndFeel.addActionListener((event) -> {
                setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                this.invalidate();
            });
            lookAndFeelMenu.add(crossplatformLookAndFeel);
        }

        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");
        
        {
            JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
            addLogMessageItem.addActionListener((event) -> {
                Logger.debug("Новая строка");
            });
            testMenu.add(addLogMessageItem);
        }        
        menuBar.add(lookAndFeelMenu);
        menuBar.add(testMenu);
        return menuBar;
    }
    
    private void setLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (ClassNotFoundException | InstantiationException
            | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            // just ignore
        }
    }
}
