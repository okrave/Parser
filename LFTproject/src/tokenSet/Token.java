/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tokenSet;

/**
 *
 * @author Luca
 */
public class Token {
    public final int tag;
    
    public Token(int t) { 
    	tag = t;  }
    
    public String toString() {
    	return "<" + tag + ">";}
    
    public static final Token
    	comma = new Token(','),
    	semicolon = new Token(';'),
    	lpar = new Token('('),
    	rpar = new Token(')'),
    	plus = new Token('+'),
    	minus = new Token('-'),
    	mult = new Token('*'),
    	div = new Token('/'),
    	lt = new Token('<'),
    	gt = new Token('>');
          
} 

