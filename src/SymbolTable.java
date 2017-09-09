package cop5556sp17;



import java.lang.reflect.Array;
import java.util.*;

import cop5556sp17.AST.Dec;


public class SymbolTable {
	
	int currentScopeValue,nextScopeValue;
	Stack<Integer> stackedScope ;
	HashMap<String,List<Object>> symbolTableMap;
	
	
	//TODO  add fields

	/** 
	 * to be called when block entered
	 */
	public void enterScope(){
		//TODO:  IMPLEMENT THIS
		currentScopeValue = nextScopeValue++;
		stackedScope.push(currentScopeValue);
	}
	
	/**
	 * leaves scope
	 * @throws Exception 
	 */
	public void leaveScope(){
		//TODO:  IMPLEMENT THIS
		currentScopeValue = 0;
		stackedScope.pop();
		if(!stackedScope.isEmpty()){
			currentScopeValue = stackedScope.peek();
		}
	
	}
	
	public boolean insert(String ident, Dec dec){
		
		List<Object> values = new ArrayList<>();
		if (symbolTableMap.containsKey(ident)){
			values = symbolTableMap.get(ident);
			for (Object obj: values){
				List<Object> objAttributes = (List<Object>) obj;
				int objectScope = (Integer) objAttributes.get(1);
				if (objectScope == currentScopeValue)
					return false;
			}
		}
			List<Object> attributes = new ArrayList<>();
			attributes.add(dec);
			attributes.add(currentScopeValue);
			attributes.toString();
			if(values == null)
				values = new ArrayList<>();
			values.add(0, attributes);
			symbolTableMap.put(ident, values);
			return true;
	}
	
	public Dec lookup(String ident){

		Dec result = null;
		if(symbolTableMap.containsKey(ident)){
			List<Object> value = symbolTableMap.get(ident);
			for(Object obj : value){
				List<Object> objAttributes = (List<Object>) obj;
				Dec dec = (Dec) objAttributes.get(0);
				int objectScope = (Integer) objAttributes.get(1);
				if(stackedScope.contains(objectScope)){
					result = dec;
					break;
				}
			}	
		}
		return result;
	}
		
	public SymbolTable() {
		currentScopeValue = 0;
		nextScopeValue = 0;
		stackedScope = new Stack<Integer>();
		symbolTableMap = new HashMap<String,List<Object>>();
	}


	@Override
	public String toString() {
		return ("Hashmap :: " + symbolTableMap.toString() + "\nScope Stack ::" + stackedScope.toString());
	}
	
	


}
