package com.googlecode.scheme2ddl.db2.domain;

import org.springframework.stereotype.Component;

import java.util.Comparator;

/**
 * @author ar
 * @since Date: 29.11.2014
 */
@Component
public class Db2LookInfoComparator implements Comparator<Db2LookInfo> {
    public int compare(Db2LookInfo o1, Db2LookInfo o2) {
        return Long.compare(o1.getOpSequence(), o2.getOpSequence());
    }
}
