package Advanced;

import java.util.ArrayList;


public class Operation {
    
    final private String name;
    final private String method;
    final private String url;
    final private ArrayList<Field> fields;

    public Operation(String name, String method, String url) {
        this.name = name;
        this.method = method;
        this.url = url;
        this.fields = new ArrayList<>();
    }

    //Getters
    public String getName() {return name;}
    public String getMethod() {return method;}
    public String getUrl() {return url;}
    public ArrayList<Field> getFields(){return fields;}
    public ArrayList<Field> getRequests(){
        ArrayList<Field> requests = new ArrayList<>();
        for(Field f:fields) if(f.isRequest()) requests.add(f);
        return requests;
    }
    public ArrayList<Field> getResponses(){
        ArrayList<Field> responses = new ArrayList<>();
        for(Field f:fields) if(!f.isRequest()) responses.add(f);
        return responses;
    }

    //Add field
    public void addField(Field field){this.fields.add(field);}
    
}
