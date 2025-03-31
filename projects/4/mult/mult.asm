// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Mult.asm

// Multiplies R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)         
	
// sum = 0
@2
M=0

// while (R1 > 0)
// if (R1 = 0) break
// else R1--
(LOOP)
@1
D=M
@END
D;JLE
D=D-1
@1
M=D

// sum += R0
@0
D=M
@2
M=M+D
@LOOP
0;JMP
(END)
