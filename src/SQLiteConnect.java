import java.io.*;
import java.sql.SQLException;
import java.util.List;

public class SQLiteConnect extends Main {
    public void doWrite() throws IOException,SQLException{
        File file = new File("C:/Users/asd/Desktop/script_3.txt"); //Database değişimlerinnde kayıt yerini main deki adres ile aynı olacak şekilde değiştirin.
        Writer writer = new FileWriter(file,true);
        int i,p = 0;
        int j = 0;
        int temp = 0;
        int p_temp = 0;
        int columnTemp = 0;
        int rowTemp = 0;
        String columnsInTable = null;
        String createTable = null;
        int getNumberOfTables = ConnectionSingleton.getInstance().getNumberOfTables();
        for (i = 0; i < getNumberOfTables; i++){
            int rowNumber = ConnectionSingleton.getInstance().getRowNumberInTheEachTable().get(i);
            String tableName = ConnectionSingleton.getInstance().getTables()[i];
            int columnNumber = ConnectionSingleton.getInstance().getColumnNumberInTheTable()[i];
            createTable = "CREATE TABLE " + "`" + tableName  + "` (";
            writer.write(createTable);
            writer.flush();
            for (j = temp; j < temp + columnNumber; j++) {
                columnsInTable = " `" + ConnectionSingleton.getInstance().getColumns()[j] + "` " +
                        ConnectionSingleton.getInstance().getMySQLColumnTypes()[j] + " " +
                        ConnectionSingleton.getInstance().getColumnNullable().get(j);
                writer.write(columnsInTable);
                writer.flush();
                if (j < temp + columnNumber - 1){
                    writer.write(", ");
                }
            }
            temp = temp + columnNumber;
            int getPrimaryKeySize = ConnectionSingleton.getInstance().getPrimaryKeys(i).size();
            if (getPrimaryKeySize > 0) {
                writer.write(" ,PRIMARY KEY (");
                for (int k = 0; k < getPrimaryKeySize; k++) {
                    writer.write(" `" + ConnectionSingleton.getInstance().getPrimaryKeys(i).get(k) + "`");
                    if (k < getPrimaryKeySize - 1) {
                        writer.write(" ,");
                        writer.flush();
                    }
                }
                writer.write("));");
            }else if (getPrimaryKeySize==0){
                writer.write(");");
            }
            writer.write("\n");
            writer.write("\n");

            String[] array = ConnectionSingleton.getInstance().getSingleRow(columnNumber,rowNumber,tableName);
            for (int a = rowTemp; a < rowTemp + rowNumber; a++){
                writer.write("\nINSERT INTO `" + tableName + "` (");
                writer.flush();
                for (int d = columnTemp; d < columnTemp + columnNumber;  d++) {
                    writer.write(ConnectionSingleton.getInstance().getColumns()[d]);
                    writer.flush();
                    if (d < columnTemp + columnNumber - 1){
                        writer.write(", ");
                    }else {
                        writer.write(")");
                    }
                }
                writer.write(" VALUES (");
                List<String> tempType = ConnectionSingleton.getInstance().getSpecificTypes(tableName);
                List<String> tempData = ConnectionSingleton.getInstance().getSpecificRow(columnNumber,p_temp,tableName);
                p_temp++;
                for (p = 0; p < tempType.size(); p++){
                    try {
                        if(tempType.get(p).equals("null") || tempType.get(p) == "null" || tempType.get(p).contains("null")){
                            writer.write("NULL");
                        }
                        else if (tempType.get(p).contains("varchar") && !tempType.get(p).contains("null")){
                            writer.write("'"+tempData.get(p).replaceAll("'","''")+"'");
                        }else if(tempType.get(p).contains("int")){
                            writer.write(tempData.get(p));
                        }else if(tempType.get(p).contains("bit(1)")){
                            writer.write("'\\"+tempData.get(p)+"'");  //'\0' bit
                        }
                    }catch (NullPointerException e){
                        writer.append("NULL");
                    }
                    if (p < columnNumber - 1){
                        writer.write(", ");
                    }else {
                        writer.write(");");
                    }
                }
            }
            columnTemp = columnTemp + columnNumber;
            rowTemp = rowTemp + ConnectionSingleton.getInstance().getRowNumberInTheEachTable().get(i);
            writer.write("\n");
            writer.write("\n");
            p_temp=0;
            rowNumber = 0;
            columnNumber = 0;
            tableName="";
        }
        writer.flush();
        writer.close();

    }
}