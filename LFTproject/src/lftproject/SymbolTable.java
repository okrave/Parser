package lftproject;
import java.util.*;

import java.util.*;
public class SymbolTable {
	Map <String, Type> TypeMap = new HashMap <String, Type>();
	Map <String, Integer> OffsetMap = new HashMap <String,Integer>();
	/*Associa ad ogni variabile un tipo di tipo intero poichè l'ijvm ricosce effettivamente solo gli interi*/
	public void insert(String s, Type t, int address){
		/*Se typemap non contiene la string s*/
		if(!TypeMap.containsKey(s)) 
			TypeMap.put(s,t);
		else 
			throw new IllegalArgumentException("Variabile gia' dichiarata.");
		if(!OffsetMap.containsValue(address)) 
			OffsetMap.put(s,address);
		else 
			throw new IllegalArgumentException("Riferimento ad una locazione di memoria gia' occupata da un�altra variabile." );
	}
	
	public Type lookupType(String s) {
		if(TypeMap.containsKey(s)) 
			return TypeMap.get(s);
		throw new IllegalArgumentException("Variabile sconosciuta ." + s );
	}
	
	public int lookupAddress(String s) {
		if(OffsetMap.containsKey(s)) 
			return OffsetMap.get(s);
		throw new IllegalArgumentException("Variabile sconosciuta.");
	}
}
