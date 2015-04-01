package com.googlecode.scheme2ddl.db2;

import com.googlecode.scheme2ddl.db2.domain.GrantType;
import com.googlecode.scheme2ddl.db2.domain.RoutineAuth;
import com.googlecode.scheme2ddl.db2.domain.TabAuth;
import org.springframework.format.Printer;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * @author ar
 * @since Date: 01.04.2015
 */
@Component
public class RoutineAuthPrinter implements Printer<RoutineAuth> {

    /*
>>-GRANT EXECUTE ON--+-| function-designator |----------+------->
                     +-FUNCTION--+---------+--*---------+
                     |           '-schema.-'            |
                     +-| method-designator |------------+
                     +-METHOD * FOR--+-type-name------+-+
                     |               '-+---------+--*-' |
                     |                 '-schema.-'      |
                     +-| procedure-designator |---------+
                     '-PROCEDURE--+---------+--*--------'
                                  '-schema.-'

       .-,---------------------------------.
       V                                   |
>--TO----+-+-------+--authorization-name-+-+-------------------->
         | +-USER--+                     |
         | +-GROUP-+                     |
         | '-ROLE--'                     |
         '-PUBLIC------------------------'

>--+-------------------+---------------------------------------><
   '-WITH GRANT OPTION-'

function-designator

|--+-FUNCTION--function-name--+-------------------------+-+-----|
   |                          '-(--+---------------+--)-' |
   |                               | .-,---------. |      |
   |                               | V           | |      |
   |                               '---data-type-+-'      |
   '-SPECIFIC FUNCTION--specific-name---------------------'

method-designator

|--+-METHOD--method-name--+-------------------------+--FOR--type-name-+--|
   |                      '-(--+---------------+--)-'                 |
   |                           | .-,---------. |                      |
   |                           | V           | |                      |
   |                           '---data-type-+-'                      |
   '-SPECIFIC METHOD--specific-name-----------------------------------'

procedure-designator

|--+-PROCEDURE--procedure-name--+-------------------------+-+---|
   |                            '-(--+---------------+--)-' |
   |                                 | .-,---------. |      |
   |                                 | V           | |      |
   |                                 '---data-type-+-'      |
   '-SPECIFIC PROCEDURE--specific-name----------------------'

     */


    public static String template = "GRANT EXECUTE ON %s \"%s\".\"%s\" TO %s %s %s ;\n";


    public String print(RoutineAuth routineAuth, Locale locale) {

        StringBuffer sb = new StringBuffer();

        sb.append(String.format(template, routineAuth.getRoutineType().name(), routineAuth.getSchema(),
                routineAuth.getSpecificname(), routineAuth.getGranteeType().getSql(),
                routineAuth.getGrantee(), routineAuth.getExecuteAuth().getSql()));


        return sb.toString();
    }


}
