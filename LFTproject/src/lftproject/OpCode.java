/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lftproject;

public enum OpCode {
	ldc ,            //0
	imul ,
	ineg , 
	idiv , 
	iadd ,
	isub ,           //5
	istore ,
	ior, 
	iand , 
	iload ,
	if_icmpeq ,      //10
	if_icmple , 
	if_icmplt , 
	if_icmpne, 
	if_icmpge ,
	if_icmpgt ,      //15
	ifne , 
	GOto , 
	invokestatic , 
	label            //19
}

