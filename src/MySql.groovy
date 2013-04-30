

/**
 * Created with IntelliJ IDEA.
 * User: davec
 * Date: 13-04-29
 * Time: 2:12 PM
 * To change this template use File | Settings | File Templates.
 */
class MySql
{
  static String translateType(String dataType)
  {

    if ( dataType.endsWith('unsigned'))
    {
      if (dataType.startsWith('decimal')) return dataType.split(' ')[0]
      if (dataType.startsWith('int')) return 'int8'

    }
    if ( dataType.startsWith('enum')) return 'text'
    if ( dataType == 'double') return 'float8'
    if ( dataType == 'datetime') return 'timestamp'
    if ( dataType.endsWith('text') ) return 'text'
    if ( dataType.endsWith('blob') ) return 'bytea'
    if ( dataType.startsWith('tinyint') || dataType.startsWith('smallint') ) return 'int2'
    if ( dataType.startsWith('int')   ) return 'int4'
    if ( dataType.startsWith('bigint') ) return 'int8'
    return dataType
  }
  static String createFDW = "create server mysql_svr foreign data wrapper mysql_fdw options (address '10.151.83.123', port '3306');"
  static String createUserMapping = "create user mapping for public server mysql_svr options (username 'orangehrm', password 'duckerli');"

  static public void main(String []args)
  {

    if ( args.length != 4 )
    {
      println """usage groovy Mysql.groovy <input> <create output> <drop output> <database>
                Where input is the xml output of mysqldump -d -X database
                create output is the filename of the create statements
                drop output is the filename of the drop statements
                database is the foreign database in the mysql server
              """

      System.exit(-1)
    }

    File createSql = new File(args[1])
    File dropSql = new File (args[2])
    createSql.setText('')
    dropSql.setText('')
    String database = args[3]

    def mysqlDump = new XmlSlurper().parse(new File(args[0]))

    def createTable
    println mysqlDump.database.table_structure.each { table ->


      createTable = "create foreign table if not exists ${table.@name} ("
      dropSql << "drop foreign table if exists ${table.@name};\n"
      def numFields = table.field.size()
      table.field.eachWithIndex { field,i ->

        createTable += "${field.@Field} ${translateType(field.@Type.text())}"
        if ( i +1 < numFields ) createTable += ", "

      }
      createTable += ") server mysql_svr options (table '${table.@name}', database '${database}' );\n"
      createSql << createTable

    }
  }
}
