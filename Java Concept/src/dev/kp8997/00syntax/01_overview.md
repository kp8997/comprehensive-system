1.Keywords:
	Types: 
	Method: 
	Property: 
	Function
	Underscore: _

------------------------------------------------------
2. Conditional branch

if () {

} else if () {

} else {
    
}


------------------------------------------------------
3. Method

public static void function(int a, int b, String c) { return ;
    
}


------------------------------------------------------
4. switch

switch (a) {
    case 1:
        break;
    case 2: case 3: case 4:
        break;
    default:
        break;
}

switch (a) {
    case 1 -> break;
    case 2: case 3: case 4: {
        break;
    }
    default -> {
        break
    }
}

// yield must be inside the { } 
public static int getValue(int a) { return ;
    return switch (a > 1) {
        case 1 -> { yield 5;}
        case 2: case 3: case 4: {
            yield 10;
        }
        default -> {
            yield 10;
        }
    }
}

------------------------------------------------------
5. Loop
for (int i = 0; i < 10; i++) {
    
}

// check condition before do
while (true) {
	break;
}

// do first then check condition
do {
	continue;
} while ()

------------------------------------------------------
6. Console input

// NOT EASY TO USE
System.in

// KIND OF EASY
System.console

// EASY TO USE
Scanner sc = new Scanner(System.in);
sc.nextInt();
sc.nextLine();
