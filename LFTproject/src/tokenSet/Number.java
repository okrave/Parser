/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tokenSet;

public class Number extends Token{
	public String lexeme = "";    
    public Number(int tag, String s) { 
    	super(tag); 
    	lexeme=s;
    }
    public String toString() { 
    	return "<" + tag + ", " + lexeme + ">"; 
    }
}