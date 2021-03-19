package com.neu.crm.workbench.dao;

import com.neu.crm.workbench.domain.Clue;
import com.neu.crm.workbench.domain.ClueActivityRelation;

import java.util.List;

public interface ClueDao {
    int save(Clue c);

    Clue detail(String id);


    Clue getById(String clueId);

    int delete(String clueId);
}
