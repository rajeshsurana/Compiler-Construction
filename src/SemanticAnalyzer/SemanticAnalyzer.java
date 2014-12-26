package SemanticAnalyzer;
import compiler.*;
import parser.Parser;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

public class SemanticAnalyzer {
  
  private static Gui gui;
  private static final Hashtable<String, Vector<SymbolTableItem>> symbolTable = new Hashtable<String, Vector<SymbolTableItem>>();
  private static final Stack stack = new Stack();
  
  // create here a data structure for the cube of types
  private static final int INT = 0;
  private static final int FLOAT = 1;
  private static final int CHAR = 2;
  private static final int STRING = 3;
  private static final int BOOLEAN = 4;
  private static final int VOID = 5;
  private static final int ERROR = 6;
  private static final int OP_MIN_MUL_DIV = 0;
  private static final int OP_PLU = 1;
  private static final int OP_NEG = 2;
  private static final int OP_GRE_LES = 3;
  private static final int OP_EQEQ_NOEQ = 4;
  private static final int OP_AND_OR = 5;
  private static final int OP_NOT = 6; 
  //Awesome Incredible Cube
  private static final String[][][] cube ={
  {
   {  "int", "float", "error", "error", "error", "error", "error"},
   {"float", "float", "error", "error", "error", "error", "error"},
   {"error", "error", "error", "error", "error", "error", "error"},
   {"error", "error", "error", "error", "error", "error", "error"},
   {"error", "error", "error", "error", "error", "error", "error"},
   {"error", "error", "error", "error", "error", "error", "error"},
   {"error", "error", "error", "error", "error", "error", "error"}
  },
 
  {
   {  "int", "float", "error", "string", "error", "error", "error"},
   {"float", "float", "error", "string", "error", "error", "error"},
   {"error", "error", "error", "string", "error", "error", "error"},
   {"string", "string", "string", "string", "string", "error", "error"},
   {"error", "error", "error", "string", "error", "error", "error"},
   {"error", "error", "error", "error", "error", "error", "error"},
   {"error", "error", "error", "error", "error", "error", "error"}
  },
  
  {
   {  "int", "float", "error", "error", "error", "error", "error"},
  },
  
  {
   {"boolean", "boolean",  "error", "error", "error", "error", "error"},
   {"boolean", "boolean",  "error", "error", "error", "error", "error"},
   {"error", "error", "error", "error", "error", "error", "error"},
   {"error", "error", "error", "error", "error", "error", "error"},
   {"error", "error", "error", "error", "error", "error", "error"},
   {"error", "error", "error", "error", "error", "error", "error"},
   {"error", "error", "error", "error", "error", "error", "error"}
  },
  
  {
   {"boolean", "boolean",  "error", "error", "error", "error", "error"},
   {"boolean", "boolean",  "error", "error", "error", "error", "error"},
   {"error", "error", "boolean", "error", "error", "error", "error"},
   {"error", "error", "error", "boolean", "error", "error", "error"},
   {"error", "error", "error", "error", "boolean", "error", "error"},
   {"error", "error", "error", "error", "error", "error", "error"},
   {"error", "error", "error", "error", "error", "error", "error"}
  },
  
  {
   {"error", "error", "error", "error", "error", "error", "error"},
   {"error", "error", "error", "error", "error", "error", "error"},
   {"error", "error", "error", "error", "error", "error", "error"},
   {"error", "error", "error", "error", "error", "error", "error"},
   {"error", "error", "error", "error", "boolean", "error", "error"},
   {"error", "error", "error", "error", "error", "error", "error"},
   {"error", "error", "error", "error", "error", "error", "error"}
  },
  
  {
   {"error", "error", "error", "error", "boolean", "error", "error"},
  }
  };
  
  public static Hashtable<String, Vector<SymbolTableItem>> getSymbolTable() {
    return symbolTable;
  }
  
  public static void checkVariable(String type, String id, int lineNo) {
   
    // A. search the id in the symbol table
    if(!symbolTable.containsKey(id)){
   
    // B. if !exist then insert: type, scope=global, value={0, false, "", '')
        Vector v = new Vector();
        switch (type) {
            case "string":
                v.add(new SymbolTableItem(type,"global", ""));
                break;
            case "void":
                v.add(new SymbolTableItem(type,"global", ""));
                break;
            case "int":
                v.add(new SymbolTableItem(type,"global", "0"));
                break;
            case "float":
                v.add(new SymbolTableItem(type,"global", "0.0"));
                break;
            case "char":
                v.add(new SymbolTableItem(type,"global", "''"));
                break;
            case "boolean":
                v.add(new SymbolTableItem(type,"global", "false"));
                break;
        }
        symbolTable.put(id, v);
    }
    else{
    // C. else error: “variable id is already defined”
        error(gui, 1, lineNo, id);
    }
    
  }

  public static void pushStack(String type) {
  
    // push type in the stack
      stack.add(type);
  }
  
  public static String popStack() {
    String result="";
    // pop a value from the stack
    if(!stack.isEmpty())
        result= stack.pop().toString();
    return result;
  }
  
  
  public static String calculateCube(String type, String operator) {
    String result="";
    // unary operator ( - and !)
    int Dim1=2;
    int Dim3=6;
    int Dim2 = 0; // Because of unary operator
    
    switch(type)
    {
        case "int":
            Dim3 = INT;
            break;
        case "float":
            Dim3 = FLOAT;
            break;
        case "char":
            Dim3 = CHAR;
            break;
        case "string":
            Dim3 = STRING;
            break;
        case "boolean":
            Dim3 = BOOLEAN;
            break;
        case "void":
            Dim3 = VOID;
            break;
        case "error":
            Dim3 = ERROR;
    }
    
    if (operator == "-")
        Dim1 = OP_NEG;
    else if (operator == "!")
        Dim1 = OP_NOT;      
    
    result = cube[Dim1][Dim2][Dim3];
    
    return result;
  }

  public static String calculateCube(String type1, String type2, String operator) {
    String result="";
    // binary operator ( - and !)
    int Dim1=0;
    int Dim2=6; 
    int Dim3=6;
        
    if(!operator.equals("=")){
    switch(type1)
    {
        case "int":
            Dim2 = INT;
            break;
        case "float":
            Dim2 = FLOAT;
            break;
        case "char":
            Dim2 = CHAR;
            break;
        case "string":
            Dim2 = STRING;
            break;
        case "boolean":
            Dim2 = BOOLEAN;
            break;
        case "void":
            Dim2 = VOID;
            break;
        case "error":
            Dim2 = ERROR;
    }
    
    switch(type2)
    {
        case "int":
            Dim3 = INT;
            break;
        case "float":
            Dim3 = FLOAT;
            break;
        case "char":
            Dim3 = CHAR;
            break;
        case "string":
            Dim3 = STRING;
            break;
        case "boolean":
            Dim3 = BOOLEAN;
            break;
        case "void":
            Dim3 = VOID;
            break;
        case "error":
            Dim3 = ERROR;
    }
    }
    if ((operator == "-") || (operator == "*") || (operator == "/"))
        Dim1 = OP_MIN_MUL_DIV;
    else if (operator == "+")
        Dim1 = OP_PLU;
    else if ((operator == "<") || (operator == ">"))
        Dim1 = OP_GRE_LES;
    else if ((operator == "==") || (operator == "!="))
        Dim1 = OP_EQEQ_NOEQ; 
    else if ((operator == "&") || (operator == "|"))
        Dim1 = OP_AND_OR;
    else if(operator.equals("="))
    {
        if(type1.equals(type2) || type2.equals("float")&& type1.equals("int"))
            result = "OK";
        return result;
    }
    result = cube[Dim1][Dim2][Dim3];
    return result;
  }
  
  public static void error(Gui gui, int err, int n, String info) {
      
    switch (err) {
      case 0: 
        gui.writeConsole("Line " + n + ":[Semantic] variable <"+ info + "> not found"); 
        break;
      case 1: 
        gui.writeConsole("Line " + n + ":[Semantic] variable <"+ info + "> is already defined"); 
        break;
      case 2: 
        gui.writeConsole("Line " + n + ":[Semantic] incompatible types: type mismatch"); 
        break;
      case 3: 
        gui.writeConsole("Line " + n + ":[Semantic] incompatible types: expected boolean"); 
        break;
      case 4:
        gui.writeConsole("Line " + n + ":[Semantic] incompatible types: expected integer / octal / hexadecimal / binary");
        break;
    }
  }
   
  public static String getIdType(String Id, int lineNo){
      
  Vector<SymbolTableItem> tempSymbolTableItem;   
  tempSymbolTableItem = symbolTable.get(Id);
  if(tempSymbolTableItem == null){
      error(gui, 0, lineNo, Id);
   return "error";
  }
    return ((tempSymbolTableItem.firstElement()).getType());    
  }
  
  public static void initializeGUI(Gui g){
      
  gui=g;
  }
  
  public static void clearAll(){
      
      getSymbolTable().clear();
      while(!stack.isEmpty())
          stack.pop();
  }
}
