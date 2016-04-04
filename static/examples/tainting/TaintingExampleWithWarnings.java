import org.checkerframework.checker.tainting.qual.*;

public class TaintingExampleWithWarnings {
  String getUserInput() {
      return "taintedStr";
    }
    
  void processRequest() {
      @Tainted String input = getUserInput();
      executeQuery(input); //error: pass tainted string to executeQeury()
    }
  
  public void executeQuery(@Untainted String input) {
        //Do some SQL Query 
    }
    
  
  /*To eliminate wraning in line 10, replace line 10 by
    executeQuery(validate(input));*/
  /*@Untainted*/ public String validate(String userInput) {
    //Do some validation here
    @SuppressWarnings("tainting")
    @Untainted String result = userInput;
    return result;
    }
  }
