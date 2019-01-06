package io.wispershadow.stream.service.core.schema;

import java.util.Date;

public class ChildPojo extends ParentPojo {
    private boolean blackList;
    private Date transactionDate;

    public boolean isBlackList() {
        return blackList;
    }

    public void setBlackList(boolean blackList) {
        this.blackList = blackList;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }
}
