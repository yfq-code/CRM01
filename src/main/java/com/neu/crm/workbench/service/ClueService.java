package com.neu.crm.workbench.service;

import com.neu.crm.workbench.domain.Clue;
import com.neu.crm.workbench.domain.Tran;

public interface ClueService {

    boolean save(Clue c);

    Clue detail(String id);

    boolean unbund(String id);

    boolean bund(String cid, String[] aids);

    boolean convert(String clueId, Tran t, String createBy);
}
