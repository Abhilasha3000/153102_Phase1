package com.capgemini.paytm.service;

import java.math.BigDecimal;

import com.capgemini.paytm.beans.Customer;
import com.capgemini.paytm.exception.InvalidInputException;

public interface WalletService {
	
	public Customer createAccount(String name ,String mobileno, BigDecimal amount);
	public Customer showBalance (String mobileno);
	public Customer fundTransfer (String sourceMobileNo,String targetMobileNo, BigDecimal amount);
	public Customer depositAmount (String mobileNo,BigDecimal amount );
	public Customer withdrawAmount(String mobileNo, BigDecimal amount);
	public boolean validate(String name,String phoneno,Customer cust) ;
}
