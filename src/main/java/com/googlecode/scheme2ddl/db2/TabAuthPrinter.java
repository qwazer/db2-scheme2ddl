package com.googlecode.scheme2ddl.db2;

import com.googlecode.scheme2ddl.db2.domain.TabAuth;
import org.springframework.format.Formatter;
import org.springframework.format.Printer;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * @author ar
 * @since Date: 14.12.2014
 */
@Component
public class TabAuthPrinter implements Printer<TabAuth> {

    /*
                     .-PRIVILEGES-.
>>-GRANT--+-ALL--+------------+---------------------------+----->
          | .-,-----------------------------------------. |
          | V                                           | |
          '---+-ALTER---------------------------------+-+-'
              +-CONTROL-------------------------------+
              +-DELETE--------------------------------+
              +-INDEX---------------------------------+
              +-INSERT--------------------------------+
              +-REFERENCES--+-----------------------+-+
              |             |    .-,-----------.    | |
              |             |    V             |    | |
              |             '-(----column-name-+--)-' |
              +-SELECT--------------------------------+
              '-UPDATE--+-----------------------+-----'
                        |    .-,-----------.    |
                        |    V             |    |
                        '-(----column-name-+--)-'

       .-TABLE-.
>--ON--+-------+--+-table-name----+----------------------------->
                  |           (1) |
                  +-view-name-----+
                  '-nickname------'

       .-,---------------------------------.
       V                                   |
>--TO----+-+-------+--authorization-name-+-+-------------------->
         | +-USER--+                     |
         | +-GROUP-+                     |
         | '-ROLE--'                     |
         '-PUBLIC------------------------'

>--+-------------------+---------------------------------------><
   '-WITH GRANT OPTION-'

     */


    public static String template = "GRANT %s ON TABLE \"%s\".\"%s\" TO %s %s %s ;\n";


    public String print(TabAuth tabAuth, Locale locale) {

           //todo fix PUBLIC
        StringBuffer sb = new StringBuffer();

        if (!tabAuth.getAlterAuth().equals(TabAuth.GrantType.N)) {
            sb.append(String.format(template, "ALTER", tabAuth.getTabSchema(),
                    tabAuth.getTabName(), tabAuth.getGranteeType().getSql(),
                    tabAuth.getGrantee(), tabAuth.getAlterAuth().getSql()));
        }
        if (!tabAuth.getControlAuth().equals(TabAuth.GrantType.N)) {
            sb.append(String.format(template, "CONTROL", tabAuth.getTabSchema(),
                    tabAuth.getTabName(), tabAuth.getGranteeType().getSql(),
                    tabAuth.getGrantee(), tabAuth.getControlAuth().getSql()));
        }

        if (!tabAuth.getDeleteAuth().equals(TabAuth.GrantType.N)) {
            sb.append(String.format(template, "DELETE", tabAuth.getTabSchema(),
                    tabAuth.getTabName(), tabAuth.getGranteeType().getSql(),
                    tabAuth.getGrantee(), tabAuth.getDeleteAuth().getSql()));
        }
        if (!tabAuth.getIndexAuth().equals(TabAuth.GrantType.N)) {
            sb.append(String.format(template, "INDEX", tabAuth.getTabSchema(),
                    tabAuth.getTabName(), tabAuth.getGranteeType().getSql(),
                    tabAuth.getGrantee(), tabAuth.getIndexAuth().getSql()));
        }
        if (!tabAuth.getInsertAuth().equals(TabAuth.GrantType.N)) {
            sb.append(String.format(template, "INSERT", tabAuth.getTabSchema(),
                    tabAuth.getTabName(), tabAuth.getGranteeType().getSql(),
                    tabAuth.getGrantee(), tabAuth.getInsertAuth().getSql()));
        }
        if (!tabAuth.getRefAuth().equals(TabAuth.GrantType.N)) {
            sb.append(String.format(template, "REFERENCES", tabAuth.getTabSchema(),
                    tabAuth.getTabName(), tabAuth.getGranteeType().getSql(),
                    tabAuth.getGrantee(), tabAuth.getRefAuth().getSql()));
        }
        if (!tabAuth.getSelectAuth().equals(TabAuth.GrantType.N)) {
            sb.append(String.format(template, "SELECT", tabAuth.getTabSchema(),
                    tabAuth.getTabName(), tabAuth.getGranteeType().getSql(),
                    tabAuth.getGrantee(), tabAuth.getSelectAuth().getSql()));
        }
        if (!tabAuth.getUpdateAuth().equals(TabAuth.GrantType.N)) {
            sb.append(String.format(template, "UPDATE", tabAuth.getTabSchema(),
                    tabAuth.getTabName(), tabAuth.getGranteeType().getSql(),
                    tabAuth.getGrantee(), tabAuth.getUpdateAuth().getSql()));
        }


        return sb.toString();
    }


}
