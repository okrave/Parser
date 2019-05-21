/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tokenSet;

import lftproject.Tag;

public class Word extends Token {
    public String lexeme = "";
    
    public Word(int tag, String s) { 
    	super(tag); 
    	lexeme=s;
    }
    
    public String toString() {
    	return "<" + tag + ", " + lexeme + ">";
    }
		// Word tutte quelle parole che non sono identificate da codice ASCI perchè composte
    public static final Word
    	and = new Word(Tag.AND, "&&"),
    	or = new Word(Tag.OR, "||"),			
    	eq = new Word(Tag.EQ, "=="),
    	le = new Word(Tag.LE, "<="),
    	ne = new Word(Tag.NE, "<>"),
    	ge = new Word(Tag.GE, ">="),
   		assign = new Word(Tag.ASSIGN, ":=");
    
}

