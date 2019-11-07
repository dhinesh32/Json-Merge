import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class FileHandler {
	
	
	//merge map contains key and value json pairs spread across different files
	HashMap<String,StringBuffer> mergeMap =new HashMap<>();
	
	//returns valid files in ascending order of their numbers
	
	public List<File> getValidFilesSorted(String srcPath,String inputPrefix)
	{
		 List<File> validFiles= getValidFiles(srcPath,inputPrefix);
		 Collections.sort(validFiles,new FileNameComparator(inputPrefix));
		 return validFiles;
	}
	
	//this function returns a list of valid files with .json extension also considers sub directory
	private List<File> getValidFiles(String srcPath,String inputPrefix)
	{

		List<File> validFiles;
		File folder = new File(srcPath);
		File[] listOfFiles = folder.listFiles();
		validFiles=new ArrayList<>();
		for (File file : listOfFiles) {
			if(file.isDirectory())
			{
				List<File> temp=getValidFiles(file.getPath(),inputPrefix);
				validFiles.addAll(temp);
			}
			
		    if (file.isFile()) {
		    	 	String name = file.getName();
		    	    int lastIndexOf = name.lastIndexOf(".");
		    	   
		    	    if(lastIndexOf != -1&&name.substring(lastIndexOf).equals(".json")&&name.indexOf(inputPrefix)==0)
		    	    {
		    		   	validFiles.add(file);
		    	    }
		    }
			}
	
		return validFiles;

	}
	

	
	//split the json spread across different files into key, value pairs
	
	public void parseFiles(List<File> validFiles)
	{
		
		for(File file:validFiles)
		{
			findKeyValue(file);
		}
		
	}
	
	
	//parses the file and identifies keys and values, which are put into mergeMap
	
	public void findKeyValue(File file)
	{
		 FileReader fr;
		 try {
			 fr = new FileReader(file);  
			    int i,flagKey=0,isOpen=0;
			    StringBuffer key =new StringBuffer();
			    StringBuffer value =new StringBuffer();
			    Stack<Character> stack=new Stack<>();
			    while ((i=fr.read()) != -1) 
			    {
			    	char c=(char)i;
			    	if(flagKey!=1&&(c=='"'||(c>=97&&c<=123)||(c>=65&&c<=90))&&(c!='{'))
			    	{
			    		key.append(c);
			    	}
			    	else if(flagKey!=1&&c==':')
			    	{
			    		flagKey=1;
			    	}
			    	else if(c==']')
			    	{
			    		stack.pop();
			    		if(stack.isEmpty())
			    		{
			    			String k=key.toString();
			    			if( mergeMap.containsKey(k))
			    			{
			    				mergeMap.put(k,mergeMap.get(k).append(","+value));
			    			}
			    			else
			    				mergeMap.put(k, value);
			    			key = null;value=null;
			    			flagKey=0;
			    			isOpen=0;
			    		}	
			    		 
			    	}
			    	else if(c=='['||isOpen==1)
			    	{
			    		if(c=='[')
			    		{
			    			stack.push(c);
			    		}
			    		else
			    			value.append(c);
			    	
			    		isOpen=1;
			    	}   
			    }
			    
		 } catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		 } 
		 catch (IOException e) {
			// TODO Auto-generated catch block
			 e.printStackTrace();
		 } 
		}
	
		public void splitFiles(String destPath,String outputPrefix,int maxSize )
		{
			maxSize=maxSize*1024;
			int count=1;
		
			for(String key:mergeMap.keySet())
			{
			
				StringBuffer value=mergeMap.get(key);
				String[] entries=value.toString().split("},");
			
				//header and footer are string added to tisOpen and bottom of file to make it fit to JSON syntax
			
				String header = "{"+key+":[";
				String footer = "]}";
			
				StringBuffer chunks=new StringBuffer(header);
				int i=0;
			
				//initially the file size is the no. of bytes occupied by header and footer
			
				int fileSize = entries[i].getBytes().length+header.getBytes().length+footer.getBytes().length;
				while(i<entries.length){
				
					while(fileSize<maxSize){

					chunks.append(entries[i]+"},");
					i++;
					if(i==entries.length)
					break;
				
				//file size is the total size of entries along with 4 additional bytes occupied by the characters }, along with newlines
				
					fileSize += entries[i].getBytes().length+4;
				
					}
					chunks.append(footer);
			
					File file=new File(destPath+"\\"+outputPrefix+count+".json");
					//write chunks to file
					try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
						// create file if not exists
						if (!file.exists()) {
							file.createNewFile();
						}
			
						writer.write(chunks.toString());
			  
					} catch (Exception e) {
						e.printStackTrace();
					}
					//set filesize = 0;
					fileSize = 0;
					count++;
					chunks=new StringBuffer("{"+key+":[");
				}
			
			
			
			}
		
		}

}

//sort the files based on their numeric order after prefix

class FileNameComparator implements Comparator<File>
{
		String prefix;
		FileNameComparator(String prefix)
		{
			this.prefix=prefix;
		}
		public int compare(File f1,File f2)
		{
		
			return f1.getName().substring(prefix.length(),f1.getName().length()-5).compareTo( f2.getName().substring(prefix.length(),f2.getName().length()-5));
		}
	

}

