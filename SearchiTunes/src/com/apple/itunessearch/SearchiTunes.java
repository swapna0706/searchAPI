package com.apple.itunessearch;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class SearchiTunes 
{
public static List<String> countryList=Arrays.asList("CA","US");
public static List<String> mediaList=Arrays.asList("MOVIE","PODCAST","MUSIC","ALL");

public static final String baseUrl="https://itunes.apple.com/search?";

public static String fileName = "SearchApiTestData.xls";
public static String countrySheet = "Country";
public static String mediaSheet = "Media";
public static String testDataSheet = "Testdata";
public static Workbook wbook;
public static WritableWorkbook wwbCopy;
public static Sheet shSheet;
public static String testCaseID;


public static String getContent(String fileName, String testDataSheet, String Columnname, int Rowno) throws BiffException, IOException{
	
	Workbook wb = Workbook.getWorkbook(new File(fileName));
	String getContentFromXl = wb.getSheet(testDataSheet).getCell(wb.getSheet(testDataSheet).findCell(Columnname).getColumn(), Rowno).getContents();
	return getContentFromXl;
}

//To read the column values in XL using while loop

public static void run() throws BiffException, IOException, WriteException{
	int a=1;
	StringBuilder s=new StringBuilder();
	
	
	while(true){
	
		
	
	
    String testCaseNo = getContent(fileName, testDataSheet, "TestCaseNo", a);
	
	if(testCaseNo != null && testCaseNo.equalsIgnoreCase("end")) 
	{
		break;
	}	else{
		
	s.append(baseUrl);
	String term = getContent(fileName, testDataSheet, "Term", a);
	
	if(term==null)
	{
			
			System.out.println("term cannot be null");
			SetXLValues("testDataSheet", 5, 1, "Fail");
			break;
		
	}else{
		s.append("term=");
		s.append(term);
	}
			
	String country = getContent(fileName, testDataSheet, "Country", a);
	
if((country != null) && (countryList.contains(country)))
{
	s.append("&country=");
	s.append(country);
}
	 
	String media = 	getContent(fileName, testDataSheet, "Media", a);
	if((media!=null) && (mediaList.contains(media)))
	{
		s.append("&media=");
		s.append(media);
	}
	int limit = Integer.parseInt(getContent(fileName, testDataSheet, "Limit", a));	
	if(Integer.toString(limit)!=null)
	{
		s.append("&limit=");
		s.append(limit);
	}
	else
	{
		s.append("&limit=");
		s.append(65);	
	}
	
	String newUrl;
	newUrl=s.toString();
	System.out.println(newUrl);
	
	/*ProfilesIni ini = new ProfilesIni();
	FirefoxProfile fp = ini.getProfile("default");
	WebDriver driver=new FirefoxDriver(fp);*/ 
	
	System.setProperty("webdriver.chrome.driver" , "C:\\Users\\Swapna\\Downloads\\chromedriver_win32\\chromedriver.exe" );
	ChromeOptions option = new ChromeOptions();
	option.addArguments("--test-type");
	WebDriver driver = new ChromeDriver(option);
	
	driver.get(newUrl);	
	URL codeChrome = new URL(newUrl);
	try{
		HttpURLConnection http = (HttpURLConnection)codeChrome.openConnection();
		int status = http.getResponseCode();
		if(status>=400 || status>=500){
			SetXLValues("testDataSheet", 4, 1, "Fail");
			SetXLValues("testDataSheet", 5, 1, String.valueOf(status));
			
		} else
		 {			
		SetXLValues("testDataSheet", 5, 1, String.valueOf(status));
		SetXLValues("testDataSheet", 4, 1, "Pass");
		 }
		}catch (Exception e){
			e.printStackTrace();
			
		}
	
	SetXLValues("testDataSheet", 5, 1, "Pass");
	driver.quit();
	a++;
	s=new StringBuilder();
	
	}
		
	}
	
}

private static void SetXLValues(String sheetName, int columnNo, int rowNo, String xlData) throws BiffException, IOException
{
	Workbook wbook = Workbook.getWorkbook(new File(fileName));
	Sheet ws = wbook.getSheet(sheetName);
	Label le = new Label(columnNo, rowNo, xlData);
	try{
		((WritableSheet) ws).addCell(le);
		}
	catch (Exception e){
			
			
			
		}
	
}

public static void main(String[] args) throws BiffException, WriteException, IOException 
{
	
	run();
	exit();
    
}

private static void exit() {
	
	
	System.out.println("******Testcases Completed******");
	
}

}