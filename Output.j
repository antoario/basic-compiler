.class public Output 
.super java/lang/Object

.method public <init>()V
 aload_0
 invokenonvirtual java/lang/Object/<init>()V
 return
.end method

.method public static print(I)V
 .limit stack 2
 getstatic java/lang/System/out Ljava/io/PrintStream;
 iload_0 
 invokestatic java/lang/Integer/toString(I)Ljava/lang/String;
 invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
 return
.end method

.method public static read()I
 .limit stack 3
 new java/util/Scanner
 dup
 getstatic java/lang/System/in Ljava/io/InputStream;
 invokespecial java/util/Scanner/<init>(Ljava/io/InputStream;)V
 invokevirtual java/util/Scanner/next()Ljava/lang/String;
 invokestatic java/lang/Integer.parseInt(Ljava/lang/String;)I
 ireturn
.end method

.method public static run()V
 .limit stack 1024
 .limit locals 256
 invokestatic Output/read()I
 istore 0
 invokestatic Output/read()I
 istore 1
 ldc 10
 dup
 istore 2
 istore 3
 ldc 1
 invokestatic Output/print(I)V
 iload 0
 iload 1
 iadd 
 iload 2
 iadd 
 iload 3
 iadd 
 invokestatic Output/print(I)V
 ldc 2
 invokestatic Output/print(I)V
 iload 0
 iload 2
 if_icmpgt L4
 goto L2
L4:
 iload 1
 iload 3
 if_icmpgt L3
 goto L2
L3:
 iload 0
 invokestatic Output/print(I)V
 iload 1
 invokestatic Output/print(I)V
 goto L1
L2:
L1:
L5:
 iload 0
 ldc 0
 if_icmpgt L6
 goto L7
L6:
 iload 0
 iload 1
 if_icmpgt L10
 goto L9
L10:
 iload 0
 ldc 2
 isub 
 istore 0
 iload 0
 invokestatic Output/print(I)V
 goto L8
L9:
 iload 0
 iload 1
 if_icmpeq L12
 goto L11
L12:
 ldc 0
 istore 0
 iload 0
 invokestatic Output/print(I)V
 goto L8
L11:
 iload 0
 ldc 1
 isub 
 istore 0
 iload 0
 invokestatic Output/print(I)V
L8:
 goto L5
L7:
L0:
 return
.end method

.method public static main([Ljava/lang/String;)V
 invokestatic Output/run()V
 return
.end method

