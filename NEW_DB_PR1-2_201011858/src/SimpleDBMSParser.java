/* Generated By:JavaCC: Do not edit this line. SimpleDBMSParser.java */
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
// Import libraries from Berkeley DB
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

public class SimpleDBMSParser implements SimpleDBMSParserConstants {
  public static final int PRINT_SYNTAX_ERROR = 0;

  public static final int PRINT_CREATE_TABLE = 1;

  public static final int PRINT_DROP_TABLE = 2;

  public static final int PRINT_DESC = 3;

  public static String errorMsg;

  public static String currentTableName;

  public static Environment myDbEnvironment = null;

  public static EnvironmentConfig envConfig;

  public static Database myDatabase = null;

  public static DatabaseConfig dbConfig;

  public static void main(String args []) throws ParseException
  {
    /* OPENING DB */
    // Open Database Environment or if not, create one.
    envConfig = new EnvironmentConfig();
    envConfig.setAllowCreate(true);
    myDbEnvironment = new Environment(new File("db/"), envConfig);
    // Set database configuration
    dbConfig = new DatabaseConfig();
    dbConfig.setAllowCreate(true);
    dbConfig.setSortedDuplicates(false);
    SimpleDBMSParser parser = new SimpleDBMSParser(System.in);
    while (true)
    {
      try
      {
        System.out.print("DB_2010-11858> ");
        parser.command();
      }
      catch (Exception e)
      {
        printMessage(PRINT_SYNTAX_ERROR);
        SimpleDBMSParser.ReInit(System.in);
      }
    }
  }

  public static void printMessage(int q)
  {
    switch (q)
    {
      case PRINT_SYNTAX_ERROR :
      if (!currentTableName.equals(""))
      {
        myDbEnvironment.removeDatabase(null, currentTableName);
      }
      System.out.println("Syntax error");
      break;
      case PRINT_CREATE_TABLE :
      if (errorMsg.equals(""))
      {
        putKeyValue("@TABLELIST", currentTableName, currentTableName);
        System.out.println("\u005c'" + currentTableName + "\u005c' table is created");
      }
      else
      {
        System.out.println(errorMsg);
        myDbEnvironment.removeDatabase(null, "@TEMP");
      }
      break;
      case PRINT_DROP_TABLE :
      break;
      case PRINT_DESC :
      break;
    }
  }

  static public boolean findKeyValue(String dbName, String keyString)
  {
    Cursor cursor = null;
    myDatabase = myDbEnvironment.openDatabase(null, dbName, dbConfig);
    cursor = myDatabase.openCursor(null, null);
    DatabaseEntry foundKey;
    DatabaseEntry foundValue;
    boolean result = false;
    try
    {
      foundKey = new DatabaseEntry(keyString.getBytes("UTF-8"));
      foundValue = new DatabaseEntry();
      if (cursor.getSearchKey(foundKey, foundValue, LockMode.DEFAULT) == OperationStatus.SUCCESS)
      {
        result = true;
      }
      else
      {
        result = false;
      }
    }
    catch (DatabaseException de)
    {
      de.printStackTrace();
    }
    catch (UnsupportedEncodingException e)
    {
      e.printStackTrace();
    }
    cursor.close();
    myDatabase.close();
    return result;
  }

  static public void putKeyValue(String dbName, String keyString, String valueString)
  {
    Cursor cursor = null;
    myDatabase = myDbEnvironment.openDatabase(null, dbName, dbConfig);
    cursor = myDatabase.openCursor(null, null);
    DatabaseEntry key;
    DatabaseEntry value;
    boolean result = false;
    try
    {
      key = new DatabaseEntry(keyString.getBytes("UTF-8"));
      value = new DatabaseEntry(valueString.getBytes("UTF-8"));
      cursor.put(key, value);
    }
    catch (DatabaseException de)
    {
      de.printStackTrace();
    }
    catch (UnsupportedEncodingException e)
    {
      e.printStackTrace();
    }
    cursor.close();
    myDatabase.close();
  }

  static public String getValue(String dbName, String keyString)
  {
    Cursor cursor = null;
    myDatabase = myDbEnvironment.openDatabase(null, dbName, dbConfig);
    cursor = myDatabase.openCursor(null, null);
    DatabaseEntry foundKey;
    DatabaseEntry foundValue;
    String result = "";
    try
    {
      foundKey = new DatabaseEntry(keyString.getBytes("UTF-8"));
      foundValue = new DatabaseEntry();
      if (cursor.getSearchKey(foundKey, foundValue, LockMode.DEFAULT) == OperationStatus.SUCCESS)
      {
        result = new String(foundValue.getData(), "UTF-8");
      }
      else
      {
        result = "";
      }
    }
    catch (DatabaseException de)
    {
      de.printStackTrace();
    }
    catch (UnsupportedEncodingException e)
    {
      e.printStackTrace();
    }
    cursor.close();
    myDatabase.close();
    return result;
  }

  static public void deleteKeyValue(String dbName, String keyString)
  {
    Cursor cursor = null;
    myDatabase = myDbEnvironment.openDatabase(null, dbName, dbConfig);
    cursor = myDatabase.openCursor(null, null);
    DatabaseEntry foundKey;
    DatabaseEntry foundValue;
    try
    {
      foundKey = new DatabaseEntry(keyString.getBytes("UTF-8"));
      foundValue = new DatabaseEntry();
      if (cursor.getSearchKey(foundKey, foundValue, LockMode.DEFAULT) == OperationStatus.SUCCESS)
      {
        cursor.delete();
      }
    }
    catch (DatabaseException de)
    {
      de.printStackTrace();
    }
    catch (UnsupportedEncodingException e)
    {
      e.printStackTrace();
    }
    cursor.close();
    myDatabase.close();
  }



  static public boolean keyListValidation(String dbName, String keyList, boolean addMsg)
  {
    String key;
    String [] keyArray = keyList.split(" ");
    int keyArrayLength = keyArray.length;
    boolean result = true;
    for (int i = 0; i < keyArrayLength; i++)
    {
      key = keyArray [i];
      if (!findKeyValue(dbName, key))
      {
        result = false;
        if (addMsg) addErrorMsg("Create table has failed: \u005c'" + key + "\u005c' does not exists in column definition");
        break;
      }
    }
    return result;
  }

  static public void setDefaultPkFk(String dbName)
  {
    if (findKeyValue(dbName, "@PK"))
    {
      ;
    }
    else
    {
      Cursor cursor = null;
      myDatabase = myDbEnvironment.openDatabase(null, dbName, dbConfig);
      cursor = myDatabase.openCursor(null, null);
      String totalKeyList = "";
      DatabaseEntry foundKey = new DatabaseEntry();
      DatabaseEntry foundValue = new DatabaseEntry();
      cursor.getFirst(foundKey, foundValue, LockMode.DEFAULT);
      do
      {
        try
        {
          String keyString = new String(foundKey.getData(), "UTF-8");
          String valueString = new String(foundValue.getData(), "UTF-8");
          if(!keyString.substring(0,1).equals("@"))
          {
            String[] valueArray = valueString.split(" ");
            valueArray[2] = "N";
            String newValueString = "";
            for(int j=0;j<valueArray.length;j++)
            {
              newValueString += (valueArray[j]+" ");
            }
            newValueString += "PRI";
            foundValue.setData(newValueString.getBytes());
            cursor.put(foundKey, foundValue);
            totalKeyList += (keyString + " ");
          }
        }
        catch (UnsupportedEncodingException e)
        {
          e.printStackTrace();
        }
      }
      while (cursor.getNext(foundKey, foundValue, LockMode.DEFAULT) == OperationStatus.SUCCESS);
      totalKeyList = totalKeyList.substring(0, totalKeyList.length() - 1);
      cursor.close();
      myDatabase.close();
      putKeyValue(dbName, "@PK", totalKeyList);
    }
    if(!findKeyValue(dbName,"@REFER")) putKeyValue(dbName, "@REFER", "");
    putKeyValue(dbName, "@REFERED", "");
  }

  static public boolean foreignKeyValidation(String dbName, String keyList)
  {
    boolean result = false;
    String primaryKey = getValue(dbName, "@PK");
    String [] primaryKeyArray = primaryKey.split(" ");
    String [] keyArray = keyList.split(" ");
    if (keyArray.length == primaryKeyArray.length)
    {
      Arrays.sort(keyArray);
      Arrays.sort(primaryKeyArray);
      if (Arrays.equals(keyArray, primaryKeyArray)) result = true;
    }
    return result;
  }

  static public boolean referenceDataTypeValidation(String dbName1, String keyList1, String dbName2, String keyList2)
  {
    boolean result = true;
    String [] keyArray1 = keyList1.split(" ");
    String [] keyArray2 = keyList2.split(" ");
    if (keyArray1.length == keyArray2.length)
    {
      for (int i = 0; i < keyArray1.length; i++)
      {
        String [] attrDataType1 = getValue(dbName1, keyArray1 [i]).split(" ");
        String [] attrDataType2 = getValue(dbName2, keyArray2 [i]).split(" ");
        boolean typeMatch = attrDataType1 [0].equals(attrDataType2 [0]);
        boolean sizeMatch = attrDataType1 [1].equals(attrDataType2 [1]);
        if (!(typeMatch && sizeMatch))
        {
          result = false;
          break;
        }
      }
    }
    else
    {
      result = false;
    }
    return result;
  }

  static public void addPrimaryKey(String dbName, String columnNameList)
  {
    String[] columnNameArray = columnNameList.split(" ");
    Arrays.sort(columnNameArray);

    for(int i=0;i<columnNameArray.length;i++)
    {
      String valueString = getValue(dbName, columnNameArray[i]);
      String[] valueArray = valueString.split(" ");
      valueArray[2] = "N";
      valueString="";
      for(int j=0;j<valueArray.length;j++)
      {
        valueString += (valueArray[j]+" ");
      }
      valueString += "PRI";
      putKeyValue(dbName, columnNameArray[i], valueString);
    }

    putKeyValue(dbName, "@PK", columnNameList);
  }

  static public void descTableList(String dbNameList)
  {
    if(dbNameList.equals("*"))
    {
      String totalTableList = "";
      Cursor cursor = null;
      myDatabase = myDbEnvironment.openDatabase(null, "@TABLELIST", dbConfig);
      cursor = myDatabase.openCursor(null, null);
      DatabaseEntry foundKey = new DatabaseEntry();
      DatabaseEntry foundValue = new DatabaseEntry();
      if (cursor.getFirst(foundKey, foundValue, LockMode.DEFAULT) == OperationStatus.SUCCESS)
      {
        do
        {
          try
          {
            String tableName = new String(foundKey.getData(), "UTF-8");
            totalTableList += (tableName + " ");
          }
          catch (DatabaseException de)
          {
            System.out.println(de.getMessage());
          }
          catch (UnsupportedEncodingException e)
          {
            System.out.println(e.getMessage());
          }
        }
        while (cursor.getNext(foundKey, foundValue, LockMode.DEFAULT) == OperationStatus.SUCCESS);
        cursor.close();
        myDatabase.close();
        String [] totalTableArray = totalTableList.split(" ");
        System.out.println("---------------------------------------------");
        for (int i = 0; i < totalTableArray.length; i++)
        {
          descTable(totalTableArray[i]);
        }
      }
      else
      {
        cursor.close();
        myDatabase.close();
      }
    }
    else
    {
      String [] dbNameArray = dbNameList.split(" ");
      boolean isError = false;
      for (int i = 0; i < dbNameArray.length; i++)
      {
        if (!findKeyValue("@TABLELIST", dbNameArray [i]))
        {
          isError = true;
          break;
        }
      }
      if (!isError)
      {
        System.out.println("---------------------------------------------");
        for (int i = 0; i < dbNameArray.length; i++)
        {
          descTable(dbNameArray [i]);
        }
      }
      else
      {
        System.out.println("No such table");
      }
    }
  }

  static public void descTable(String dbName)
  {
    Cursor cursor = null;
    myDatabase = myDbEnvironment.openDatabase(null, dbName, dbConfig);
    cursor = myDatabase.openCursor(null, null);
    DatabaseEntry foundKey = new DatabaseEntry();
    DatabaseEntry foundValue = new DatabaseEntry();
    cursor.getFirst(foundKey, foundValue, LockMode.DEFAULT);
    cursor.getNext(foundKey, foundValue, LockMode.DEFAULT);
    cursor.getNext(foundKey, foundValue, LockMode.DEFAULT);
    cursor.getNext(foundKey, foundValue, LockMode.DEFAULT);

    System.out.println("table_name ["+dbName+"]");
    System.out.println("column_name\u005cttype\u005ctnull\u005ctkey");

    do
    {
      try
      {
        String keyString = new String(foundKey.getData(), "UTF-8");
        String valueString = new String(foundValue.getData(), "UTF-8");
        String[] valueArray = valueString.split(" ");

        if(valueArray[0].equals("char"))
        {
          System.out.print(keyString+"\u005ctchar("+valueArray[1]+")\u005ct"+valueArray[2]+"\u005ct");
        }
        else
        {
          System.out.print(keyString+"\u005ct"+valueArray[0]+"\u005ct"+valueArray[2]+"\u005ct");
        }

        if(valueArray.length==3)
        {
          System.out.println("");
        }
        if(valueArray.length==4)
        {
          System.out.println(valueArray[3]);
        }

        if(valueArray.length==5)
        {
          System.out.println(valueArray[3]+"\u005ct"+valueArray[4]);
        }
      }
      catch (UnsupportedEncodingException e)
      {
        e.printStackTrace();
      }
    } while (cursor.getNext(foundKey, foundValue, LockMode.DEFAULT) == OperationStatus.SUCCESS);
    System.out.println("---------------------------------------------");

    cursor.close();
    myDatabase.close();
  }

  static public void addForeignKey(String referencingTable, String columnNameList, String referencedTable)
  {
    String currentRefered = getValue(referencedTable, "@REFERED");
    currentRefered += (referencingTable+" ");
    putKeyValue(referencedTable, "@REFERED", currentRefered);

    String currentRefer = getValue(referencingTable, "@REFER");
    currentRefer += (referencedTable+" ");
    putKeyValue(referencingTable, "@REFER", currentRefer);


    String[] columnNameArray = columnNameList.split(" ");
    for(int i=0;i<columnNameArray.length;i++)
    {
      String currentDataType = getValue(referencingTable, columnNameArray[i]);
      String updatedDataType = currentDataType + " FOR";
      putKeyValue(referencingTable, columnNameArray[i], updatedDataType);
    }
  }

  static public void removeReference(String referencedTable, String referencingTable)
  {
    String referingList = getValue(referencedTable, "@REFERED");
    String[] referingArray = referingList.split(" ");

    int i;
    for(i=0;i<referingArray.length;i++)
    {
      if(referingArray[i].equals(referencingTable)) break;
    }

    referingArray[i]="";
    String newReferingList = "";

    for(int j=0;j<referingArray.length;j++)
    {
      newReferingList += (referingArray[j]+" ");
    }
    newReferingList = newReferingList.substring(0, newReferingList.length()-1);

    putKeyValue(referencedTable, "@REFERED", newReferingList);
  }

  static public void dropTable(String dbName)
  {
    if (dbName.equals("*"))
    {

      String totalTableList = "";
      Cursor cursor = null;
      myDatabase = myDbEnvironment.openDatabase(null, "@TABLELIST", dbConfig);
      cursor = myDatabase.openCursor(null, null);
      DatabaseEntry foundKey = new DatabaseEntry();
      DatabaseEntry foundValue = new DatabaseEntry();

      if (cursor.getFirst(foundKey, foundValue, LockMode.DEFAULT) == OperationStatus.SUCCESS)
      {
        do
        {
          try
          {
            String tableName = new String(foundKey.getData(), "UTF-8");
            totalTableList += (tableName + " ");
          }
          catch (DatabaseException de)
          {
            System.out.println(de.getMessage());
          }
          catch (UnsupportedEncodingException e)
          {
            System.out.println(e.getMessage());
          }
        } while (cursor.getNext(foundKey, foundValue, LockMode.DEFAULT) == OperationStatus.SUCCESS);

        cursor.close();
        myDatabase.close();

        String [] totalTableArray = totalTableList.split(" ");
        for (int i = 0; i < totalTableArray.length; i++)
        {
          myDbEnvironment.removeDatabase(null, totalTableArray[i]);
        }
        myDbEnvironment.removeDatabase(null, "@TABLELIST");
      }
      else
      {
        cursor.close();
        myDatabase.close();
      }
      System.out.println("Every table is dropped");
    }
    else
    {
      String [] dbNameArray = dbName.split(" ");
      for (int i = 0; i < dbNameArray.length; i++)
      {
        if (findKeyValue("@TABLELIST", dbNameArray [i]))
        {
          if (!getValue(dbNameArray [i], "@REFERED").equals(""))
          {
            System.out.println(getValue(dbNameArray [i], "@REFERED")+"end");
            System.out.println("Drop table has failed: '" + dbNameArray [i] + "'is referenced by other table");
          }
          else
          {
            if(!getValue(dbNameArray[i], "@REFER").equals(""))
            {
              String referedList = getValue(dbNameArray[i], "@REFER");
              String[] referedArray = referedList.split(" ");
              for(int j=0;j<referedArray.length;j++)
              {
                removeReference(referedArray[j], dbNameArray[i]);
              }
            }

            myDbEnvironment.removeDatabase(null, dbNameArray [i]);
            deleteKeyValue("@TABLELIST", dbNameArray [i]);

            System.out.println("'" + dbNameArray [i] + "' table is dropped");
          }
        }
        else
        {
          System.out.println("No such table named '" + dbNameArray [i] + "'");
        }
      }
    }
  }

  static public void addErrorMsg(String msg)
  {
    if (errorMsg.equals(""))
    {
      errorMsg = msg;
    }
  }

  static final public void command() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case CREATE_TABLE:
    case DROP_TABLE:
    case DESC:
      queryList();
      break;
    case EXIT:
      jj_consume_token(EXIT);
      jj_consume_token(SEMICOLON);
      if (myDatabase != null) myDatabase.close();
      if (myDbEnvironment != null) myDbEnvironment.close();
      System.exit(0);
      break;
    default:
      jj_la1[0] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  static final public void queryList() throws ParseException {
  int q;
    label_1:
    while (true) {
      q = query();
      jj_consume_token(SEMICOLON);
      System.out.print("DB_2010-11858> ");
      printMessage(q);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CREATE_TABLE:
      case DROP_TABLE:
      case DESC:
        ;
        break;
      default:
        jj_la1[1] = jj_gen;
        break label_1;
      }
    }
  }

  static final public int query() throws ParseException {
  int q;
  currentTableName = "";
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case CREATE_TABLE:
      createTableQuery();
      q = PRINT_CREATE_TABLE;
      break;
    case DROP_TABLE:
      dropTableQuery();
      q = PRINT_DROP_TABLE;
      break;
    case DESC:
      descQuery();
      q = PRINT_DESC;
      break;
    default:
      jj_la1[2] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    {if (true) return q;}
    throw new Error("Missing return statement in function");
  }

  static final public void createTableQuery() throws ParseException {
  String tableName;
    jj_consume_token(CREATE_TABLE);
    tableName = tableName();
    errorMsg = "";
    //    currentTableName = "";
    if (findKeyValue("@TABLELIST", tableName))
    {
      addErrorMsg("Create table has failed: table with the same name already exists");
      currentTableName = "@TEMP";
    }
    else
    {
      currentTableName = tableName;
    }
    tableElementList(tableName);
    setDefaultPkFk(currentTableName);
  }

  static final public void tableElementList(String tableName) throws ParseException {
    jj_consume_token(LEFT_PAREN);
    tableElement(tableName);
    label_2:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case COMMA:
        ;
        break;
      default:
        jj_la1[3] = jj_gen;
        break label_2;
      }
      jj_consume_token(COMMA);
      tableElement(tableName);
    }
    jj_consume_token(RIGHT_PAREN);
  }

  static final public void tableElement(String tableName) throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case LEGAL_IDENTIFIER:
      columnDefinition(tableName);
      break;
    case PRIMARY_KEY:
    case FOREIGN_KEY:
      tableConstraintDefinition(tableName);
      break;
    default:
      jj_la1[4] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  static final public void columnDefinition(String tableName) throws ParseException {
  String columnName;
  String dataType;
  String databaseName;
  boolean result;
    columnName = columnName();
    dataType = dataType();
    dataType += " Y";
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case NOT_NULL:
      jj_consume_token(NOT_NULL);
      dataType = dataType.substring(0, dataType.length() - 1) + "N";
      break;
    default:
      jj_la1[5] = jj_gen;
      ;
    }
    if (findKeyValue(currentTableName, dataType))
    {
      addErrorMsg("Create table has failed: column definition is duplicated");
      myDbEnvironment.renameDatabase(null, currentTableName, "@TEMP");
      currentTableName = "@TEMP";
    }
    else
    {
      putKeyValue(currentTableName, columnName, dataType);
    }
  }

  static final public void tableConstraintDefinition(String tableName) throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case PRIMARY_KEY:
      primaryKeyConstraint(tableName);
      break;
    case FOREIGN_KEY:
      referentialConstraint(tableName);
      break;
    default:
      jj_la1[6] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  static final public void primaryKeyConstraint(String tableName) throws ParseException {
  String columnNameList;
  String databaseName;
    jj_consume_token(PRIMARY_KEY);
    columnNameList = columnNameList();
    if (findKeyValue(currentTableName, "@PK"))
    {
      addErrorMsg("Create table has failed: primary key definition is duplicated");
      myDbEnvironment.renameDatabase(null, currentTableName, "@TEMP");
      currentTableName = "@TEMP";
    }
    else
    {
      if (keyListValidation(currentTableName, columnNameList, true))
      {
        addPrimaryKey(currentTableName, columnNameList);
      }
      else
      {
        myDbEnvironment.renameDatabase(null, currentTableName, "@TEMP");
        currentTableName = "@TEMP";
      }
    }
  }

  static final public void referentialConstraint(String referencingTableName) throws ParseException {
  String referencingColumnNameList;
  String referencedTableName;
  String referencedColumnNameList;
  String databaseName;
    jj_consume_token(FOREIGN_KEY);
    referencingColumnNameList = columnNameList();
    if (!keyListValidation(currentTableName, referencingColumnNameList, true))
    {
      myDbEnvironment.renameDatabase(null, currentTableName, "@TEMP");
      currentTableName = "@TEMP";
    }
    jj_consume_token(REFERENCES);
    referencedTableName = tableName();
    if (!findKeyValue("@TABLELIST", referencedTableName))
    {
      myDbEnvironment.renameDatabase(null, currentTableName, "@TEMP");
      addErrorMsg("Create table has failed: foreign key references non existing table");
      currentTableName = "@TEMP";
    }
    referencedColumnNameList = columnNameList();
    if (!keyListValidation(referencedTableName, referencedColumnNameList, false))
    {
      addErrorMsg("Create table has failed: foreign key references non existing column");
      myDbEnvironment.renameDatabase(null, currentTableName, "@TEMP");
      currentTableName = "@TEMP";
    }
    else if (!foreignKeyValidation(referencedTableName, referencedColumnNameList))
    {
      addErrorMsg("Create table has failed: foreign key references non primary key column");
      myDbEnvironment.renameDatabase(null, currentTableName, "@TEMP");
      currentTableName = "@TEMP";
    }
    else if (!referenceDataTypeValidation(currentTableName, referencingColumnNameList, referencedTableName, referencedColumnNameList))
    {
      addErrorMsg("Create table has failed: foreign key references wrong type");
      myDbEnvironment.renameDatabase(null, currentTableName, "@TEMP");
      currentTableName = "@TEMP";
    }
    if (!currentTableName.equals("@TEMP"))
    {
      addForeignKey(currentTableName, referencingColumnNameList, referencedTableName);
    }
  }

  static final public String columnNameList() throws ParseException {
  String columnNameList;
  String columnName;
    jj_consume_token(LEFT_PAREN);
    columnNameList = columnName();
    label_3:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case COMMA:
        ;
        break;
      default:
        jj_la1[7] = jj_gen;
        break label_3;
      }
      jj_consume_token(COMMA);
      columnName = columnName();
      columnNameList += (" " + columnName);
    }
    jj_consume_token(RIGHT_PAREN);
    {if (true) return columnNameList;}
    throw new Error("Missing return statement in function");
  }

  static final public String dataType() throws ParseException {
  Token intValueToken;
  int intValue;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case INT:
      jj_consume_token(INT);
    {if (true) return "int 0";}
      break;
    case CHAR:
      jj_consume_token(CHAR);
      jj_consume_token(LEFT_PAREN);
      intValueToken = jj_consume_token(INT_VALUE);
      jj_consume_token(RIGHT_PAREN);
      intValue = Integer.parseInt(intValueToken.image);
      if (intValue < 1)
      {
        addErrorMsg("Char length should be > 0");
        myDbEnvironment.renameDatabase(null, currentTableName, "@TEMP");
        currentTableName = "@TEMP";
      }
      {if (true) return ("char " + intValue);}
      break;
    case DATE:
      jj_consume_token(DATE);
    {if (true) return "date 0";}
      break;
    default:
      jj_la1[8] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  static final public String tableNameList() throws ParseException {
  String tableNameList;
  String tableName;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case STAR:
      jj_consume_token(STAR);
    {if (true) return "*";}
      break;
    case LEGAL_IDENTIFIER:
      tableNameList = tableName();
      label_4:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case COMMA:
          ;
          break;
        default:
          jj_la1[9] = jj_gen;
          break label_4;
        }
        jj_consume_token(COMMA);
        tableName = tableName();
        tableNameList += (" " + tableName);
      }
    {if (true) return tableNameList;}
      break;
    default:
      jj_la1[10] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  static final public String tableName() throws ParseException {
  Token tableName;
    tableName = jj_consume_token(LEGAL_IDENTIFIER);
    {if (true) return tableName.image.toLowerCase();}
    throw new Error("Missing return statement in function");
  }

  static final public String columnName() throws ParseException {
  Token columnName;
    columnName = jj_consume_token(LEGAL_IDENTIFIER);
    {if (true) return columnName.image.toLowerCase();}
    throw new Error("Missing return statement in function");
  }

  static final public void dropTableQuery() throws ParseException {
  String tableNameList;
    jj_consume_token(DROP_TABLE);
    errorMsg = "";
    tableNameList = tableNameList();
    dropTable(tableNameList);
  }

  static final public void descQuery() throws ParseException {
  String tableNameList;
    jj_consume_token(DESC);
    tableNameList = tableNameList();
    descTableList(tableNameList);
  }

  static private boolean jj_initialized_once = false;
  /** Generated Token Manager. */
  static public SimpleDBMSParserTokenManager token_source;
  static SimpleCharStream jj_input_stream;
  /** Current token. */
  static public Token token;
  /** Next token. */
  static public Token jj_nt;
  static private int jj_ntk;
  static private int jj_gen;
  static final private int[] jj_la1 = new int[11];
  static private int[] jj_la1_0;
  static {
      jj_la1_init_0();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0xe20,0xe00,0xe00,0x80000,0x1006000,0x1000,0x6000,0x80000,0x1c0,0x80000,0x1400000,};
   }

  /** Constructor with InputStream. */
  public SimpleDBMSParser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public SimpleDBMSParser(java.io.InputStream stream, String encoding) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser.  ");
      System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new SimpleDBMSParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 11; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  static public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  static public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 11; i++) jj_la1[i] = -1;
  }

  /** Constructor. */
  public SimpleDBMSParser(java.io.Reader stream) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser. ");
      System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new SimpleDBMSParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 11; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  static public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 11; i++) jj_la1[i] = -1;
  }

  /** Constructor with generated Token Manager. */
  public SimpleDBMSParser(SimpleDBMSParserTokenManager tm) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser. ");
      System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 11; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(SimpleDBMSParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 11; i++) jj_la1[i] = -1;
  }

  static private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }


/** Get the next Token. */
  static final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  static final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  static private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  static private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  static private int[] jj_expentry;
  static private int jj_kind = -1;

  /** Generate ParseException. */
  static public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[26];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 11; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 26; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  static final public void enable_tracing() {
  }

  /** Disable tracing. */
  static final public void disable_tracing() {
  }

}
