package Advanced;

import java.util.ArrayList;


public abstract class Field {
    final private String name;
    final private boolean mandatory;
    final private boolean request;

    public Field(String name, boolean mandatory, boolean request) {
        this.name = name;
        this.mandatory = mandatory;
        this.request = request;
    }

    //Getters
    public String getName() {return name;}
    public boolean isMandatory() {return mandatory;}
    public boolean isRequest() {return request;}

    /**
     * Print field data
     * @param indentation
     */
    public abstract void print(int indentation);
}

class StringField extends Field{
    final private String allowedValues;

    //Constuctors
    public StringField(String name, boolean mandatory, boolean request, String allowedValues) {
        super(name, mandatory, request);
        this.allowedValues = allowedValues;
    }

    public StringField(String name, boolean mandatory, boolean request) {
        this(name, mandatory, request, null);
    }

    public StringField(String name, boolean mandatory) {
        this(name, mandatory, true, null);
    }

    public StringField(String name) {
        this(name, true, true, null);
    }

    //Getters
    public String getAllowedValues() {return allowedValues;}

    @Override
    public void print(int indentation){
        String indent = "\t".repeat(indentation);
        System.out.println(indent + "Field name = " + this.getName());
        System.out.println(indent + "Mandatory = " + (this.isMandatory() ? "YES" : "NO"));
        System.out.println(indent + "Allowed values = " + (allowedValues == null ? "(ALL)" : allowedValues));
    }

}

class ObjectField extends Field{
    final private ArrayList<Field> children;

    //Constuctors
    public ObjectField(String name, boolean mandatory, boolean request) {
        super(name, mandatory, request);
        this.children = new ArrayList<>();
    }

    //Getters
    public ArrayList<Field> getChildren() {return children;}
    public ObjectField findMyParent(String parentName){
        if(this.getName().equals(parentName)) return this;
        else{
            for(Field f : children){
                if(f instanceof ObjectField) {
                    ObjectField possibleParent = ((ObjectField)f).findMyParent(parentName);
                    if(possibleParent != null) return possibleParent;
                }
            }
        }
        return null;
    }

    //Add child
    public void addChild(Field child){children.add(child);}

    @Override
    public void print(int indentation){
        String indent = "\t".repeat(indentation);
        System.out.println(indent + "Field name = " + this.getName());
        System.out.println(indent + "Mandatory = " + (this.isMandatory() ? "YES" : "NO"));
        if(children.size() > 0){
            System.out.println(indent + "Children :");
            for(Field child : children) {
                child.print(indentation + 1);
                System.out.println("");
            };
        }
    }

}