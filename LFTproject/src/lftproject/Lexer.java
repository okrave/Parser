/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lftproject;

import tokenSet.Word;
import tokenSet.Token;
import tokenSet.Number;
import java.io.*; 
import java.util.*;

public class Lexer {
    public int line = 1;
    private char peek = ' ';   
    Hashtable<String,Word> words = new Hashtable<String,Word>();
    Hashtable<String,Number> number = new Hashtable<String,Number>();
    
    void reserve(Word w) { 
    	words.put(w.lexeme, w); 
    }
    void reserve(Number n){
    	number.put(n.lexeme, n);
    }
    public Lexer() {
        /*Tutto ciò che non può essere un identificatore*/
    	reserve(new Number(Tag.NUM, "number"));
        reserve(new Word(Tag.ID, "id"));
        reserve(new Word(Tag.INTEGER, "integer"));
        reserve(new Word(Tag.BOOLEAN, "boolean"));
        reserve(new Word(Tag.TRUE, "true"));
        reserve(new Word(Tag.FALSE, "false"));
        reserve(new Word(Tag.PRINT, "print"));
        reserve(new Word(Tag.IF, "if"));
        reserve(new Word(Tag.THEN, "then"));
        reserve(new Word(Tag.ELSE, "else"));
        reserve(new Word(Tag.WHILE, "while"));
        reserve(new Word(Tag.DO, "do"));
        reserve(new Word(Tag.BEGIN, "begin"));
        reserve(new Word(Tag.END, "end"));
    } 
    private void readch(BufferedReader br) {
        try{
            peek = (char) br.read();
        }catch(IOException exc){
            peek = (char) -1;
        }
    }
    //  \t = tab | \n = a capo | \r = a capo
    public Token lexical_scan(BufferedReader br) {
        while(peek == ' ' || peek == '\t' || peek == '\n'  || peek == '\r'){            
            if(peek == '\n'){
            	line++;
        	}
            readch(br);
        }
        switch(peek){
            case ';':
            	peek = ' ';
            	return Token.semicolon;
            case ',':
            	peek = ' ';
                return Token.comma;
            case '(':
            	peek = ' ';
            	return Token.lpar;
            case ')':
            	peek = ' ';
            	return Token.rpar;
            case '+':
            	peek = ' ';
            	return Token.plus;
            case '-':
            	peek = ' ';
            	return Token.minus;
            case '*':
            	peek = ' ';
            	return Token.mult;
            case '/':
            	peek = ' ';
            	return Token.div;
            case '&':
                readch(br);
                if(peek == '&'){
                    peek = ' ';
                    return Word.and;
                }else{
                    System.err.println("Erroneous character" + " after '&' : "  + peek );
                    return null;
                }
            case '|':
            	readch(br);
            	 if(peek == '|'){
                     peek = ' ';
                     return Word.or;
                 }else{
                     System.err.println("Erroneous character" + " after '|' : "  + peek );
                     return null;
                 }
            case '=':
            	readch(br);
            	 if(peek == '='){
                     /*Non faccio il caso dell'uguale singolo
                       in quanto l'uguale singolo è composto da :=*/
                     peek = ' ';
                     return Word.eq; 
                 }else{
                     System.err.println("Erroneous character" + " after '=' : "  + peek );
                     return null;
                 }
            case '<':
            	readch(br);
            	 if(peek == '='){
                     peek = ' ';
                     return Word.le;
            	 }else if(peek == '>'){
            		 peek = ' ';
                     return Word.ne;
            	 }else if(Character.isDigit(peek) || Character.isLetter(peek) || peek == ' ' || peek == '_'){
            		 return Token.lt;
                 }else{
                     System.err.println("Erroneous character" + " after '<' : "  + peek );
                     return null;
                 }
            case '>':
            	readch(br);
            	 if(peek == '='){
                     peek = ' ';
                     return Word.ge;
            	 }else if(Character.isDigit(peek) || Character.isLetter(peek) || peek == ' ' || peek == '_'){
            		 return Token.gt;
                 }else{
                     System.err.println("Erroneous character" + " after '>' : "  + peek );
                     return null;
                 }
            case ':':
            	readch(br);
           	 	if(peek == '='){
                    peek = ' ';
                    return Word.assign;
           	 	}else{
           	 		System.err.println("Erroneous character" + " after ':' : "  + peek );
           	 		return null;
             }
            default:
            	if(peek == '_'){//caso ID che inizia con underscore
                /*Gestione caso in cui la stringa incomincia con underscore , la stringa può cominciare e finire con
                  underscore ma non deve essere composta solamente da essi("_x" -> OK | "____" -> NO)*/
            		boolean control = false;
            		String s = "";
            		s = s + peek;
            		readch(br);
            		if(Character.isDigit(peek) || Character.isLetter(peek) || peek == '_'){
            			do {
            				if((Character.isDigit(peek) || Character.isLetter(peek)) && control == false)                                
            					control = true;/*Esiste almeno una lettera*/
                            s = s + peek;
                            readch(br);
                        }while(Character.isDigit(peek) || Character.isLetter(peek) || peek == '_');
            			if(control == false){
            				System.err.println("Syntax Error: String ID can't be composed only by character '_'" );
                            return null;
            			}else if((Word)words.get(s) != null){/* ID è già presente nell'hashtable*/
                            return (Word)words.get(s);
                        }else{/*ID non è presente nell'hashtable*/
                        	Word w = new Word(Tag.ID,s);
                        	words.put(s,w);
                        	return w;
                        }
            		}else{
            			System.err.println("Erroneous character: " + peek );
                        return null;
            		}
        
            	}else if(Character.isLetter(peek)){//caso ID che inizia con lettera
                    String s = "";
                    do {
                        s = s + peek;
                        readch(br);
                    }while(Character.isDigit(peek) || Character.isLetter(peek) || peek == '_');
                    if((Word)words.get(s) != null){ 
                        /*Caso in cui il'ID è presente nell'hash*/
                        return (Word)words.get(s);
                    }else{
                        /*Caso in cui l'ID non è presente nell'hash*/
                    	Word w = new Word(Tag.ID,s);/*Creo l'identificatore associato alla stringa*/                        
                    	words.put(s,w);
                    	return w;
                    }
                 //DOMANDA: SE IL NUMERO È COMPOSTO DA 123N LO ACCETTERÒ LO STESSO MA SOLO 123
                }else if(Character.isDigit(peek)){//caso NUM
                    String s = "";
                    do {
                        s = s + peek;
                        readch(br);
                    }while(Character.isDigit(peek));
                    	if((Number)number.get(s) != null){ 
                    		return (Number)number.get(s);
                    	}else{
                    		Number w = new Number(Tag.NUM,s);
                    		number.put(s,w);
                    		return w;
                    	}
                }else{
                	if(peek == (char)-1){
                		return new Token(Tag.EOF);
                    }else{
                        System.err.println("Erroneous character: " + peek );
                        return null;
                    }
                }
         }
    }	
}

