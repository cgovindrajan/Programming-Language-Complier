# Programming-Language-Complier
Implement a compiler for a small programming language.
Implement a scanner for the programming language with the following lexical structure.
 
comment ::=   /*   NOT(*/)*  */
token ::= ident  | keyword | frame_op_keyword | filter_op_keyword | image_op_keyword | boolean_literal
 	| int_literal  | separator  | operator
ident ::= ident_start  ident_part*    (but not reserved)
ident_start ::=  A .. Z | a .. z | $ | _
ident_part ::= ident_start | ( 0 .. 9 )
int_literal ::= 0  |  (1..9) (0..9)*
keyword ::= integer | boolean | image | url | file | frame | while | if | sleep | screenheight | screenwidth 
filter_op_keyword ∷= gray | convolve | blur | scale
image_op_keyword ∷= width | height 
frame_op_keyword ∷= xloc | yloc | hide | show | move
boolean_literal ::= true | false
separator ::= 	;  | ,  |  (  |  )  | { | }
operator ::=   	|  | &  |  ==  | !=  | < |  > | <= | >= | +  |  -  |  *   |  /   |  % | !  | -> |  |-> | <-
