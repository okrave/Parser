/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lftproject;

/**
 *
 * @author Luca
 */
import tokenSet.Word;
import tokenSet.Token;
import tokenSet.Number;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

/* 
 * gestione dell'errore tramite insieme guida(sostituire ad epsilon gli elementi dell'insieme guida
 * 
 */
public class Valutatore{
	private Lexer lex;
	private BufferedReader pbr;
	private Token look;
	private CodeGenerator code;
	private SymbolTable symb;
	
	private int counter = 0;
	
	public Valutatore(Lexer le, BufferedReader br, CodeGenerator cg, SymbolTable st) {
		lex = le;
		pbr = br;
		code = cg;
		symb = st;
		/*DOMANDA:Cosa significa move()*/
		move();
	}
	void match(int t) {
		if (look.tag == t) {
			if (look.tag != Tag.EOF)
				move();
		}else 
			error("<Syntax Error>");
	}
	void error(String s) {
		throw new Error("near line " + lex.line + ": " + s);
	}
	void move(){
		/*legge il token grazie all'oggetto lex*/
		look = lex.lexical_scan(pbr);
		System.out.println("Token = " + look);
	}
	public void start() {
		dec_list();
		stat();
		match(Tag.EOF);
	}
	private void dec_list(){
		switch(look.tag){
			case Tag.INTEGER:
				dec();
				match(';');//Se c'è un ";" allora vai avanti a leggere il prossimo tag
				dec_list();//richiama per vedere quale altro tag c'è
				break;
			case Tag.BOOLEAN:
				dec();
				match(';');
				dec_list();
				break;
			//Transizione silente
			case Tag.ID:
				break;
			case Tag.PRINT:
				break;
			case Tag.BEGIN:
				break;
			case Tag.WHILE:
				break;
			case Tag.IF:
				break;
			default:
				error("Erroneous Character in method 'dec_list_p()'");
				break;
		}
	}
	private void dec(){
		/*type = INTEGER || BOOLEAN*/
		Type type_ret = type();
		if(look.tag == Tag.ID){
			String s = ((Word)look).lexeme;
			symb.insert(s, type_ret, counter);
			counter++;
			move();
		}else{
			error("Syntax Error in method: 'dec()'");
		}
		id_list(type_ret);
	}
	private void id_list(Type var){
		switch(look.tag){
			case ',':
				match(',');
				if(look.tag == Tag.ID){
					String s = ((Word)look).lexeme;
					/*VEDERE symbletable*/
					symb.insert(s, var, counter);
					counter++;
					move();
				}else{
					error("Syntax Error in method: 'id_list()'");
				}
				id_list(var);
				break;
			case ';':
				break;
			default:
				error("Erroneous Character in method 'id_list_p()'");
				break;
		}
	}
	private Type type(){
		Type ret;
		switch(look.tag){
			case Tag.INTEGER:
				match(Tag.INTEGER);
				ret = Type.valueOf("INTEGER");
				return ret;
			case Tag.BOOLEAN:
				match(Tag.BOOLEAN);
				ret = Type.valueOf("BOOLEAN");
				return ret;
			default:
				error("Syntax Error in method: 'type()'");
				return null;
		}
	}
	private void stat(){
		Type exp_ret;
		int ltrue, lnext;
		switch(look.tag){
			case Tag.PRINT:
				match(Tag.PRINT);
				match('(');
				exp_ret = exp();
				if(exp_ret == Type.valueOf("INTEGER")){
					code.emit(OpCode.invokestatic, 1);// 1 INTEGER
				}else{
					code.emit(OpCode.invokestatic, 0);
				}
				match(')');
				break;
			case Tag.BEGIN:
				match(Tag.BEGIN);
				stat_list();
				match(Tag.END);
				break;
			case Tag.WHILE:
				match(Tag.WHILE);
				ltrue = code.newLabel();
				lnext = code.newLabel();
				code.emitLabel(ltrue);
				exp_ret = exp();
				code.emit(OpCode.ldc, 0);
				code.emit(OpCode.if_icmpeq,lnext);
				match(Tag.DO);
				stat();
				code.emit(OpCode.GOto,ltrue);
				code.emitLabel(lnext);
				break;
			case Tag.IF:
				match(Tag.IF);
				ltrue = code.newLabel();
				lnext = code.newLabel();
				exp_ret = exp();
				code.emit(OpCode.ldc, 0);
				code.emit(OpCode.if_icmpeq,ltrue);
				match(Tag.THEN);
				stat();
				if(look.tag == Tag.ELSE){//'if-then-else'
					move();
					code.emit(OpCode.GOto,lnext);
					code.emitLabel(ltrue);
					stat();
					code.emitLabel(lnext);
				}else{//'if-then'
					code.emitLabel(ltrue);
				}
				break;
			case Tag.ID:
				String s = "";
				if(look.tag == Tag.ID){
					s = ((Word)look).lexeme;
					move();
				}else{
					error("Syntax Error in method: 'stat()'");
				}
				match(Tag.ASSIGN);
				int x = symb.OffsetMap.get(s);
				exp_ret = exp();
				code.emit(OpCode.istore, x);
				break;
			default:
				error("Syntax Error in method: 'stat()'");
				break;
		}
	}
	private Type exp(){
		Type orE_ret;
		orE_ret = orE();
		return orE_ret;
	}
	private void stat_list(){
		stat();
		stat_list_p();
	}
	private void stat_list_p(){
		switch(look.tag){
			case ';':
				match(';');
				stat();
				stat_list_p();
				break;
			case Tag.END:
				break;
			default:
				error("Erroneous Character in method 'stat_list_p()'");
				break;
		}
	}
	private Type orE(){
		Type andE_ret, orE_p_ret;
		andE_ret = andE();
		orE_p_ret = orE_p(andE_ret);
		return orE_p_ret;
	}
	private Type orE_p(Type val){
		Type andE_ret, orE_p_ret = val;
		int ltrue, lnext;
		switch(look.tag){
			case Tag.OR:
				match(Tag.OR);
				if(val == Type.valueOf("BOOLEAN")){//type check 1
					ltrue = code.newLabel();
					lnext = code.newLabel();
					code.emit(OpCode.ldc ,1);
					code.emit(OpCode.if_icmpeq, ltrue);
					andE_ret = andE();
					if(andE_ret == Type.valueOf("BOOLEAN")){//type check 2
						code.emit(OpCode.GOto,lnext);
						code.emitLabel (ltrue);
						code.emit(OpCode.ldc,1);
						code.emitLabel(lnext);
						orE_p_ret = orE_p(andE_ret);
						return orE_p_ret;
					}else{
						error("Type Error in method: 'orE_p()'");
						return null;
					}
				}else{
					error("Type Error in method: 'orE_p()'");
					return null;
				}
			case ')':
				return orE_p_ret;
			case ';':
				return orE_p_ret;
			case Tag.THEN:
				return orE_p_ret;
			case Tag.DO:
				return orE_p_ret;
			case Tag.ELSE:
				return orE_p_ret;
			case Tag.END:
				return orE_p_ret;
			case Tag.EOF:
				return orE_p_ret;
			default:
				error("Erroneous Character in method 'orE_p()'");
				return null;
		}
	}
	private Type andE(){
		Type relE_ret, andE_p_ret;
		relE_ret = relE();
		andE_p_ret = andE_p(relE_ret);
		return andE_p_ret;
	}
	private Type andE_p(Type val){
		Type relE_ret, andE_p_ret = val;
		int ltrue, lnext;
		switch(look.tag){
			case Tag.AND:
				match(Tag.AND);
				if(val == Type.valueOf("BOOLEAN")){
					ltrue = code.newLabel();
					lnext = code.newLabel();
					code.emit(OpCode.ldc ,0);
					code.emit(OpCode.if_icmpeq, ltrue);
					relE_ret = relE();
					if(relE_ret == Type.valueOf("BOOLEAN")){
						code.emit(OpCode.GOto,lnext);
						code.emitLabel (ltrue);
						code.emit(OpCode.ldc,0);
						code.emitLabel(lnext);
						andE_p_ret = andE_p(relE_ret);
						return andE_p_ret;
					}else{
						error("Type Error in method: 'andE_p()'");
						return null;
					}
				}else{
					error("Type Error in method: 'andE_p()'");
					return null;
				}
			case Tag.OR:
				return andE_p_ret;
			case Tag.THEN:
				return andE_p_ret;
			case Tag.DO:
				return andE_p_ret;
			case ')':
				return andE_p_ret;
			case ';':
				return andE_p_ret;
			case Tag.ELSE:
				return andE_p_ret;
			case Tag.END:
				return andE_p_ret;
			case Tag.EOF:
				return andE_p_ret;
			default:
				error("Erroneous Character in method 'orE_p()'");
				return null;
		}
	}
	private Type relE(){
		Type addE_ret, relE_p_ret;
		addE_ret = addE();
		relE_p_ret = relE_p(addE_ret);
		return relE_p_ret;
	}
	
	private Type relE_p(Type val){
		OpCode oprel_ret = oprel();
		Type addE_ret;
		if(oprel_ret != null){
			if((oprel_ret == OpCode.valueOf("if_icmpeq")) || (oprel_ret == OpCode.valueOf("if_icmpne"))){
				addE_ret = addE();
				if(((val == Type.valueOf("BOOLEAN")) && (addE_ret == Type.valueOf("BOOLEAN"))) || ((val == Type.valueOf("INTEGER") && addE_ret == Type.valueOf("INTEGER")))){
					relE_p_Support(oprel_ret);
					return Type.valueOf("BOOLEAN");
				}else{
					error("Type Error in method: 'relE_p()'");
					return null;
				}
			}else{
				addE_ret = addE();
				if((val == Type.valueOf("INTEGER")) && (addE_ret == Type.valueOf("INTEGER"))){
					relE_p_Support(oprel_ret);
					return Type.valueOf("BOOLEAN");
				}else{
					error("Type Error in method: 'relE_p()'");
					return null;
				}
			}
		}else{//transizione silente
			switch(look.tag){
				case Tag.AND:
					return val;
				case Tag.OR:
					return val;
				case Tag.THEN:
					return val;
				case Tag.DO:
					return val;
				case ')':
					return val;
				case ';':
					return val;
				case Tag.ELSE:
					return val;
				case Tag.END:
					return val;
				case Tag.EOF:
					return val;
				default:
					error("Erroneous Character in method 'relE_p()'");
					return null;
			}
		}
	}
	private OpCode oprel(){
		switch(look.tag){
			case Tag.EQ:
				match(Tag.EQ);
				return OpCode.valueOf("if_icmpeq");
			case Tag.NE:
				match(Tag.NE);
				return OpCode.valueOf("if_icmpne");
			case Tag.LE:
				match(Tag.LE);
				return OpCode.valueOf("if_icmple");
			case Tag.GE:
				match(Tag.GE);
				return OpCode.valueOf("if_icmpge");
			case '<':
				match('<');
				return OpCode.valueOf("if_icmplt");
			case '>':
				match('>');
				return OpCode.valueOf("if_icmpgt");
			default:
				return null;
		}
	}
	private void relE_p_Support(OpCode oprel_ret){
		int ltrue, lnext;
		ltrue = code.newLabel();
		lnext = code.newLabel();
		code.emit(oprel_ret,ltrue );
		code.emit(OpCode.ldc,0);
		code.emit(OpCode.GOto,lnext);
		code.emitLabel (ltrue);
		code.emit(OpCode.ldc,1);
		code.emitLabel (lnext);
	}
	
	private Type addE(){
		Type multE_ret, addE_p_ret;
		multE_ret = multE();
		addE_p_ret = addE_p(multE_ret);
		return addE_p_ret;
	}
	private Type addE_p(Type val) {
		Type multE_ret, addE_p_ret = val;
		switch(look.tag) {
			case '+':
				match('+');
				multE_ret = multE();
				if(val == Type.valueOf("INTEGER") && multE_ret == Type.valueOf("INTEGER")){
					addE_p_ret = addE_p(multE_ret);
					code.emit(OpCode.iadd);
					return addE_p_ret;
				}else{
					error("Type Error in method: 'addE_p()'");
					return null;
				}
			case '-':
				match('-');
				multE_ret = multE();
				if(val == Type.valueOf("INTEGER") && multE_ret == Type.valueOf("INTEGER")){
					addE_p_ret = addE_p(multE_ret);
					code.emit(OpCode.isub);
					return addE_p_ret;
				}else{
					error("Type Error in method: 'addE_p()'");
					return null;
				}
			case Tag.EQ:
				return addE_p_ret;
			case Tag.NE:
				return addE_p_ret;
			case Tag.LE:
				return addE_p_ret;
			case Tag.GE:
				return addE_p_ret;
			case '<':
				return addE_p_ret;
			case '>':
				return addE_p_ret;
			case Tag.AND:
				return addE_p_ret;
			case Tag.OR:
				return addE_p_ret;
			case Tag.THEN:
				return addE_p_ret;
			case Tag.DO:
				return addE_p_ret;
			case ')':
				return addE_p_ret;
			case ';':
				return addE_p_ret;
			case Tag.ELSE:
				return addE_p_ret;
			case Tag.END:
				return addE_p_ret;
			case Tag.EOF:
				return addE_p_ret;
			default:
				error("Erroneous Character in method 'addE_p()'");
				return null;
		}
	}
	private Type multE(){
		Type fact_ret, multE_p_ret;
		fact_ret = fact();
		multE_p_ret = multE_p(fact_ret);
		return multE_p_ret;
	}
	private Type multE_p(Type val){
		Type fact_ret, multE_p_ret = val;
		switch(look.tag) {
			case '*':
				match('*');
				fact_ret = fact();
				if(val == Type.valueOf("INTEGER") && fact_ret == Type.valueOf("INTEGER")){
					multE_p_ret = multE_p(fact_ret);
					code.emit(OpCode.imul);
					return multE_p_ret;
				}else{
					error("Type Error in method: 'multE_p()'");
					return null;
				}
			case '/':
				match('/');
				fact_ret = fact();
				if(val == Type.valueOf("INTEGER") && fact_ret == Type.valueOf("INTEGER")){
					multE_p_ret = multE_p(fact_ret);
					code.emit(OpCode.idiv);
					return multE_p_ret;
				}else{
					error("Type Error in method: 'multE_p()'");
					return null;
				}
			case '+':
				return multE_p_ret;
			case '-':
				return multE_p_ret;
			case Tag.EQ:
				return multE_p_ret;
			case Tag.NE:
				return multE_p_ret;
			case Tag.LE:
				return multE_p_ret;
			case Tag.GE:
				return multE_p_ret;
			case '<':
				return multE_p_ret;
			case '>':
				return multE_p_ret;
			case Tag.AND:
				return multE_p_ret;
			case Tag.OR:
				return multE_p_ret;
			case Tag.THEN:
				return multE_p_ret;
			case Tag.DO:
				return multE_p_ret;
			case ')':
				return multE_p_ret;
			case ';':
				return multE_p_ret;
			case Tag.ELSE:
				return multE_p_ret;
			case Tag.END:
				return multE_p_ret;
			case Tag.EOF:
				return multE_p_ret;
			default:
				error("Erroneous Character in method 'addE_p()'");
				return null;
		}
	}
	private Type fact(){
		Type orE_ret, ID_ret;
		int val;
		switch(look.tag){
			case '(':
				match('(');
				orE_ret = orE();
				match(')');
				return orE_ret;
			case Tag.ID:
				String s = ((Word)look).lexeme;
				match(Tag.ID);
				val = symb.lookupAddress(s);
				ID_ret = symb.lookupType(s);
				code.emit(OpCode.iload, val);
				return ID_ret;
			case Tag.TRUE:
				match(Tag.TRUE);
				code.emit(OpCode.ldc, 1);
				Type ret1 = Type.valueOf("BOOLEAN");
				return ret1;
			case Tag.FALSE:
				match(Tag.FALSE);
				code.emit(OpCode.ldc, 0);
				Type ret2 = Type.valueOf("BOOLEAN");
				return ret2;
			case Tag.NUM:
				val = Integer.parseInt(((Number)look).lexeme);
				match(Tag.NUM);
				code.emit(OpCode.ldc, val);
				Type ret3 = Type.valueOf("INTEGER");
				return ret3;
			default: 
				error("Syntax Error in method: 'fact()'");
				return null;
				
		}
	}
	
	public static void main(String[] args) {
		Lexer lex = new Lexer();
		CodeGenerator cg = new CodeGenerator();
		SymbolTable st = new SymbolTable();
		
               
		try {
                        URL url = Valutatore.class.getResource("Input.pas");           
			BufferedReader br = new BufferedReader(new FileReader(url.getPath()));
			Valutatore valutatore = new Valutatore(lex, br, cg, st);
			valutatore.start();
			br.close();
			cg.toJasmin();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}

