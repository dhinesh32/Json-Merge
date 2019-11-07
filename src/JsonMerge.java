import java.io.File;
import java.util.List;
import java.util.Scanner;




//merges json spread across different files

/*Additional Cases handled 
 * i) Nested array of objects
 * 		eg:
 * 			employee:[{name:emp1,phoneNos:[9988199,9988102]}]
 * 
 * ii) Multiple keys
 * 		eg:
 * 			striker:[{name:'a',age:1}],
 * 			employee:[{name:'emp1'}]
 *
 * 	where striker and employees are split into different files
 * */



public class JsonMerge {
	
	
	public static void main(String[] args){
		
		//getting all the necessary inputs
		Scanner cin=new Scanner(System.in);
		System.out.println("Enter your source folder  path");
		String srcPath=cin.next();
		System.out.println("Enter your destination folder  path");
		String destPath=cin.next();
		System.out.println("Enter input file base name");
		String inputPrefix=cin.next();
		System.out.println("Enter output  file base name");
		String outputPrefix=cin.next();
		System.out.println("Enter max merge file size in kilo bytes");
		int maxSize=cin.nextInt();
		
		FileHandler fileHandler=new FileHandler();
		
	
		List<File> validFiles=fileHandler.getValidFilesSorted(srcPath, inputPrefix);
		
		
		//iterate over valid files and split them into key, value pairs
		
		fileHandler.parseFiles(validFiles);
		
		
		//split the output key,value json pairs into chunks of files which does not cross maxSize

		fileHandler.splitFiles(destPath,outputPrefix,maxSize);
		System.out.println("done...");
		
		cin.close();
	} 

}
