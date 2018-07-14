package com.capgemini.paytm.service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.capgemini.paytm.beans.Customer;
import com.capgemini.paytm.beans.Wallet;
import com.capgemini.paytm.exception.InsufficientBalanceException;
import com.capgemini.paytm.exception.InvalidInputException;
import com.capgemini.paytm.repo.WalletRepo;
import com.capgemini.paytm.repo.WalletRepoImpl;

public class WalletServiceImpl implements WalletService {


public WalletRepo repo;
WalletRepoImpl obj=new WalletRepoImpl();
	public WalletServiceImpl(){
		repo= new WalletRepoImpl();
	}
	public WalletServiceImpl(WalletRepo repo) {
		super();
		this.repo = repo;
	}
	
	public WalletServiceImpl(Map<String, Customer> data) {		
		repo= new WalletRepoImpl(data);
		// TODO Auto-generated constructor stub
	}

	
	public Customer createAccount(String name, String mobileNo, BigDecimal amount) {
		//create object of customer, and call dao save layer
		Customer cust=new Customer();
		
		 validate( name, mobileNo,cust);
		 validate(amount,cust);			//amount should be positive
		 
		boolean result=repo.save(cust);
		if(result==true)
			return cust;
		else
			return null;
				
		}

	public Customer showBalance(String mobileNo) {
		
		Customer customer=repo.findOne(mobileNo);		
		if(customer!=null)
			return customer;
		else
			throw new InvalidInputException("Invalid mobile no ");
	}

	public Customer fundTransfer(String sourceMobileNo, String targetMobileNo, BigDecimal amount) {	
		amount= validate(amount);
		Customer scust=new Customer();
		Customer tcust=new Customer();
		Wallet sw=new Wallet();
		Wallet tw=new Wallet();
		scust=repo.findOne(sourceMobileNo);
		tcust=repo.findOne(targetMobileNo);
		if(sourceMobileNo.equals(targetMobileNo))
		{
			throw new InvalidInputException("Source and target mobile numbers cannot be same");
		}
		else
		if(scust!=null && tcust!=null )
		{	
			if(scust.getWallet().getBalance().compareTo(amount)==1)
			{
			BigDecimal amtSub=scust.getWallet().getBalance();
			BigDecimal diff=amtSub.subtract(amount);
			sw.setBalance(diff);
			scust.setWallet(sw);
			
			BigDecimal amtAdd=tcust.getWallet().getBalance();
			BigDecimal sum=amtAdd.add(amount);			
			tw.setBalance(sum);
			tcust.setWallet(tw);
			
			obj.getData().put(targetMobileNo, tcust);
			obj.getData().put(sourceMobileNo, scust);
			}
			else
				throw new InsufficientBalanceException("Amount is more than available balance");
		}
		else
		{
			throw new InvalidInputException("Account does not exists");
		}		
		return tcust;
	}

	public Customer depositAmount(String mobileNo, BigDecimal amount) {
		amount= validate(amount);
		Customer cust=new Customer();
		Wallet wallet=new Wallet();
		cust=repo.findOne(mobileNo);
		if(cust!=null)
		{
			
			BigDecimal amtAdd=cust.getWallet().getBalance().add(amount);
			wallet.setBalance(amtAdd);
			cust.setWallet(wallet);
			obj.getData().put(mobileNo, cust);
			
		}
		else
			throw new InvalidInputException("Account does not exists");
		return cust;
	}

	public Customer withdrawAmount(String mobileNo, BigDecimal amount) {
		 amount=validate(amount);
		Customer cust=new Customer();
		Wallet wallet=new Wallet();
		cust=repo.findOne(mobileNo);
		if(cust!=null)
		{
			if(cust.getWallet().getBalance().compareTo(amount)==1)
			{
			BigDecimal amtSub=cust.getWallet().getBalance().subtract(amount);
			wallet.setBalance(amtSub);
			cust.setWallet(wallet);
			obj.getData().put(mobileNo, cust);
			}
			else
				throw new InsufficientBalanceException("Sorry cannot withdraw,amount to be withdrawn is more than available balance");
		}
		else
			throw new InvalidInputException("Account does not exists");
			
		return cust;
	}
	
	public boolean validate(String name,String phoneno,Customer cust)  {
		Scanner sc=new Scanner(System.in);
		while(true)
		{Pattern pa=Pattern.compile("[a-zA-Z]+\\.?");
		Matcher ma=pa.matcher(name);
		if(ma.matches())
		{
			break;
		}
		else
		{
			System.err.println("Enter valid name: ");
			name=sc.next();
		}
			
		}
		
		 //check if phone no is valid
		while(true)
		{Pattern p=Pattern.compile("(0/91)?[7-9][0-9]{9}");
		Matcher m=p.matcher(phoneno);
		if(m.matches())
		{
			break;
		}
		else
		{
			System.err.println("Enter valid 10 digit phone no: ");
			phoneno=sc.next();
		}
			
		}
		cust.setMobileNo(phoneno);
		cust.setName(name);
		return true;
	}
	public boolean validate(BigDecimal amount,Customer cust)
	{
		Scanner sc=new Scanner(System.in);
		while(true)
		{
			if(Math.abs(amount.floatValue())==amount.floatValue())
			{
				break;
			}
			else
			{
				System.err.println("Enter positive amount: ");
				amount=sc.nextBigDecimal();
			}
				
		}
		cust.setWallet(new Wallet(amount));
		
		return true;
		
	}
	public BigDecimal validate(BigDecimal amount)
	{
		Scanner sc=new Scanner(System.in);
		while(true)
		{
			if(Math.abs(amount.floatValue())==amount.floatValue())
			{
				break;
			}
			else
			{
				System.err.println("Enter positive amount: ");
				amount=sc.nextBigDecimal();
			}
				
		}
		
		return amount;
		
	}
	
	
}
