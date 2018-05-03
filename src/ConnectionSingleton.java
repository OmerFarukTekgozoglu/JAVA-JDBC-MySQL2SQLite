import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class ConnectionSingleton {
    //Singleton object
    private static ConnectionSingleton ourInstance;
    private String DB_NAME = "content";       // DATABASE İSMİ BURADAN DEĞİŞECEKTİR.
    private final String DB_URL = "jdbc:mysql://localhost:3306/"+DB_NAME+"?useSSL=false";
    private final String DB_USER = "<your username>";
    private final String DB_PASSWORD = "<your password>";
    private String[] columnList,arrList,typeListInColumns;
    private Integer[] getColumnNumberInTheTable;
    private Connection connection = null;
    private ConnectionSingleton() throws SQLException {
        getConnection();
    }

    //region connect codes
    private Connection connect() throws SQLException {
        return getConnection();
    }

    private Connection getConnection() throws SQLException {
        if(connection == null || connection.isClosed()){
            try {
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                System.out.println("Connection has been established!");
            }catch (SQLException sqlException){
                sqlException.printStackTrace();
            }
        }
        return connection;
    }
    //endregion
    public int getNumberOfColumns() throws SQLException{
        ResultSet resultSet;
        Statement statement;
        String sql = "select count(*)\n" +
                "from information_schema.columns\n" +
                "where table_schema = "+"'"+DB_NAME+"'";
        statement = getConnection().createStatement();
        resultSet = statement.executeQuery(sql);
        String num = null;
        while (resultSet.next()){
            num = resultSet.getString(1);
        }
        return Integer.valueOf(num);
    }

    //region column data getter from database
    public String[] getColumns() throws SQLException {
        String[] columnList = new String[getNumberOfColumns()];
        ResultSet columnResult;
        DatabaseMetaData metaDataColumn = getConnection().getMetaData();
        columnResult = metaDataColumn.getColumns(null,null,null,null);
        int count2 = 0;
        while (columnResult.next()){
            columnList[count2] = columnResult.getString(4); //+ " " + columnResult.getString("TYPE_NAME");
            count2++;
        }
        this.columnList = columnList;
        return columnList;
    }

    public String[] getColumnList() {
        return columnList;
    }
    //endregion

    //region table data getter from database


    public int getNumberOfTables() throws SQLException{
        ResultSet resultSet;
        Statement statement;
        String sql = "select count(*)\n" +
                "from information_schema.tables\n" +
                "where table_schema ="+"'"+DB_NAME+"';";
        statement = getConnection().createStatement();
        resultSet = statement.executeQuery(sql);
        String tablenum = null;
        while (resultSet.next()){
            tablenum = resultSet.getString(1);
        }
        return Integer.valueOf(tablenum);
    }

    public String[] getTables() throws SQLException {
        String[] arrList = new String[getNumberOfTables()];
        ResultSet resultSet;
        DatabaseMetaData databaseMetaData = getConnection().getMetaData();
        resultSet = databaseMetaData.getTables(null, null, null, null);
        int count = 0;
        while (resultSet.next()) {
            arrList[count] =  resultSet.getString(3);
            count++;
        }
        this.arrList = arrList;
        return arrList;
    }

    public String[] getArrList() {
        return arrList;
    }
    //endregion

    //region column number in each table array from database
    public Integer[] getGetColumnNumberInTheTable() {
        return getColumnNumberInTheTable;
    }

    public Integer[] getColumnNumberInTheTable() throws SQLException{
        Statement statement = null;
        ResultSet resultSet = null;
        int numberOfColumnParameters = 0;
        Integer[] numberOfColumnNamesInTheTable = new Integer[getNumberOfTables()];
        for (int i = 0; i < getNumberOfTables(); i++) {
            String sql = "SHOW COLUMNS FROM " +
                    DB_NAME+"." +
                    getArrList()[i];
            statement = ConnectionSingleton.getInstance().getConnection().createStatement();
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()){
                numberOfColumnParameters++;
            }
            numberOfColumnNamesInTheTable[i] = numberOfColumnParameters;
            numberOfColumnParameters = 0;
        }
        this.getColumnNumberInTheTable = getColumnNumberInTheTable;
        return numberOfColumnNamesInTheTable;
    }
    //endregion

    //region type list getter from database
    public String[] getTypeListInColumns() {
        return typeListInColumns;
    }

    public String[] getMySQLColumnTypes() throws SQLException{
        ResultSet resultSet;
        String[] typeListInColumns = new String[getNumberOfColumns()];
        Statement statement = null;
        int count = 0;
        for (int i = 0; i < getNumberOfTables(); i++) {
            String sql = "SHOW FIELDS FROM "+ DB_NAME+"." + getArrList()[i];
            statement = getConnection().createStatement();
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()){
                typeListInColumns[count] = resultSet.getString("TYPE");
                count++;
            }
        }
        this.typeListInColumns = typeListInColumns;
        return typeListInColumns;
    }
    //endregion

    public List<String > getSpecificTypes(String tableName) throws SQLException{
        List<String> typeList = new ArrayList<String>();
        ResultSet result = null;
        Statement st = null;
        String sql = "show fields from "+DB_NAME+"."+tableName+";";
        st = getConnection().createStatement();
        result = st.executeQuery(sql);
        while (result.next()){
            typeList.add(result.getString(2));
        }
        return typeList;
    }

    public List<String> getColumnNullable() throws SQLException{
        ResultSet resultSet = null;
        List<String> nullableListInColumns = new ArrayList<String>();
        Statement statement = null;
        for (int i = 0; i < ConnectionSingleton.getInstance().getTables().length; i++) {
            String sql = "EXPLAIN "+DB_NAME+"." + getArrList()[i];
            statement = ConnectionSingleton.getInstance().getConnection().createStatement();
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()){
                nullableListInColumns.add(resultSet.getString(3));
            }
            ListIterator<String > iterator = nullableListInColumns.listIterator();
            while (iterator.hasNext()){
                String next = iterator.next();
                if (next.equals("NO")){
                    iterator.set("NOT NULL");
                } else if (next.equals("YES")){
                    iterator.set("DEFAULT NULL");
                }
            }
        }
        return nullableListInColumns;
    }


    public List<String> getPrimaryKeys(int primaryKeyParameter) throws SQLException{
        ResultSet resultSet = null;
        List<String > primaryKeys = new ArrayList<String >();
        DatabaseMetaData meta = getConnection().getMetaData();
        resultSet = meta.getPrimaryKeys(null,null,getTables()[primaryKeyParameter]);
        int j = 0;
        while (resultSet.next()){
            primaryKeys.add(j,resultSet.getString("COLUMN_NAME"));
            j++;
        }
        return primaryKeys;
    }

    public List<Integer> getRowNumberInTheEachTable() throws SQLException{
        Statement statement= null;
        ResultSet resultSet = null;
        statement = getConnection().createStatement();
        List<Integer> rowNumber = new ArrayList<Integer>();
        for (int i = 0; i < getNumberOfTables(); i++){
            String SQLQuery = "SELECT * FROM "+DB_NAME+"." + getTables()[i];
            resultSet = statement.executeQuery(SQLQuery);
            int count = 0;
            while (resultSet.next()){
                count++;
            }
            rowNumber.add(i,count);
        }
        return rowNumber;
    }


    public String[] getSingleRow(int columnNumber, int rowNumber,String tableName) throws SQLException{
        Statement st = null;
        ResultSet rs = null;
        st = getConnection().createStatement();
        String[] deneme = new String[columnNumber * rowNumber];
        int index = 0;
        for (int i = 0 ; i < rowNumber; i++){
            String query = "select * from "+DB_NAME+"."+tableName+" limit " + i +",1;";
            rs = st.executeQuery(query);
            while (rs.next()){
                for (int j = 1; j <= columnNumber; j++){
                    deneme[index] = rs.getString(j);
                    index++;
                }
            }
        }
        return deneme;
    }

    public List<String> getSpecificRow(int columnNumber,int numberOfRow,String tableName) throws SQLException{
        Statement st1=null;
        ResultSet rs1=null;
        st1 = getConnection().createStatement();
        List<String> dataList = new ArrayList<String>();
        int index1 = 0;
        String sql1 = "select * from "+DB_NAME+"."+tableName+" limit "+ numberOfRow+",1;";
        rs1 = st1.executeQuery(sql1);
        while (rs1.next()){
            for (int j = 1; j<= columnNumber; j++){
                dataList.add(rs1.getString(j));
            }
        }


        return dataList;
    }

    public static ConnectionSingleton getInstance() throws SQLException {
        if(ourInstance == null)
             ourInstance= new ConnectionSingleton();
        return ourInstance;
    }



}
