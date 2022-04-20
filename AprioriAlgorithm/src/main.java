import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class main {

	public static void main(String[] args) throws FileNotFoundException {
		String fileName = "retail_dataset.txt";
	    int minSupp;
	    float minConf; 

	    Scanner scan= new Scanner(System.in);
	    System.out.print("Enter the minimum support count: ");
	    minSupp= scan.nextInt();
	    System.out.print("Enter the minimum confidence: ");
	    minConf= scan.nextFloat();
	    scan.close();
	    Map<String, Integer> item_sets1      = new ConcurrentHashMap<>();
	    Map<String, Integer> item_sets2      = new ConcurrentHashMap<>();
	    Map<String, Integer> item_sets3      = new ConcurrentHashMap<>();
	    Map<String, Integer> item_sets4      = new ConcurrentHashMap<>();
	    Map<String, Float  > item_Confidence = new ConcurrentHashMap<>();


	    

	    //Candidate 1
	    creatingFirstCandidate(fileName,item_sets1);
	    reduceItemset(item_sets1,minSupp);// Reducing first itemsets by removing sets with count less than minSupp
	    
	    //Candidate 2
	    itemsetCombinations(item_sets1,item_sets2); // Create candidate 2
	    settingCountOfItemSet(item_sets2,2,fileName);
	    reduceItemset(item_sets2, minSupp);
	    

	    
	    // Candidate 3
	    itemsetCombinationsWithoutRepetitionForCandidate3(item_sets2, item_sets3,3);

	    settingCountOfItemSet(item_sets3,3,fileName);

	    reduceItemset(item_sets3, minSupp);
	    if(item_sets3.size()==0) {//Show item_sets2
	    	System.out.println("The frequent item sets of candidate 2");
	    	showMap(item_sets2);
	    	// Calculate confidence of item_sets2
	    	
	    }else {
		    // Candidate 4
		    itemsetCombinationsWithoutRepetitionForCandidate4(item_sets3, item_sets4,4);
		    settingCountOfItemSet(item_sets4,4,fileName);
		    reduceItemset(item_sets4, minSupp);
		    if(item_sets4.size()==0) {//Show item_sets3
		    	System.out.println("The frequent item sets of candidate 3");
		    	showMap(item_sets3);
		    	calculateConfidence(item_sets1,item_sets2,item_sets3,item_Confidence,minConf);// Calculate confidence of item_sets3
		    	System.out.println("The association rules with their confidence");
		    	showAssociationRules(item_Confidence);
		    	
		    }else {//Show item_sets4
		    	System.out.println("The frequent item sets of candidate 4");
		    	showMap(item_sets4);
		    	// Calculate confidence of item_sets4
		    }
	    }
	    	    
	
	}
	/**
	 * 
	 * @param item_Confidence
	 * @param keysValues
	 * @param item_sets1
	 * @param item_sets2
	 * @param minConf
	 * Calculating all possible association rules and their confidence, and removing any association rule
	 * that its confidence is less than the minimum confidence
	 */
	public static void getOneRecordAssociationRule(Map<String, Float> item_Confidence,String [] keysValues,
			Map<String, Integer> item_sets1, Map<String, Integer> item_sets2, 
			Map<String, Integer> item_sets3,float minConf) {
		float firstValue=0,secondValue=0;
		float totalConf=0;
		boolean isKeyPresent=true;
		
		
		///////////////////////////////// 1 -> 2,3

		isKeyPresent = item_sets3.containsKey(keysValues[0]+","+keysValues[1]+","+keysValues[2]);
		if(isKeyPresent) {

			firstValue=item_sets3.get(keysValues[0]+","+keysValues[1]+","+keysValues[2]);
			secondValue=item_sets1.get(keysValues[0]);
			totalConf= (firstValue/secondValue);

			if(totalConf>=minConf) {
				item_Confidence.put(keysValues[0]+"->"+keysValues[1]+","+keysValues[2], totalConf);
			}
		}
		///////////////////////////////// 2 -> 1,3
		
		isKeyPresent = item_sets3.containsKey(keysValues[1]+","+keysValues[0]+","+keysValues[2]);
		if(isKeyPresent) {
			firstValue=item_sets3.get(keysValues[1]+","+keysValues[0]+","+keysValues[2]);
			secondValue=item_sets1.get(keysValues[1]);
			totalConf= (firstValue/secondValue);

			if(totalConf>=minConf) {
				item_Confidence.put(keysValues[1]+"->"+keysValues[0]+","+keysValues[2], totalConf);

			}
		}
		
		///////////////////////////////// 3 -> 1,2
		isKeyPresent = item_sets3.containsKey(keysValues[0]+","+keysValues[1]);
		if(isKeyPresent) {
			firstValue=item_sets3.get(keysValues[2]+","+keysValues[0]+","+keysValues[1]);
			secondValue=item_sets1.get(keysValues[2]);
			totalConf= (firstValue/secondValue);

			if(totalConf>=minConf) {
				item_Confidence.put(keysValues[2]+"->"+keysValues[0]+","+keysValues[1], totalConf);

			}
		}
		
		///////////////////////////////// 1,2 -> 3
		isKeyPresent = item_sets3.containsKey(keysValues[0]+","+keysValues[1]+","+keysValues[2]);
		if(isKeyPresent) {
			firstValue=item_sets3.get(keysValues[0]+","+keysValues[1]+","+keysValues[2]);
			secondValue=item_sets2.get(keysValues[0]+","+keysValues[1]);
			totalConf= (firstValue/secondValue);

			if(totalConf>=minConf) {
				item_Confidence.put(keysValues[0]+","+keysValues[1]+"->"+keysValues[2], totalConf);
			}
		}
		///////////////////////////////// 1,3 -> 2
		isKeyPresent = item_sets3.containsKey(keysValues[0]+","+keysValues[2]+","+keysValues[1]);
		if(isKeyPresent) {
			firstValue=item_sets3.get(keysValues[0]+","+keysValues[2]+","+keysValues[1]);
			secondValue=item_sets2.get(keysValues[0]+","+keysValues[2]);
			totalConf= (firstValue/secondValue);

			if(totalConf>=minConf) {
				item_Confidence.put(keysValues[0]+","+keysValues[2]+"->"+keysValues[1], totalConf);
			}
		}
		///////////////////////////////// 2,3 -> 1
		isKeyPresent = item_sets3.containsKey(keysValues[1]+","+keysValues[2]+","+keysValues[0]);
		if(isKeyPresent) {
			firstValue=item_sets3.get(keysValues[1]+","+keysValues[2]+","+keysValues[0]);

			secondValue=item_sets2.get(keysValues[1]+","+keysValues[2]);
			totalConf= (firstValue/secondValue);

			if(totalConf>=minConf) {
				item_Confidence.put(keysValues[1]+","+keysValues[2]+"->"+keysValues[0], totalConf);
			}
		}
	}
	/**
	 * 
	 * @param item_sets1
	 * @param item_sets2
	 * @param item_sets3
	 * @param minConf
	 * Calculating confidence by looping on candidate 3, generating all possible association rules and calculating
	 * their confidence by calling getOneRecordAssociationRule() method
	 * @return
	 */
	public static Map<String,Float> calculateConfidence(Map<String, Integer> item_sets1, 
			Map<String, Integer> item_sets2,Map<String, Integer> item_sets3,
			 Map<String, Float  > item_Confidence,float minConf) {
		for (Iterator<String> keys = item_sets3.keySet().iterator(); keys.hasNext();) {
	        String key = keys.next();
	        String [] keysValues = key.split(",");
	        getOneRecordAssociationRule(item_Confidence,keysValues,item_sets1,item_sets2,item_sets3,minConf);
	    }
		return item_Confidence;
	}

	/**
	 * 
	 * @param fileName
	 * @param item_sets
	 * @param totalCount
	 * @throws FileNotFoundException
	 * Creating the candidate 1 table
	 */
	public static void creatingFirstCandidate (String fileName,Map<String, Integer> item_sets) throws FileNotFoundException {
	    Scanner scanner = new Scanner(new File(fileName));
		String line;
	    while(scanner.hasNextLine()){//Create candidate 1
	    	line = scanner.nextLine();
	    	String[] str = line.split(",");
	    	for (int i=1 ; i<str.length ;++i ) {
		    	item_sets.merge(str[i], 1, Integer::sum);// Creating first itemsets
	    	}
	    }
	    scanner.close();
	}
	/**
	 * 
	 * @param item_sets
	 * @param candidate
	 * @param fileName
	 * @throws FileNotFoundException
	 * This function is responsible for taking a map with itemsets and empty values, then it should count the
	 * number of appearance of this itemset in the file
	 */
	public static void settingCountOfItemSet(Map<String, Integer> item_sets,int candidate,String fileName) throws FileNotFoundException {
		Scanner input = new Scanner(new File(fileName));
	    String record;
	    while(input.hasNextLine()){// Setting the count of item_sets2 values
	    	record = input.nextLine();
	    	String[] str = record.split(",");
		    for (Map.Entry<String, Integer> entry : item_sets.entrySet()) {
		    	String key = entry.getKey();
		    	String[] keys=key.split(",");
		    	int count=0;
		    	Integer val = entry.getValue();
		    	for(int i=0;i<keys.length;++i) {
		    		for(int j=0;j<str.length;++j) {
		    			if(keys[i].equals(str[j])) {
		    				count++;
		    				break;
		    			}
		    		}
		    	}
		    	if(count==candidate) {
	    			StringBuilder strBuilder = new StringBuilder();
		    		for(int i=0;i<keys.length-1;++i) {
						strBuilder.append(keys[i]);
						strBuilder.append(',');
		    		}
		    		strBuilder.append(keys[keys.length-1]);
					
					String temp = strBuilder.toString();
		    		item_sets.merge(temp, 1, Integer::sum);
		    	}
		    	
	    	}
	    }
	    input.close();

	}
	
	/**
	 * 
	 * @param item_sets
	 * @param minSupp
	 * Removing itemsets that have count less than the minimum support
	 */
	public static void reduceItemset(Map<String, Integer> item_sets, int minSupp) {

		for (Iterator<String> keys = item_sets.keySet().iterator(); keys.hasNext();) {
	        String key = keys.next();
	        Integer val = item_sets.get(key);
	        if(val<minSupp ) {
		        item_sets.remove(key);
	        }
	    }

	}
	/**
	 * 
	 * @param item_sets1
	 * @param item_sets2
	 * Creating item_sets2 items by making a combination using the item_sets1
	 */
	public static void itemsetCombinations(Map<String, Integer> item_sets1,Map<String, Integer> item_sets2) {
		ArrayList<String> itemsCombinations = new ArrayList<>();
		
		
		for (Iterator<String> keys = item_sets1.keySet().iterator(); keys.hasNext();) {
		        String key = keys.next();
		    	itemsCombinations.add(key);
		}
		for(int i=0;i<itemsCombinations.size()-1;++i) {
			for(int j=i+1;j<itemsCombinations.size();++j) {
			    	item_sets2.put(itemsCombinations.get(i)+","+itemsCombinations.get(j), 0);
				
		    	
			}
		}
	}
	/**
	 * 
	 * @param item_sets1
	 * @param item_sets2
	 * @param candidate
	 * Creating item_sets3 by making combination using item_sets2, then calling function removeDuplication
	 * to remove any duplication in item_sets3
	 */
	public static void itemsetCombinationsWithoutRepetitionForCandidate3(Map<String, Integer> item_sets1,
			Map<String, Integer> item_sets2,int candidate) {
		ArrayList<String> itemsCombinations = new ArrayList<>();
		

		for (Iterator<String> keys = item_sets1.keySet().iterator(); keys.hasNext();) {
			String key = keys.next();
	    	itemsCombinations.add(key);
		}
		for(int i=0;i<itemsCombinations.size()-1;++i) {
			String [] keys = itemsCombinations.get(i).split(",");
			for(int j=i+1;j<itemsCombinations.size();++j) {
				String[] keyValues =itemsCombinations.get(j).split(",");
				if(!keys[0].equals(keyValues[0]) && !keys[1].equals(keyValues[0])) {
					item_sets2.put(itemsCombinations.get(i)+","+keyValues[0], 0);
				}
				if(!keys[0].equals(keyValues[1]) && !keys[1].equals(keyValues[1])) {
					item_sets2.put(itemsCombinations.get(i)+","+keyValues[1], 0);
				}
		    	
			}
		}
	    removeDuplication(item_sets2,candidate);
	}
	/**
	 * 
	 * @param item_sets1
	 * @param item_sets2
	 * @param candidate
	 * Creating item_sets4 by making combination using item_sets3, then calling function removeDuplication
	 * to remove any duplication in item_sets4
	 */
	public static void itemsetCombinationsWithoutRepetitionForCandidate4(Map<String, Integer> item_sets1,
			Map<String, Integer> item_sets2,int candidate) {
		ArrayList<String> itemsCombinations = new ArrayList<>();
		
		for (Iterator<String> keys = item_sets1.keySet().iterator(); keys.hasNext();) {
			String key = keys.next();
	    	itemsCombinations.add(key);
		}
		for(int i=0;i<itemsCombinations.size()-1;++i) {
			String [] keys = itemsCombinations.get(i).split(",");
			for(int j=i+1;j<itemsCombinations.size();++j) {
				String[] keyValues =itemsCombinations.get(j).split(",");
				if(!keys[0].equals(keyValues[0]) && !keys[1].equals(keyValues[0])&& !keys[2].equals(keyValues[0])) {
					item_sets2.put(itemsCombinations.get(i)+","+keyValues[0], 0);
				}
				if(!keys[0].equals(keyValues[1]) && !keys[1].equals(keyValues[1]) && !keys[2].equals(keyValues[1])) {
					item_sets2.put(itemsCombinations.get(i)+","+keyValues[1], 0);
				}
				if(!keys[0].equals(keyValues[2]) && !keys[1].equals(keyValues[2]) && !keys[2].equals(keyValues[2])) {
					item_sets2.put(itemsCombinations.get(i)+","+keyValues[2], 0);
				}
		    	
			}
		}


	    removeDuplication(item_sets2,candidate);
	}
	
	/**
	 * 
	 * @param item_sets
	 * Removing duplications of itemsets by comparing each itemsets with the rest of the itemsets,
	 * if found any duplication, then eliminating one of them
	 */
	public static void removeDuplication(Map<String, Integer> item_sets, int candidate) {
		ArrayList<String> itemsCombinations = new ArrayList<>();

		for (Iterator<String> keys = item_sets.keySet().iterator(); keys.hasNext();) {
			String key = keys.next();
	    	itemsCombinations.add(key);			
	    	}
		for(int i=0;i<itemsCombinations.size()-1;++i) {		
			ArrayList<String> tempArr=null;
			for(int j=i+1;j<itemsCombinations.size();++j) {

				StringBuilder strBuilder = new StringBuilder();
				strBuilder.append(itemsCombinations.get(i));
				strBuilder.append(',');
				strBuilder.append(itemsCombinations.get(j));
				String temp = strBuilder.toString();
		        String[] tempStr = temp.split(",");
		        tempArr = new ArrayList<>(Arrays.asList(tempStr));
		        Set<String> set = new LinkedHashSet<>();
		        set.addAll(tempArr);
		        tempArr.clear();
		        tempArr.addAll(set);
		        if(tempArr.size()==candidate) {
					item_sets.remove(itemsCombinations.get(j));
		        }

			}
		}
	}
	/**
	 * 
	 * @param item_sets
	 * Printing map content
	 */
	public static void showMap(Map<String, Integer> item_sets) {
		for (Iterator<String> keys = item_sets.keySet().iterator(); keys.hasNext();) {
	        String key = keys.next();
	        Integer val = item_sets.get(key);
	        System.out.println(key+" = "+val);
	    }
	}
	/**
	 * 
	 * @param item_sets
	 * Printing all association rules with their confidence
	 */
	public static void showAssociationRules(Map<String, Float> item_sets) {
		for (Iterator<String> keys = item_sets.keySet().iterator(); keys.hasNext();) {
	        String key = keys.next();
	        Float val = item_sets.get(key);
	        System.out.println(key+" = "+val);
	    }
	}
	
}

