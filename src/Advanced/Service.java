package Advanced;

import java.util.ArrayList;


public class Service {
    
    final private String name;
    final private ArrayList<Operation> operations;

    public Service(String name) {
        this.name = name;
        this.operations = new ArrayList<>();
    }

    //Getters
    public String getName() {return name;}
    public ArrayList<Operation> getOperations() {return operations;}

    //Add operation
    public void addOperation(Operation op){this.operations.add(op);}
    
}
