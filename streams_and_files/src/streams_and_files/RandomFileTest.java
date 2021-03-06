package streams_and_files;

import java.io.*;
import java.util.*;

/**
@version 1.11
*/

public class RandomFileTest
{
	public static void main(String[] args)
	{
		Emp[] staff = new Emp[3];
		
		staff[0] = new Emp("dante Cracker", 75000, 1987,12 ,15);
		staff[1] = new Emp("dicer hacker", 500000, 1988,14 ,1);
		staff[2] = new Emp("antonio Tester", 400000, 1993,4 ,15);
		
		try
		{
			//save all employee records to the file emp.dat
			DataOutputStream out = new DataOutputStream(new FileOutputStream("emp.dat"));
			for(Emp e : staff)
				e.writeData(out);
			out.close();
			
			// retrive all records into a new array
			RandomAccessFile in = new RandomAccessFile("emp.dat","r");
			// compue the array size
			int n = (int)(in.length() / Emp.RECORD_SIZE);
			Emp[] newStaff = new Emp[n];
			
			//read employee in reverse order
			for(int i = n-1; i > 0 ; i--)
			{
				newStaff[i] = new Emp();
				in.seek(i * Emp.RECORD_SIZE);
				newStaff[i].readData(in);
			}
			in.close();
			
			//print the newly read employee records
			for(Emp e : newStaff)
				System.out.println(e);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}

class Emp
{
	public Emp()
	{}
	
	public Emp(String n,double s,int year,int month,int date)
	{
		name = n;
		salary = s;
		GregorianCalendar calendar = new GregorianCalendar(year, month -1, date);
		hireDay = calendar.getTime();
	}
	
	public String getName()
	{
		return name;
	}
	
	public double getSalary()
	{
		return salary;
	}
	
	public Date getHireDay()
	{
		return hireDay;
	}
	
	public void raiseSalary(double byPercent)
	{
		double raise = salary * byPercent / 100;
		salary += raise;
	}
	
	public String toString()
	{
		return getClass().getName() + "[name=" + name + ",salary=" + salary + ",hireDay=" + hireDay + "]";
	}
	
	/**
	 * writes employee data to a data output
	 * @param out the data output
	 */
	public void writeData(DataOutput out) throws IOException
	{
		DataIO.writeFixedString(name, NAME_SIZE, out);
		out.writeDouble(salary);
		
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(hireDay);
		out.writeInt(calendar.get(Calendar.YEAR));
		out.writeInt(calendar.get(Calendar.MONTH) + 1);
		out.writeInt(calendar.get(Calendar.DAY_OF_MONTH));
	}
	/** 
	 * Reads employee data from data input
	 * @param in the data input
	 */
	public void readData(DataInput in) throws IOException
	{
		name = DataIO.readFixedString(NAME_SIZE, in);
		salary = in.readDouble();
		int y = in.readInt();
		int m = in.readInt();
		int d = in.readInt();
		GregorianCalendar calendar = new GregorianCalendar(y, m - 1, d);
		hireDay = calendar.getTime();
	}
	
	public static final int NAME_SIZE = 40;
	public static final int RECORD_SIZE = 2 * NAME_SIZE + 8 + 4 + 4 + 4;
	
	private String name;
	private double salary;
	private Date hireDay;
}

class DataIO
{
	public static String readFixedString(int size, DataInput in) throws IOException
	{
		StringBuilder b = new StringBuilder(size);
		int i = 0;
		boolean more = true;
		while (more && i < size)
		{
			char ch = in.readChar();
			i++;
			if(ch == 0) more = false;
			else b.append(ch);
		}
		in.skipBytes(2 * (size -1));
		return b.toString();
	}
	
	public static void writeFixedString(String s,int size,DataOutput out) throws IOException
	{
		for (int i = 0;i< size ; i++)
		{
			char ch =0;
			if(i<s.length()) ch = s.charAt(i);
			out.writeChar(ch);
		}
	}
}
