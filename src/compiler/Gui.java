
package compiler;
import lexer.*;
import SemanticAnalyzer.*;
import parser.Parser;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static java.awt.event.InputEvent.CTRL_DOWN_MASK;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTabbedPane;
import javax.swing.JTree;

public class Gui extends JFrame implements ActionListener {
  
  private JTextArea console, codeArea;
  private JTextArea editor;
  private JTable    tokensTable; 
  private JTable    semanticTable;   
  private JMenuItem menuOpen = new JMenuItem("Open ...");    
  private JMenuItem menuCompiler = new JMenuItem("Compile");  
  private JTree     tree;
  private JPanel    treePanel = new JPanel(new GridLayout(1,1));
         
  public void writeCode(String msg) {
    codeArea.append(msg+"\n");
  }
  
  public void writeConsole(String msg) {
    console.append(msg+"\n");
  }

  private void writeEditor(String msg) {
    editor.append(msg+"\n");
    
  }
    
  private void writeTokenTable(Vector<Token> tokens) {
      for (Token token1 : tokens) {
        int line = token1.getLine();
        String token = token1.getToken();
        String word = token1.getWord();
        ((DefaultTableModel)tokensTable.getModel()).addRow(new Object[]{String.format("%04d", line), token, word});                  
      }           
  }
    
  public void writeSymbolTable(Hashtable<String, Vector<SymbolTableItem>> symbolTable) {
    if (symbolTable == null) return;
    Enumeration items = symbolTable.keys();
    if (items != null)
    while(items.hasMoreElements()) {
      String name = (String)items.nextElement();
      String type = ((SymbolTableItem)(symbolTable.get(name).get(0))).getType();
      String scope= ((SymbolTableItem)(symbolTable.get(name).get(0))).getScope();
      String value= ((SymbolTableItem)(symbolTable.get(name).get(0))).getValue();
      ((DefaultTableModel)semanticTable.getModel()).addRow(new Object[]{name, type, scope, });  
    }
    
  }
    
  private void clearTokenTable() {   
    int ta = ((DefaultTableModel)tokensTable.getModel()).getRowCount();
    for(int i=0; i< ta; i++)
     ((DefaultTableModel)tokensTable.getModel()).removeRow(0); 
  }
  
  private void clearSemanticTable() {   
    int ta = ((DefaultTableModel)semanticTable.getModel()).getRowCount();
    for(int i=0; i< ta; i++)
     ((DefaultTableModel)semanticTable.getModel()).removeRow(0); 
  }
  
  @Override
  public void actionPerformed(ActionEvent e) {
    if (menuOpen.equals(e.getSource())) {
      JFileChooser fc = new JFileChooser();
      FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt", "text");
      fc.setFileFilter(filter);    
      int returnVal = fc.showOpenDialog(this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        File file = fc.getSelectedFile();   
        console.setText("");
        codeArea.setText("");
        editor.setText(""); 
        clearTokenTable();
        clearSemanticTable();
        try {
          loadFile(file.getAbsolutePath());      
        } catch (IOException ex) {
          writeConsole(ex.toString());
        } 
      }
    } else if (menuCompiler.equals(e.getSource())) {
      clearTokenTable();
      clearSemanticTable();
      console.setText("");
      codeArea.setText("");
      // lexical analysis
      if (editor.getText().equals("")) {
          writeConsole("The file is empty");
          return;
      }
      Lexer lex = new Lexer (editor.getText());     

      lex.run();
      Vector<Token> tokens = lex.getTokens(); 
      // show token in a table
      writeTokenTable(tokens);
      // counting errors
      int errors = 0;
      for (Token token : tokens) {
        if (token.getToken().equals("ERROR")) {
          errors++;      
        }
      }
      // show stats on on the console
      writeConsole(tokens.size() + " strings found in " + tokens.get(tokens.size()-1).getLine() + " lines,");
      writeConsole(errors + " strings do not match any rule");
      // update tree
      treePanel.removeAll();
      tree = new JTree(Parser.run(tokens, this));
      JScrollPane treeView = new JScrollPane(tree);
      // expand nodes
      for (int i = 0; i < tree.getRowCount(); i++) {
         tree.expandRow(i);
      }
      treePanel.add(treeView);
      treePanel.revalidate();
      treePanel.repaint();
    }    
  }
  
  private boolean loadFile(String file) throws FileNotFoundException, IOException {       
    String line;
    BufferedReader br = new BufferedReader(new FileReader(file));    
    writeConsole("Reading " + file + "");
    line = br.readLine();  
    while (line != null ) {     
      writeEditor(line);       
      line = br.readLine();  
    }
    writeConsole("File loaded.");
    br.close();
    return true;
  }
  
  private void createMenu() {    
    JMenuBar menuBar = new JMenuBar();
    JMenu menuFile = new JMenu("File");
    JMenu menuRun = new JMenu("Run");  
    menuOpen.addActionListener(this);
    menuCompiler.addActionListener(this);
    menuFile.add(menuOpen);
    menuRun.add(menuCompiler);
    menuBar.add(menuFile);   
    menuBar.add(menuRun);  
    setJMenuBar(menuBar);
  }
      
  private void createGUI() {
    TitledBorder panelTitle;     
    setLayout(new BorderLayout());
    JPanel topPanel = new JPanel(new GridLayout(1,2));
    JPanel downPanel = new JPanel(new GridLayout(1,1));
    JPanel tokenPanel = new JPanel(new GridLayout(1,1));
    JPanel semanticPanel = new JPanel(new GridLayout(1,1)); 
    JPanel screenPanel = new JPanel(new GridLayout(1,1));
    JPanel consolePanel = new JPanel(new GridLayout(1,1));
    JPanel codePanel = new JPanel(new GridLayout(1,1));
    // screen
    panelTitle = BorderFactory.createTitledBorder("Source Code");
    screenPanel.setBorder(panelTitle);  
    editor = new JTextArea();
    editor.setEditable(true);
    JScrollPane scrollScreen = new JScrollPane(editor); 
    screenPanel.add(scrollScreen);
    // tokens 
    panelTitle = BorderFactory.createTitledBorder("Lexical Analysis");
    tokenPanel.setBorder(panelTitle);
    DefaultTableModel modelRegistry = new DefaultTableModel(); 
    tokensTable = new JTable(modelRegistry); 
    tokensTable.setShowGrid(true);
    tokensTable.setGridColor(Color.LIGHT_GRAY);
    tokensTable.setAutoCreateRowSorter(true);
    modelRegistry.addColumn("line"); 
    modelRegistry.addColumn("token");  
    modelRegistry.addColumn("string or word");
    JScrollPane scrollRegistry = new JScrollPane(tokensTable);
    tokensTable.setFillsViewportHeight(true);
    tokenPanel.add(scrollRegistry);
    tokensTable.setEnabled(false);
    // console
    panelTitle = BorderFactory.createTitledBorder("Console");
    consolePanel.setBorder(panelTitle);  
    console = new JTextArea();
    console.setEditable(false);
    console.setBackground(Color.black);
    console.setForeground(Color.white);    
    JScrollPane scrollConsole = new JScrollPane(console); 
    consolePanel.add(scrollConsole);
    // tree
    panelTitle = BorderFactory.createTitledBorder("Syntactical Analysis");
    treePanel.setBorder(panelTitle);
    JScrollPane treeView = new JScrollPane(new JLabel("After compilation, the parse Tree will be showed here", JLabel.CENTER));
    treePanel.add(treeView);
    // semantic
    panelTitle = BorderFactory.createTitledBorder("Symbol Table");
    semanticPanel.setBorder(panelTitle);
    DefaultTableModel modelSemantic= new DefaultTableModel(); 
    semanticTable = new JTable(modelSemantic); 
    semanticTable.setShowGrid(true);
    semanticTable.setGridColor(Color.LIGHT_GRAY);
    semanticTable.setAutoCreateRowSorter(true);
    modelSemantic.addColumn("name"); 
    modelSemantic.addColumn("type");  
    modelSemantic.addColumn("scope");
    modelSemantic.addColumn("value");
    JScrollPane scrollSemantic = new JScrollPane(semanticTable);
    semanticTable.setFillsViewportHeight(true);
    semanticPanel.add(scrollSemantic);
    semanticTable.setEnabled(false);
    // code
    panelTitle = BorderFactory.createTitledBorder("Intermediate Code");
    codePanel.setBorder(panelTitle);  
    codeArea = new JTextArea(); 
    JScrollPane scrollCode = new JScrollPane(codeArea); 
    codePanel.add(scrollCode);    
    // tabs
    JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.addTab("Lexer", tokenPanel);
    tabbedPane.addTab("Parser", treePanel);  
    tabbedPane.addTab("Semantic Analyzer", semanticPanel);  
    tabbedPane.addTab("Intermediate Code", codePanel);  
    tabbedPane.setSelectedIndex(3);
    // main frame
    topPanel.add(screenPanel);
    topPanel.add(tabbedPane);      
    downPanel.add(consolePanel);
    downPanel.setPreferredSize(new Dimension(getWidth(), getHeight()/4 ));
    add(topPanel, BorderLayout.CENTER);
    add(downPanel, BorderLayout.SOUTH);
    // editor hotkey
    menuCompiler.setAccelerator(KeyStroke.getKeyStroke('C', CTRL_DOWN_MASK));
  }
  
  public Gui(String title) throws IOException {
    super(title);
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
    }    
    Dimension dim = getToolkit().getScreenSize();
    setSize(3 * dim.width  / 4, 3 * dim.height / 4);
    setLocation((dim.width - getSize().width)/2, (dim.height - getSize().height)/2);    
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
    createMenu();
    createGUI();
  }
  
  public static void main(String[] args) throws FileNotFoundException, IOException {
    Gui gui = new Gui("CSE340 - Principles of Programming Languages");
    gui.setVisible(true);       
  }
  
}

