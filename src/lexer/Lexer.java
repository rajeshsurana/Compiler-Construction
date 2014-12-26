
package lexer;

import java.util.Vector;

public class Lexer {
 
  private String text;
  private Vector<Token> tokens; 
  private static final String[] KEYWORD = {"if", "else", "while", "switch", 
    "case", "return", "int", "float", "void", "char", "string", "boolean", 
    "true", "false", "print", "default"};
  //Constants; YOU WILL NEED TO DEFINE MORE CONSTANTS
  private static final int ZERO           =  1;
  private static final int ONE            =  2;
  private static final int B              =  0; 
  private static final int OTHER          =  3;
  private static final int DELIMITER      =  4;
  private static final int ERROR          =  4;
  private static final int STOP           = -2;
  private static final int RANGE_2_7      =  5;
  private static final int RANGE_8_9      =  6;
  private static final int x_X            =  7;
  private static final int DOT            =  8;
  private static final int e_E            =  9;
  private static final int PLUS_MINUS     = 10;
  private static final int SINGLE_QUOTE   = 11;
  private static final int ESCAPE_CHAR    = 12;
  private static final int Aa_Ff          = 13;
  private static final int Gg_Zz          = 14;
  private static final int DOLLAR_UNDERSCORE= 15;
  private static final int SPECIAL_SYMBOL = 16;
  private static final int DOUBLE_QUOTE   = 17;
  
  // states table; THIS IS THE TABLE FOR BINARY NUMBERS; YOU SHOLD COMPLETE IT
  private static final int[][] stateTable = { 
    {   20,     1,     5, ERROR,  STOP,     5,     5,    20,    22,    20,  STOP,    14, ERROR,    20,    20,    20, ERROR,    18}, 
    {    2,     6,     6, ERROR,  STOP,     6, ERROR,     7,     9,    11,  STOP,  STOP, ERROR, ERROR, ERROR, ERROR, ERROR,  STOP}, 
    {ERROR,     3,     3, ERROR,  STOP, ERROR, ERROR, ERROR, ERROR, ERROR,  STOP,  STOP, ERROR, ERROR, ERROR, ERROR, ERROR,  STOP},
    {ERROR,     3,     3, ERROR,  STOP, ERROR, ERROR, ERROR, ERROR, ERROR,  STOP,  STOP, ERROR, ERROR, ERROR, ERROR, ERROR,  STOP},
    {ERROR, ERROR, ERROR, ERROR,  STOP, ERROR, ERROR, ERROR, ERROR, ERROR,  STOP,  STOP, ERROR, ERROR, ERROR, ERROR, ERROR,  STOP},
    {ERROR,     5,     5, ERROR,  STOP,     5,     5, ERROR,     9,    11,  STOP,  STOP, ERROR, ERROR, ERROR, ERROR, ERROR,  STOP},
    {ERROR,     6,     6, ERROR,  STOP,     6, ERROR, ERROR, ERROR, ERROR,  STOP,  STOP, ERROR, ERROR, ERROR, ERROR, ERROR,  STOP},
    {    8,     8,     8, ERROR,  STOP,     8,     8, ERROR, ERROR, ERROR,  STOP,  STOP, ERROR,     8, ERROR, ERROR, ERROR,  STOP},
    {    8,     8,     8, ERROR,  STOP,     8,     8, ERROR, ERROR, ERROR,  STOP,  STOP, ERROR,     8, ERROR, ERROR, ERROR,  STOP},
    {ERROR,    10,    10, ERROR,  STOP,    10,    10, ERROR, ERROR,    11,  STOP,  STOP, ERROR, ERROR, ERROR, ERROR, ERROR,  STOP},
    {ERROR,    10,    10, ERROR,  STOP,    10,    10, ERROR, ERROR,    11,  STOP,  STOP, ERROR, ERROR, ERROR, ERROR, ERROR,  STOP},
    {ERROR,    13,    13, ERROR,  STOP,    13,    13, ERROR, ERROR, ERROR,    12,  STOP, ERROR, ERROR, ERROR, ERROR, ERROR,  STOP},
    {ERROR,    13,    13, ERROR,  STOP,    13,    13, ERROR, ERROR, ERROR,  STOP,  STOP, ERROR, ERROR, ERROR, ERROR, ERROR,  STOP},
    {ERROR,    13,    13, ERROR,  STOP,    13,    13, ERROR, ERROR, ERROR,  STOP,  STOP, ERROR, ERROR, ERROR, ERROR, ERROR,  STOP},
    {   16,    16,    16, ERROR,    16,    16,    16,    16,    16,    16,    16,  STOP,    15,    16,    16,    16,    16,    16},
    {   17,    17,    17, ERROR,    17,    17,    17,    17,    17,    17,    17,    16,    17,    17,    17,    17,    17,    17},
    {   16,    16,    16,    16,    16,    16,    16,    16,    16,    16,    16,  STOP,    15,    16,    16,    16,    16,    16},
    {   16,    16,    16,    16,    16,    16,    16,    16,    16,    16,    16,  STOP,    15,    16,    16,    16,    16,    16},    
    {   18,    18,    18, ERROR,    18,    18,    18,    18,    18,    18,    18,    18,    19,    18,    18,    18,    18,  STOP},
    {   18,    18,    18, ERROR,    18,    18,    18,    18,    18,    18,    18,    18,    18,    18,    18,    18,    18,    21},
    {   20,    20,    20, ERROR,  STOP,    20,    20,    20, ERROR,    20,  STOP,  STOP, ERROR,    20,    20,    20, ERROR,  STOP},
    {   18,    18,    18, ERROR,    18,    18,    18,    18,    18,    18,    18,    18,    19,    18,    18,    18,    18,  STOP},
    {ERROR,    10,    10, ERROR,  STOP,    10,    10, ERROR, ERROR, ERROR,  STOP,  STOP, ERROR, ERROR, ERROR, ERROR, ERROR,  STOP},
  };
  
  //constructor
  public Lexer(String text) {
    this.text = text;
  }

  //run
  public void run () {
    tokens = new Vector<Token>();
    String line;
    int counterOfLines= 1;
    // split lines
    do {
      int eolAt = text.indexOf(/*System.lineSeparator()*/"\n");
      if (eolAt >= 0) {
        line = text.substring(0,eolAt); 
        if (text.length()>0) text = text.substring(eolAt+1);  
      } else {
        line = text;
        text = "";
      }
      splitLine (counterOfLines, line);
      counterOfLines++;
    } while ( !text.equals("") );   
  }
  
  //slit line
  private void splitLine(int row, String line) {
    int state = 0;
    int index = 0;
    char currentChar = ' ';
    char previousChar;
    String string="";
    if (line.equals("")) return; 
    //DFA working
    int go; 
    do { 
      previousChar = currentChar;
      currentChar = line.charAt(index);
      go = calculateNextState(state, currentChar);  
      if( go != STOP ) {
        string = string + currentChar;   
        state = go;
      }
      index++;        
    } while (index < line.length() && go != STOP);
    //review final state
    if (state == 3) {
      tokens.add(new Token(string, "BINARY", row));
    } else if (state == 1|| state == 5){
        tokens.add(new Token(string, "INTEGER", row));
    } else if (state == 6){
        tokens.add(new Token(string, "OCTAL", row));
    } else if (state == 8){
        tokens.add(new Token(string, "HEXADECIMAL", row));
    } else if (state == 9 || state == 10 || state == 13){
        tokens.add(new Token(string, "FLOAT", row));
    } else if (state == 16 && currentChar == '\'' && previousChar != '\\'){
        if(string.length() == 2)
            tokens.add(new Token(string+currentChar, "CHARACTER", row));
        else if (string.length() == 3 && previousChar == '\'')
            tokens.add(new Token(string+currentChar, "CHARACTER", row));
        else
            tokens.add(new Token(string+currentChar, "ERROR", row));
    } else if (state == 17 && currentChar == '\''/* && previousChar == '\\'*/){
        if(string.length() == 3)
            tokens.add(new Token(string+currentChar, "CHARACTER", row));
        else
            tokens.add(new Token(string+currentChar, "ERROR", row));
    } else if (state == 14){
        if(currentChar == '\'' && previousChar == '\'')
          tokens.add(new Token(string+currentChar, "ERROR", row));
        else
          tokens.add(new Token(string, "ERROR", row));
    } else if (state == 18 && currentChar == '"'){
        if( string.length()==1 && previousChar == '"')
            tokens.add(new Token(string+currentChar, "STRING", row));
        else if(string.length()>1)
            tokens.add(new Token(string+currentChar, "STRING", row));
        else
            tokens.add(new Token(string, "ERROR", row));
    } else if (state == 21 && currentChar == '"' && previousChar == '"'){
        tokens.add(new Token(string+currentChar, "STRING", row)); 
    } else if (state == 20){
        int x;
        for( x =0; x<KEYWORD.length; x++){
            if (string.equals(KEYWORD[x])){
                tokens.add(new Token(string, "KEYWORD", row)); 
                break;
            }
        }
        if (x >= KEYWORD.length)
            tokens.add(new Token(string, "IDENTIFIER", row)); 
    } else {
      if (!string.equals(""))
        tokens.add(new Token(string, "ERROR", row));
    }
    
    if (currentChar == '='){
        if(tokens.size()>0){
            Token token_temp;
            token_temp = tokens.lastElement();
            if((token_temp.getWord()).equals("=") && (token_temp.getLine() == row))
            {
                tokens.remove(tokens.size()-1);
                tokens.add(new Token("==", "OPERATOR", row));   
            }else if((token_temp.getWord()).equals("!") && (token_temp.getLine() == row)){
                tokens.remove(tokens.size()-1);
                tokens.add(new Token("!=", "OPERATOR", row));  
            }else
                tokens.add(new Token(currentChar+"", "OPERATOR", row));
        }else
            tokens.add(new Token(currentChar+"", "OPERATOR", row));   
    }
    
    // current char
    if( isDelimiter(currentChar) && state != 18 && state !=21 && state != 16 && state != 17)
      tokens.add(new Token(currentChar+"", "DELIMITER", row));
    else if (isOperator(currentChar) && currentChar != '=' && state != 18 && state !=21 && state != 16 && state != 17)
      tokens.add(new Token(currentChar+"", "OPERATOR", row));
    
    if (((state > 0 && state < 14) || state == 20) && currentChar == '"')
      index--;
    if (((state > 0 && state < 14) || state == 20) && currentChar == '\'')
      index--;
    // loop
    if (index < line.length()) 
      splitLine(row, line.substring(index));
  }
  
  // calculate state
  private int calculateNextState(int state, char currentChar) {
    if (currentChar == 'b') 
      return stateTable [state][B];
    else if (currentChar == '0')
      return stateTable [state][ZERO];    
    else if (currentChar == '1')
      return stateTable [state][ONE];
    else if (isInRange2_7(currentChar))
      return stateTable [state][RANGE_2_7];
    else if (currentChar == '8' || currentChar == '9')
      return stateTable [state][RANGE_8_9];
    else if (currentChar == 'x'|| currentChar == 'X')
      return stateTable [state][x_X];
    else if (currentChar == '.')
      return stateTable [state][DOT];
    else if (currentChar == 'e' || currentChar == 'E')
      return stateTable [state][e_E];
    else if (currentChar == '+' || currentChar == '-')
      return stateTable [state][PLUS_MINUS];
    else if (currentChar == '\'') 
      return stateTable [state][SINGLE_QUOTE];
    else if (currentChar == '\\') 
      return stateTable [state][ESCAPE_CHAR];
    else if (isRangeA_F(currentChar))
      return stateTable [state][Aa_Ff];
    else if (isRangeG_Z(currentChar))
      return stateTable [state][Gg_Zz];
    else if (currentChar == '$' || currentChar == '_')
      return stateTable [state][DOLLAR_UNDERSCORE];
    else if (isSpecialSymbol(currentChar))
      return stateTable [state][SPECIAL_SYMBOL];
    else if (currentChar == '"')
      return stateTable [state][DOUBLE_QUOTE];
    else if (isSpace(currentChar)  || isDelimiter(currentChar) || 
           isOperator(currentChar) || isQuotationMark(currentChar))
      return stateTable[state][DELIMITER];
    return stateTable [state][OTHER];
  }
 
  // isDelimiter
  private boolean isDelimiter(char c) {
     char [] delimiters = {':', ';', '}','{', '[',']','(',')',','};
     for (int x=0; x<delimiters.length; x++) {
      if (c == delimiters[x]) return true;      
     }
     return false;
  }
  
  
  // isOperator
  private boolean isOperator(char o) {
     // == and != should be handled in splitLine
     char [] operators = {'+', '-', '*','/','<','>','=','!','&','|'};
     for (int x=0; x<operators.length; x++) {
      if (o == operators[x]) return true;      
     }
     return false;
  }

  // isQuotationMark
  private boolean isQuotationMark(char o) {
     char [] quote = {'"', '\''};
     for (int x=0; x<quote.length; x++) {
      if (o == quote[x]) return true;      
     }
     return false;
  }

  // isSpace
  private boolean isSpace(char o) {
     return (o == ' ')||(o == '\t');
  }
  
  // getTokens
  public Vector<Token> getTokens() {
    return tokens;
  }

  // isInRange1_9
  private boolean isInRange2_7(char o)
  {
      char [] range2_7 = {'2', '3', '4', '5', '6', '7'};
      for (int x=0; x<range2_7.length; x++)
      {
          if (o == range2_7[x])
              return true;
      }
      return false;
  }
  
 
  // isSpecialSymbol
  private boolean isSpecialSymbol(char o)
  {
      char [] specialSym = {'@', '#', '%', '^', '?'};
      for (int x=0; x<specialSym.length; x++)
      {
          if (o == specialSym[x])
              return true;
      }
      return false;
  }
    
  // isRangeA_F
  private boolean isRangeA_F(char o)
  {
      return ((int)o <= 70 && (int)o >= 65)|| ((int)o <= 102 && (int)o >= 97);
  }
  
  // isRangeG_Z
  private boolean isRangeG_Z(char o)
  {
      return ((int)o <= 90 && (int)o >= 71)|| ((int)o <= 122 && (int)o >= 103);
  }
}
