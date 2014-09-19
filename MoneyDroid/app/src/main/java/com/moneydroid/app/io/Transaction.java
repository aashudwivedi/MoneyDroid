package com.moneydroid.app.io;

import java.util.List;

/**
 * Created by ashu on 6/4/14.
 */
public class Transaction {
    public int id;
    public String desc;
    public float amount;
    public String currency;
    public String paidBy;
    public List<Split> splits;
}
