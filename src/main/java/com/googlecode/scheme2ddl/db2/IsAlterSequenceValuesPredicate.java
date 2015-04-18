package com.googlecode.scheme2ddl.db2;

import com.googlecode.scheme2ddl.db2.domain.Db2LookInfo;
import org.apache.commons.collections.Predicate;
import org.springframework.stereotype.Component;

/**
 * Check that passed Db2LookInfo object is ALTER SEQUENCE statement ...
 * For example
 * <code>ALTER SEQUENCE "MY      "."S01" RESTART WITH 40 </code>
 * will rewturn true
 *
 * @author ar
 * @since Date: 05.04.2015
 */
@Component
public class IsAlterSequenceValuesPredicate implements Predicate {

    /**
     * Unless otherwise specified, all names can include the following characters:

     The letters A through Z, and a through z, as defined in the basic (7-bit) ASCII character set.
     When used in identifiers for objects created with SQL statements,
     lowercase characters "a" through "z" are converted to uppercase unless they are delimited with quotes (")
     0 through 9.
     ! % ( ) { } . - ^ ~ _ (underscore) @, #, $, and space.
     \ (backslash).

     */
    private static String allowedCharsInIdentifier = "([a-zA-Z0-9_!%~@#$\\-\\.\\^\\s])+";


    private static String pattern = "ALTER SEQUENCE \""+allowedCharsInIdentifier+"\"\\.\""+ allowedCharsInIdentifier + "\" RESTART WITH (\\d+)\\s*";

    public boolean evaluate(Object object) {
        if (!(object instanceof Db2LookInfo)){
            throw new IllegalArgumentException();

        }

        boolean result = false;

        result =
        ((Db2LookInfo) object).getObjType().equals("SEQUENCE")
                &&
        ((Db2LookInfo) object).getSqlOperation().equals("ALTER")
                &&
       ((Db2LookInfo) object).getSqlStmt().matches(pattern )
        ;
        return !result;
    }
}
