package Advanced;

import java.io.File;  
import java.io.FileInputStream;  
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Cell;  
import org.apache.poi.ss.usermodel.Row;  
import org.apache.poi.xssf.usermodel.XSSFSheet;  
import org.apache.poi.xssf.usermodel.XSSFWorkbook; 

public class Utility {
    
    final private XSSFWorkbook wb;
    final private String fileName;

    public Utility(String path) throws FileNotFoundException, IOException {
        File workbook = new File(path);   //creating a new file instance
        FileInputStream fis = new FileInputStream(workbook);   //obtaining bytes from the file  
        //creating Workbook instance that refers to .xlsx file  
        this.wb = new XSSFWorkbook(fis);   
        this.fileName = path.substring(path.lastIndexOf('/') + 1, path.lastIndexOf('.'));
    }
    
    public Service extractData() throws IOException {
        XSSFSheet sheet = wb.getSheetAt(0); //Get first sheet
        Iterator<Row> r = sheet.iterator(); //Iterate over rows
        Service s = new Service(fileName);
        ArrayList<String> data= new ArrayList<>();
        String skip[] = {"HTTP Operation", "REST URL", "I/o", "Field Name", "Type", "Allowed Values", "Mandatory"};
        boolean flag = true;
        
        Operation op = null;
        Field f = null;
        
        //Clean data
        while (r.hasNext()){  
            Iterator<Cell> cellIterator = r.next().cellIterator(); 
            while (cellIterator.hasNext()){  
                Cell cell = cellIterator.next();  
                String value = cell.toString();
                if(value.length() > 0){
                    flag = true;
                    for(String str : skip){
                        if(str.equals(value)){
                            flag = false;
                            break;
                        }
                    }
                    if(flag){
                        if(value.toUpperCase().charAt(0) == 'R'){ //API Name @ 'REST Operation Mapping (API_NAME)'
                            String apiName = value.substring(value.lastIndexOf('(') + 1, value.lastIndexOf(')'));
                            cellIterator = r.next().cellIterator();
                            cell = cellIterator.next();
                            value = cell.toString();
                            
                            //Skip empty or title cells
                            while(value.length() == 0 || value.toUpperCase().charAt(0) == 'H'){
                                cellIterator = r.next().cellIterator();
                                cell = cellIterator.next();
                                value = cell.toString();
                            }
                            String method = value;
                            String url = cellIterator.next().toString();
                            op = new Operation(apiName, method, url); //Create API 
                            s.addOperation(op);

                        }else{ //Fields
                            boolean request = cell.toString().toUpperCase().equals("I"); //Request or response 
                            
                            //Next cell (Name)
                            cell = cellIterator.next();
                            value = cell.toString();
                            
                            String fields[] = value.split("/"); //Get the chain of names 
                            String fieldName = fields[fields.length - 1]; //Get field name

                            //Next cell (Type)
                            cell = cellIterator.next();
                            value = cell.toString();
                            
                            boolean isString = value.toLowerCase().equals("string");
                            
                            //Next cell (Allowed values)
                            cell = cellIterator.next();
                            value = cell.toString();
                            
                            String allowedValues = null;
                            if(value.length() > 0)allowedValues = value;
                            
                            //Next cell (Mandatory)
                            cell = cellIterator.next();
                            value = cell.toString();
                            
                            boolean mandatory = value.toUpperCase().equals("Y");
                            
                            if(isString) f = new StringField(fieldName, mandatory, request, allowedValues);
                            else f = new ObjectField(fieldName, mandatory, request);
                            
                            if(fields.length > 2){ //Field has parent
                                for(Field temp : op.getFields()){
                                    if(temp instanceof ObjectField){
                                        ObjectField possibleParent = ((ObjectField) temp).findMyParent(fields[fields.length - 2]);
                                        if(possibleParent != null) {
                                            possibleParent.addChild(f);
                                            break;
                                        }
                                    }
                                    
                                }
                            }else op.addField(f);
                        }
                    }
                }
                
            }  
        }  
        
        
        wb.close();
        return s;
    }
    
}
