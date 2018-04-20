import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
/**
 * A class that models a simple banking transaction system. 
 * It outputs prompts to standard output and takes user input from standard input. 
 * It reads transaction history from an associated log HTML file and updates that file. 
 * Users can choose to Deposit, Withdraw, Balance, or Exit.
 * It uses jsoup to parse html files quickly and easily, found through a quick google search
 * I used jsoup because it was a popular choice on stack overflow, its still beign worked on, 
 * is open source, has no dependencies, and has good documentation.
 * I opted to use a single Main class because there aren't any necessary class inheritances needed in this project
 * Also, most of my programming this semester has been in C, so I might be in a procedural mindset.
 * The control loop is a simple way to keep input available
 * I created the three handle helper methods to tidy up the flow of the main method
 * I could have made more static variables (the File, the Path, the error Strings in the helper methods)
 * But I like the self-enclosed nature of the helper methods. 
 * Is the pay off of using static strings (in the verify method worth the dependency on the class for verify?
 * @author Connor
 *
 */
public class Main {
	private static String prompt = "Please enter in a command (Deposit, Withdraw, Balance, Exit) :";
	private static String dPrompt = "Please enter an amount to desposit:";
	private static String wPrompt = "Please enter an amount to withdraw:";
	private static String formatString = "#.00#";
	private static String nError = "Error: Please enter a positive amount.";
	private static String fError = "Error: Please enter a number with no more than 2 decimal digits";
	private static String cError = "Error: Please enter a known command or D, W, B, or E";

	/**
	 * Verify the specified input with a given format. Somewhat counter-intuitively,
	 * it returns false if the input is valid, because when written it was used to answer the question
	 * "Do I need to keep asking for input?"
	 * <p>
	 * Prints the associated error cases for any input: negative numbers or more than three decimals
	 * A number such as 1.000 would still be valid because Java reports it as 1.0 instead of 1.000
	 * @param input		A Double representing a user's Withdrawal or Deposit
	 * @param formatter	A {@link DecimalFormat} that specifies the format the input must match
	 * @return 			false if the input is valid, true if the input is invalid
	 */
	private static Boolean verify(double input, DecimalFormat formatter) {
		boolean loop = false;
		if(input < 0) {
			System.out.println(nError);
			loop = true;
		}
		//formatting the input allows larger numbers to be parsed as strings with regards to a decimal point without being converted to scientific notation
		//Numbers with more than 2 decimal digits that are all 0 still pass this test.
		String inputStr = formatter.format(input);
		int decimalIndex = inputStr.indexOf('.');
		if(decimalIndex > 0) {
			int decimalLength = inputStr.length() - decimalIndex;
			//decimalLength should be 1 for decimal point + x for x decimal digits
			if(decimalLength > 3) {
				System.out.println(fError);
				loop = true;
			}
		}
		return loop;
	}

	//had these methods handle the scanner operations with the scanner defined as a static variable, changed because I thought it was causing extra scanner processing, 
	//but that was from using scanner.nextLine() instead of scanner.next()
	/*	private static void handleDeposit(Double dAmount, DecimalFormat formatter) {
		boolean lCont = true;
		while(lCont) {
			System.out.println(dPrompt);
			inputD = scannerino.nextDouble();
			scannerino.next();
			lCont = verify(wAmount, formatter);
		}
	}
	private static void handleWithdraw(Double wAmount, DecimalFormat formatter) {
		boolean lCont = true;
		while(lCont) {
			System.out.println(wPrompt);
			inputD = scannerino.nextDouble();
			scannerino.next();
			lCont = verify(wAmount, formatter);
		}
	}*/
	/**
	 * Writes a specified Double to the end of a transactions table in a specified input {@link File}
	 * Uses jsoup (See: jsoup.org) to parse the inputF and rewrite to the inputF
	 * 
	 * @param dAmount 	a positive double to be stored in the inputF as a Deposit
	 * @param inputF 	a File keeping track of all Bank transactions
	 */
	private static void handleDeposit(double dAmount, File inputF) {
		//create a new row in the table to hold the new transaction
		Document inputDoc = null;
		try {
			//using the file command in bash, it looks like the log file uses the us-ascii charset
			inputDoc = Jsoup.parse(inputF, "us-ascii");
			//Element transactionsTable = inputDoc.getElementById("transactions");

			//Document               transaction    rows       last       add row          add entry
			inputDoc.getElementById("transactions").children().last().appendElement("tr").appendElement("td").text(""+dAmount);

			//print all values in the transactions Table
			/*for(Element value : transactionsTable.children()) {
				System.out.println(value.text());
			}*/
		} catch(IOException e) {
			System.out.println("Couldn't parse the log file.");
		}

		//need a Path for a Buffered Writer to save the changes to the document
		Path logPath = FileSystems.getDefault().getPath("./log.html");
		Charset cs = Charset.forName("US-ASCII");
		try {
			BufferedWriter writer = Files.newBufferedWriter(logPath, cs);
			writer.write(inputDoc.outerHtml());
			writer.close();
		} catch (IOException e1) {
			System.out.println("Couldn't create buffered writer");
		}
	}

	/**
	 * Writes a specified Double to the end of a transactions table in a specified input {@link File}
	 * Uses jsoup (See: jsoup.org) to parse the inputF and rewrite to the inputF
	 * 
	 * @param wAmount 	a positive double to be stored in the inputF as a Withdrawal
	 * @param inputF 	a File keeping track of all Bank transactions
	 */
	private static void handleWithdraw(double wAmount, File inputF) {
		Document inputDoc = null;
		try {
			//using the file command in bash, it looks like the log file uses the us-ascii charset
			inputDoc = Jsoup.parse(inputF, "us-ascii");

			//don't need to access directly from the Document to update the Document, which is nice
			Element transactionsTable = inputDoc.getElementById("transactions");
			/*inputDoc.getElementById("transactions")*/transactionsTable.children().last().appendElement("tr").appendElement("td").text("-"+wAmount);

			//Print the entries in the transaction table
			/*for(Element value : transactionsTable.children()) {
				System.out.println(value.text());
			}*/
		} catch(IOException e) {
			System.out.println("Couldn't parse the log file.");
		}


		Path logPath = FileSystems.getDefault().getPath("./log.html");
		Charset cs = Charset.forName("US-ASCII");
		try {
			BufferedWriter writer = Files.newBufferedWriter(logPath, cs);
			writer.write(inputDoc.outerHtml());
			writer.close();
		} catch (IOException e1) {
			System.out.println("Couldn't create buffered writer");
		}
	}

	/**
	 * Sums all transactions of the specified input {@link File} and prints the sum to standard output
	 * Uses jsoup (See: jsoup.org) to parse the inputF and rewrite to the inputF
	 * 
	 * @param inputF	a File keeping track of all Bank transactions
	 * 
	 */
	private static void handleBalance(File inputF)  {
		Document inputDoc = null;
		try {
			//using the file command in bash, it looks like the log file uses the us-ascii charset
			inputDoc = Jsoup.parse(inputF, "us-ascii");
			Element transactionsTable = inputDoc.getElementById("transactions");
			//get the transaction amounts
			Elements tAmounts = transactionsTable.getElementsByTag("td");

			//arithmetic
			double balance = 0;
			for(Element value : tAmounts) {
				balance+=Double.parseDouble(value.text());
			}
			//output
			System.out.printf("The current balance is: $%.2f\n", balance);
		} catch(IOException e) {
			System.out.println("Couldn't parse the log file.");
		}
	}
	/**
	 * Prompts user for input until they choose to Exit
	 * 
	 * @param args	Standard main method protocol
	 */
	public static void main(String[] args) {

		boolean cont = true, invalid = true;
		String inputS;
		Double inputD = 0.0;
		Scanner scannerino = new Scanner(System.in);
		DecimalFormat formatterino = new DecimalFormat(formatString);


		//need a path for BufferedWriters
		//Unnecessary Path creation here, from when I made it a static variable for the helper methods
		//Can just create a file from a string directly
		Path logPath = FileSystems.getDefault().getPath("./log.html");
		//need a File for jsoup
		File inputF = logPath.toFile();
		if(!inputF.exists()) {
			System.out.println("Log file couldn't load");
			cont = false;
		}

		//command loop
		while(cont) {
			invalid = true;
			System.out.println(prompt);
			inputS = scannerino.next();
			if(inputS.equalsIgnoreCase("Deposit") || inputS.equalsIgnoreCase("D")) {
				while(invalid) {
					System.out.println(dPrompt);
					inputD = scannerino.nextDouble();
					invalid = verify(inputD, formatterino);
				}
				handleDeposit(inputD, inputF);
			}else if(inputS.equalsIgnoreCase("Withdraw") || inputS.equalsIgnoreCase("W")) {
				while(invalid) {
					System.out.println(wPrompt);
					inputD = scannerino.nextDouble();
					invalid = verify(inputD, formatterino);
				}
				handleWithdraw(inputD, inputF);
			}else if(inputS.equalsIgnoreCase("Balance") || inputS.equalsIgnoreCase("B")) {
				handleBalance(inputF);
			}else if(inputS.equalsIgnoreCase("Exit") || inputS.equalsIgnoreCase("E")) {
				cont = false;
			}else {
				System.out.println(cError);
			}
		}

		scannerino.close();
	}
}
