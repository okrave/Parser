# Laboratorio di Linguaggi Formali e Traduttori

# Corso di Studi in Informatica

# A.A. 2015/



## Progetto di laboratorio

Il progetto di laboratorio consiste in una serie di esercitazioni assistite mirate allo sviluppo di
un semplice traduttore. Il corretto svolgimento di tali esercitazioni presuppone una buona cono-
scenza del linguaggio di programmazione Java e degli argomenti di teoria del corso Linguaggi
Formali e Traduttori.

## 2 Analisi lessicale

Consideriamo un semplice linguaggio di programmazione, dove la sintassie descritta dalla`
seguente grammatica:

```
〈prog〉 ::= [〈decl〉;]∗〈stmt〉EOF

〈decl〉 ::= varID[,ID]∗:〈type〉

〈type〉 ::= integer|boolean

〈expr〉 ::= 〈andExpr〉[||〈andExpr〉]∗

〈andExpr〉 ::= 〈relExpr〉[&&〈relExpr〉]∗

〈relExpr〉 ::= 〈addExpr〉[ == 〈addExpr〉|<>〈addExpr〉
|<=〈addExpr〉|>=〈addExpr〉
|< 〈addExpr〉|> 〈addExpr〉]?

〈addExpr〉 ::= 〈mulExpr〉[+〈mulExpr〉|-〈mulExpr〉]∗

〈mulExpr〉 ::= 〈unExpr〉[*〈unExpr〉|/〈unExpr〉]∗

〈unExpr〉 ::= not〈unExpr〉|〈primary〉

〈primary〉 ::= (〈expr〉)|ID|NUM|true|false

〈stmt〉 ::= ID:=〈expr〉
| print (〈expr〉)
| 〈stmt〉[;〈stmt〉]∗
```

I terminaliIDcorrispondono all’espressione regolare(a..z|A..Z)(a..z|A..Z| 0 .. 9 )∗, e i terminali
NUMcorrispondono all’espressione regolare 0 .. 9 ( 0 .. 9 )∗.
Il linguaggio permette di scrivere programmi costituiti da due sezioni (la prima delle quali
puo essere omessa):`

1. Dichiarazioni di variabili, dove l’elenco di variabilie preceduto dalla parola chiave` vare
    seguito da:e un tipo,integeroppureboolean. Esempi:
       - var x,y:integer;
       - var z:boolean;


2. Sequenze di comandi, con due tipologie distinte di comando.
    - Assegnamento di un valore valori ad una variabile, usando la sintassi:=. Esempi:
       x:=5;,y:=x+7;ez:=x&&y;.
    - Scrittura sul terminale, usando la parola chiaveprint. Esempi:print(x),print(x||y)
       eprint((5+2)*3).

Le espressioni che possiamo scrivere in commandi di assegnamento eprintsono composte
soltanto da numeri non negativi (ovvero sequenze di cifre decimali), operatori di somma e sot-
trazione+e-, operatori di moltiplicazione e divisione*e/, simboli di parentesi(e), operatori
relazionali<,<=,==,>=,>, e<>, operatori logici&&,||enot. Esempi:

- 34+26-
- (34+26)-
- x == (y+26)* 5
- x && y == false
- x <= y+
- not x || y == true

Ad esempio, il seguente programma calcola la velocita media di un viaggio dati il numero di
metri percorsise i secondi impiegatit:

var s, t: integer;
var datiOK: boolean;

s:=300000000;
t:=1;

datiOK:= (s >=0) && (t > 0);
print(datiOK);

print(s/t)

**Esercizio 2.1.** Si scriva in Java un analizzatore lessicale che legga da tastiera comandi scritti in
questo linguaggio e per ciascuna comando stampi una sequenza di token.

- I token che corrispondono a numeri prenderanno la forma〈NUM,valore〉, dove valoree un
    intero non negativo. Ad esempio, il token che corrisponde al numero 12 sarà〈NUM, 12 〉
- I token che corrispondono agli identificatori prenderanno la forma〈ID,“lessema”〉. Ad
    esempio, il token che corrisponde axsarà〈ID,“x”〉, e il token che corrisponde a temp sarà
    〈ID,“temp”〉.
- I token che corrispondono agli elementi della sintassi che consistono di un solo carattere (ad
    esempio,’(’,’+’e’:’) prenderanno la forma〈nome〉, dove il nome è il codice ASCII del
    carattere. Ad esempio, il token che corrisponde a ’(’ sarà 〈′(′〉, e il token che corrisponde
    a ’:’ sarà〈′:′〉.
- I token che corrispondono agli elementi della sintassi che consistono di piu caratteri (ad`
    esempio,&&,<>,print,integere:=) prenderanno la forma〈nome,“lessema”〉. Ad
    esempio, il token che corrisponde a:=sarà〈ASSIGN,“:=”〉, e il token che corrisponde
    aprints arà〈PRINT,“print”〉.


Definiamo una classeTagin Listing 1, utilizzando un insieme opportuno di costanti intere
per rappresentare il nome dei token. (Si nota che non è assolutemente necessario definire tali
costanti per tutti i token: per quelli che corrispondono a un solo carattere, si puo utilizzare il
codice ASCII del carattere.)

```
Listing 1: ClasseTag
```
**public class** Tag {
**public final static int**
EOF = -1,
NUM = 256,
ID = 257,
AND = 258,
OR = 259,
VAR = 260,
INTEGER = 261,
BOOLEAN = 262,
ASSIGN = 263,
EQ = 264,
GE = 265,
LE = 266,
NE = 267,
TRUE = 268,
FALSE = 269,
NOT = 270,
PRINT = 271;
}

Questa scelta ha la conseguenza che l’output del nostro programma sarà della forma〈 271 ,“print”〉
〈 40 〉〈 257 ,“x”〉〈 259 ,“||”〉〈 257 ,“y”〉〈 41 〉〈 59 〉per il comandoprint(x||y); anzichè〈PRINT,“print”〉
〈′(′〉〈ID,“x”〉〈OR,“||”〉〈ID,“y”〉〈′)′〉〈′;′〉
Nota: l’analizzatore lessicale none preposto al riconoscimento della strutturadei comandi.
Pertanto, esso accettera anche comandi “errati” quali ad esempio:`

- 5+)
- (34+26( - (2+15-( 27
- var 5 := print < boolean

L’analizzatore lessicale dovra ignorare tutti i caratteri riconosciuti come “spazi” (incluse le
tabulazioni e i ritorni a capo), ma dovra segnalare la presenza di caratteri illeciti, quali ad esempio
#o@. Per semplicita, si può utilizzare un carattere particolare, come $, per segnalare la fine del
input (e quindi produrre un token con nomeEOFche corrisponde alla fine del input).
Definiamo una classeTokenper rappresentare i token (una possibile implementazione della
classeTokene in Listing 2). Definiamo inoltre le classe WordeNumberderivate daToken,
dove la classeWordrappresenta i token che corrispondono agli identificatori, alle parole chiave
e agli elementi del sintassi che consistono di piu caratteri (ad esempio <=e&&), e dove la classe
Numberrappresenta i token che corrispondono ai numeri.


**Gestione delle parole chiave.** Abbiamo bisogno di un meccanismo per memorizzare le parole
chiave (var,integer,boolean,not,true,falseeprint), in modo tale che possiamo distin-
guirle da eventuali identificatori. Un modo è di utilizzare unatabella hash. La classeHashtable
e utilizzata per immagazzinare coppie che consistono di una chiave(key) e unvalore. La classe
Hashtablemette a disposizione due metodi,puteget, che servono rispettivamente per:

1. inserire associazioni(chiave,valore)nella tabella;
2. recuperare il valore associato a una chiave dalla tabella.

Nel contesto di nostra implementazione di un’analizzatore lessicale, possiamo utilizzare la
classeHashtablenel
La tabella hash puo essere utilizzata anche per memorizzare gli identificatori già visti durante
l’analisi lessicale.



**Esercizio 2.2.** Consideriamo la seguente versione modificata della produzione per〈stmt〉:

```
〈stmt〉 ::= ID:=〈expr〉
| print (〈expr〉)
| if〈expr〉then〈stmt〉[else〈stmt〉]?
| while〈expr〉do〈stmt〉
| begin〈stmt〉[;〈stmt〉]∗end
```
Estendere l’implementazione del analizzatore lessicale per gestire comandi di programmi scritti
nel linguaggio modificato (cioe nel linguaggio con la versione modificata della produzione per
〈stmt〉).


**Lettura da un file.** La lettura di un programma da un file, anzichè dalla tastiera come in Listing
4, puo essere realizzata nel modo illustrato in Listing 5. Il metodo maincrea un oggetto della
classeBufferedReader, che poie passato come parametro al metodo lexicalscan, e a sua
volta areadch.




## 3 Analisi sintattica

**Esercizio 3.1.** Si scriva un analizzatore sintattico a discesa ricorsiva che parsifichi espressioni
aritmetiche molto semplici, composte soltanto da numeri non negativi (ovvero sequenze di cifre
decimali), operatori di somma e sottrazione+e-, operatori di moltiplicazione e divisione*e/,
simboli di parentesi(e). In particolare, l’analizzatore deve riconoscere le espressioni generate
dalla grammatica che segue:


```
〈start〉 ::= 〈expr〉EOF

〈expr〉 ::= 〈term〉〈exprp〉

〈exprp〉 ::= +〈term〉〈exprp〉
| -〈term〉〈exprp〉
| ε

〈term〉 ::= 〈fact〉〈termp〉

〈termp〉 ::= *〈fact〉〈termp〉
| /〈fact〉〈termp〉
| ε

```
〈fact〉 ::= (〈expr〉)|NUM
Il programma deve fare uso dell’analizzatore lessicale sviluppato in precedenza. Si nota che
l’insieme di token corrispondente alla grammatica di questa sezionee un sottoinsieme dell’in-
sieme di token corrispondente alla grammatica della Sezione 2. Nei casi in cui l’input non cor-
risponde alla grammatica, l’output del programma deve consistere di un messaggio di errore
(come illustrato nelle lezioni in aula) indicando la procedura in esecuzione quando l’errore è
stato individuato.

}

```
〈start〉 ::= 〈expr〉EOF{print(expr.val)}

〈expr〉 ::= 〈term〉{exprp.i=term.val}〈exprp〉{expr.val=exprp.val}

〈exprp〉 ::= +〈term〉{exprp 1 .i=exprp.i+term.val}〈exprp 1 〉{exprp.val=exprp 1 .val}
| -〈term〉{exprp 1 .i=exprp.i−term.val}〈exprp 1 〉{exprp.val=exprp 1 .val}
| ε{exprp.val=exprp.i}

〈term〉 ::= 〈fact〉{termp.i=fact.val}〈termp〉{term.val=termp.val}

〈termp〉 ::= *〈fact〉{termp 1 .i=termp.i∗fact.val}〈termp 1 〉{termp.val=termp 1 .val}
| /〈fact〉{termp 1 .i=termp.i/fact.val}〈termp 1 〉{termp.val=termp 1 .val}
| ε{termp.val=termp.i}

〈fact〉 ::= (〈expr〉){fact.val=expr.val}
| NUM{fact.val=NUM.value}
```
Si nota che un indice (cioe 1) è usato per distinguere due diverse occorrenze dello stesso non-
terminale (ad esempio,〈exprp〉) nella stessa produzione. Inoltre, si nota che il terminaleNUMha
l’attributovalue, chee il valore numerico del terminale, fornito dall’analizzatore lessicale.
Una possibile struttura del programma è la seguente. **Nota:** come indicato,e fortemente
consigliato la creazione di una nuova classe (chiamataValutatorein Listing 8).

## 4 Traduzione diretta dalla sintassi

**Esercizio 4.1** (Valutatore di espressioni semplici)**.** Modificare l’analizzatore sintattico di Esercizio
3.1 in modo da valutare le espressioni aritmetiche semplici, facendo riferimento allo schema di
traduzione diretto dalla sintassi seguente:

```
〈start〉 ::= 〈expr〉EOF{print(expr.val)}

〈expr〉 ::= 〈term〉{exprp.i=term.val}〈exprp〉{expr.val=exprp.val}

〈exprp〉 ::= +〈term〉{exprp 1 .i=exprp.i+term.val}〈exprp 1 〉{exprp.val=exprp 1 .val}
| -〈term〉{exprp 1 .i=exprp.i−term.val}〈exprp 1 〉{exprp.val=exprp 1 .val}
| ε{exprp.val=exprp.i}

〈term〉 ::= 〈fact〉{termp.i=fact.val}〈termp〉{term.val=termp.val}

〈termp〉 ::= *〈fact〉{termp 1 .i=termp.i∗fact.val}〈termp 1 〉{termp.val=termp 1 .val}
| /〈fact〉{termp 1 .i=termp.i/fact.val}〈termp 1 〉{termp.val=termp 1 .val}
| ε{termp.val=termp.i}

〈fact〉 ::= (〈expr〉){fact.val=expr.val}
| NUM{fact.val=NUM.value}

```
Si nota che un indice (cioe 1) è usato per distinguere due diverse occorrenze dello stesso non-
terminale (ad esempio,〈exprp〉) nella stessa produzione. Inoltre, si nota che il terminaleNUMha
l’attributovalue, chee il valore numerico del terminale, fornito dall’analizzatore lessicale.
Una possibile struttura del programma è la seguente. **Nota:** come indicato,e fortemente
consigliato la creazione di una nuova classe (chiamataValutatorein Listing 8).


