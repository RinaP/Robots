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
    private CoordWindow coordWindow;
    private LogWindow logWindow;
    private GameWindow gameWindow;
    private Rectangle panePosition = new Rectangle();
    private Rectangle logPosition = new Rectangle();
    private Rectangle gamePosition = new Rectangle();
    private int counter = 0;
    
    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;
        ArrayList<String> positions = readConfig();
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
        
        coordWindow = new CoordWindow(gameWindow.m_visualizer);
        coordWindow.setLocation(870, 90);
        coordWindow.setSize(250, 90);
        addWindow(coordWindow);
        
        setPosition(positions);
        setBounds(panePosition);
        logWindow.setBounds(logPosition);
        gameWindow.setBounds(gamePosition);
        
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setVisible(true);
        
        setJMenuBar(generateMenuBar());
        this.addWindowListener(new WindowAdapter() {
        	
			public void windowClosing(WindowEvent e) {
					exit(e);}
        });
        
        JMenuBar menuBar = generateMenuBar();
        menuBar.add(createMenuBar());
        setJMenuBar(menuBar);
    }
    protected ArrayList<String> readConfig(){
    	ArrayList<String> coords = new ArrayList<String>();
    	try(BufferedReader reader = new BufferedReader(new FileReader("../config.rbt"))) {
       	 	String line;
       	 	while ((line = reader.readLine()) != null) {
       	 		coords.add(line);
       	 	}
    	}
    	catch(IOException e) {
    		System.out.println(e.getMessage());
    	}
    	return coords;
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
    protected void setPanePosition(String[] arr){
    	panePosition = new Rectangle(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]), Integer.parseInt(arr[2]), Integer.parseInt(arr[3])) ;
    }
    protected void setLogPosition(String[] arr){
    	logPosition = new Rectangle(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]), Integer.parseInt(arr[2]), Integer.parseInt(arr[3])) ;
    }
    protected void setGamePosition(String[] arr){
    	gamePosition = new Rectangle(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]), Integer.parseInt(arr[2]), Integer.parseInt(arr[3])) ;
    }
    
    protected void setPosition(ArrayList<String> coords){
    	try {
	    	String[] lp = coords.get(0).split(":");
	    	String[] ll = coords.get(1).split(":");
	    	String[] lg = coords.get(2).split(":");
	    	setPanePosition(lp);
	    	setLogPosition(ll);
	    	setGamePosition(lg);
	    }
    	catch(Exception e) {
    		System.out.println(e.getMessage());
    	}
    }
    
    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
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
//        menuBar.add(createFileMenu());
        
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
        menuItem.setMnemonic(KeyEvent.VK_Q | KeyEvent.VK_ALT);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_Q, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("quit");
        menuItem.addActionListener((event) -> {
        	WindowEvent winClosingEvent = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
        	close(winClosingEvent);
        });
        menu.add(menuItem);
 
        return menuBar;
    }
    protected void exit(WindowEvent ev){
    	if (counter == 0){
	    	Object[] options = { "Да", "Нет" }; 
	    	int selectedOption = JOptionPane.showOptionDialog(null, "Вы уверены, что хотите выйти?", 
	    			"Подтверждение", JOptionPane.YES_NO_OPTION,
	                JOptionPane.QUESTION_MESSAGE, null, options,
	                options[0]); 
	    	if (selectedOption == JOptionPane.YES_OPTION) {
		    		close(ev);}
    		}
    }
    public  void close (WindowEvent e)  {
    	savePosition();
		e.getWindow().setVisible(false);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(e);
        counter++;
    }
    private JMenu createLookAndFeelMenu() {
    	JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");
        lookAndFeelMenu.add(createLookAndFeelMenuItem("Системная схема", UIManager.getSystemLookAndFeelClassName()));
        lookAndFeelMenu.add(createLookAndFeelMenuItem("Универсальная схема", UIManager.getCrossPlatformLookAndFeelClassName()));
    	return lookAndFeelMenu;
    }
    private JMenu createTestMenu() {
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
    	return testMenu;
    }
    
    private JMenuBar generateMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createLookAndFeelMenu());
        menuBar.add(createTestMenu());
        return menuBar;
    }
    private JMenuItem createLookAndFeelMenuItem(String text, String lookAndFeelClassName) {
    	         JMenuItem menuItem = new JMenuItem(text, KeyEvent.VK_S);
    	         menuItem.addActionListener((event) -> {
    	             setLookAndFeel(lookAndFeelClassName);
    	             this.invalidate();
    	         });
    	         return menuItem;
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
