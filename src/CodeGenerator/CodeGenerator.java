package CodeGenerator;
import compiler.*;
import java.util.Vector;

/**
 *
 * @author javiergs
 */
public class CodeGenerator {
  
  private static final Vector<String> variables = new Vector<>();  
  private static final Vector<String> labels = new Vector<>();  
  private static final Vector<String> instructions = new Vector<>();

  // To add instruction in the String vector 
  public static void addInstruction(String instruction, String p1, String p2) {
    instructions.add(instruction + " " + p1 + ", " + p2);
  }

  // To add lable for JMP and JMC instructions
  public static void addLabel(String name, int value) {
    labels.add("#"+name + ", int, " + value);
  }
  
  // To add varible  
  public static void addVariable(String type, String name) {
    variables.add(name + ", " + type + ", global, null" );
  }

  // To write code in the console
  public static void writeCode(Gui gui) {
    for (String variable : variables) {
      gui.writeCode(variable);    
    }
    for (String label : labels) {
      gui.writeCode(label);    
    }
    gui.writeCode("@");
    for (String instruction : instructions) {
      gui.writeCode(instruction);    
    }

  }
  
  // To clear all the datastructures
  public static void clear(Gui gui) {
    variables.clear();
    instructions.clear();
    labels.clear();
  }
  
  //To get number of the instructions
  public static int getInstructionCount(){
    return instructions.size();
  }
    
}
