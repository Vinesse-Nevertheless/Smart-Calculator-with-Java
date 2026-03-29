package calculator;

import java.math.BigInteger;
import java.util.*;

public class Main {

    OperandHandler variable;

    Calculator expression;

    public static void main(String[] args) {
        new Main().getNumbers();
    }

    void getNumbers() {
        Scanner scanner = new Scanner(System.in);
        variable = new OperandHandler();

        while (true) {
            String input = scanner.nextLine();

            if (input.isBlank()) {
                continue;
            }

            if (input.equals("/exit")) {
                System.out.println("Bye!");
                return;
            }

            if (input.equals("/help")) {
                System.out.println("The program is capable of PEMDAS calculations using whole integers and variables: ");
                System.out.println("\t parenthesis");
                System.out.println("\t exponents");
                System.out.println("\t multiplication");
                System.out.println("\t division");
                System.out.println("\t addition");
                System.out.println("\t subtraction");
                System.out.println("Menu : /help for help and /exit to exit program.");
                continue;
            }

            if (input.startsWith("/")) {
                System.out.println("Unknown command");
                continue;
            }
            input = input.strip();
            processInput(input);
        }
    }

    void processInput(String input) {
        expression = new Calculator();
        variable = new OperandHandler();

        boolean isAssignment = input.indexOf("=") != -1;

        if ( isAssignment && variable.isValidAssignment(input)) {
            expression.makePostfix(OperandHandler.fInput);
            BigInteger ans = expression.calculate();
            if (!expression.hasDivideBy0) {
                String key = input.substring(0,input.indexOf("=")).strip();
                variable.addToVarMap(key, ans);
            }
        }
        else if (!isAssignment && variable.isValidExpression(input)) {
            expression.makePostfix(OperandHandler.fInput);
            BigInteger ans = expression.calculate();
            if (!expression.hasDivideBy0) {
                printAnswer(ans);
            }
        }else if(!isAssignment && OperandHandler.fInput != null && OperandHandler.fInput.isEmpty()){
            if (variable.isSingleVar(input)) {
                BigInteger ans = OperandHandler.getValue(input);
                printAnswer(ans);
            }
        }
    }

    void printAnswer(BigInteger ans) {
        System.out.println(ans);
    }
}

class OperandHandler {
    static Map<String, BigInteger> memoryMap = new HashMap<>();
    static String[] split;
    static String fInput;

    static String getFormattedInputString(String input) {

        StringBuilder simpIn = new StringBuilder();

        input =  input.replaceAll("[\\s]", "");

        //resolve multiple + and - signs
        int i = 0;
        while (i < input.length()) {
            if (input.charAt(i) == '-' || input.charAt(i) == '+') {
                int start = i;
                while (i < input.length() && !Character.isLetterOrDigit(input.charAt(i)) && input.charAt(i) != '(') {
                    i++;
                }
                if (OperandHandler.canSimplifySign(input.substring(start, i))) {
                    String s = Calculator.simplifySign(input.substring(start, i));
                    simpIn.append(s);
                }else{
                    return null;
                }
            }
            else {
                simpIn.append(input.charAt(i));
                i++;
            }
        }

        //give string proper spacing
        input = simpIn.toString();

        input = input.replaceAll("(?<=[a-zA-Z0-9()])([/*+-^])(?=[a-zA-Z0-9()])", " $1 ");
        input = input.replaceAll("(?<=[^a-zA-Z[+-]?\\d() ])([/*+-^])(?=[^a-zA-Z0-9()])", " $1 ");
        input = input.replaceAll("(?<=[a-zA-Z0-9()])([/*+-^])(?=[^a-zA-Z0-9()])", " $1 ");
        input = input.replaceAll("(?<=[^a-zA-Z0-9()])([/*^])(?=[a-zA-Z0-9()])", "$1 ");

        input = input.replaceAll("(\\()", " $1 ");
        input = input.replaceAll("(\\))", " $1 ");

        input = input.replaceAll(" {2}+", " ");

        input = input.replaceAll("(?<=[a-zA-Z0-9])([ ])(?=[a-zA-Z0-9])", "");

        input = input.strip();

        return input;

    }


    boolean isValidAssignment(String input) {

        String assigned = input.substring(input.indexOf("=")+1).strip();
        if (hasDanglingSign(assigned) || hasMissingOperator(assigned)){
            System.out.println("Invalid assignment");
            return false;
        }

        input = input.replaceAll(" {1}+", "");

        split = input.split("=");

        if (split.length > 2 || split.length == 1) {
            System.out.println("Invalid assignment");
            return false;
        }

        boolean validKey;

        if (split.length == 2) {
            validKey = isValidKey(split[0]);

            if (!validKey) {
                return false;
            }

            fInput = getFormattedInputString(split[1]);

            String[] fSplit = fInput.split(" ");

            for (int i = 0; i < fSplit.length; i++) {
                if(fSplit[i].matches("[-+*/^()]")){
                    continue;
                }
                if ( fSplit[i].matches("[a-zA-Z]*") || fSplit[i].matches("[+-]?\\d*") ){
                    boolean validValue = isValidValue(fSplit[i]);
                    if (!validValue) {
                        return false;
                    }
                    if (fSplit[i].matches("[a-zA-Z*]") && !isValidVariable(fSplit[i])) {
                        return false;
                    }
                }else{
                    System.out.println("Invalid assignment");
                    return false;
                }
            }

            if (fSplit.length > 1){
                if(!isValidExpression(fInput)){
                    return false;
                }
            }
        }

        return true;
    }

    boolean isSingleVar(String key) {

        //if user has only entered a variable or can not be split on =
        if (isValidKey(key) && isValidVariable(key) && memoryMap.containsKey(key)) {
            return true;
        }

        return false;
    }

    boolean isValidKey(String key) {

        key = key.strip();

        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            if (!Character.isLetter(c) || c > 'z') {
                System.out.println("Invalid identifier");
                return false;
            }
        }
        return true;
    }

    boolean isValidValue(String input) {
        if (input.matches("[+-]?\\d*")) {
            return true;
        } else if (memoryMap.containsKey(input)) {
            return true;
        } else {
            for (int i = 0; i < input.length(); i++) {
                char c = input.charAt(i);
                if (!Character.isLetter(c) || c > 'z') {
                    System.out.println("Invalid assignment");
                    return false;
                }
            }
        }

        return true;
    }

    boolean isValidVariable(String input) {
        if (input.matches("\\w*") && !memoryMap.containsKey(input)) {
            System.out.println("Unknown variable");
            return false;
        }
        return true;
    }

    void addToVarMap(String key, BigInteger val) {
        memoryMap.put(key, val);
    }

    boolean isValidExpression(String input) {

        if (hasDanglingSign(input) ||  hasMissingOperator(input)){
            System.out.println("Invalid expression");
            return false;
        }

        int opening = 0;
        int closing = 0;

        fInput = getFormattedInputString(input);
        if(fInput == null){
            return false;
        }

        split = fInput.split(" ");

        for (int i = 0; i < split.length; i++) {
            if (split[i].equals("(")) {
                opening++;
            }
            else if (split[i].equals(")")) {
                if (i-1 > -1 && split[i-1].equals("(")){
                    System.out.println("Invalid expression");
                    return false;
                }
                closing++;
            }
            else if (i-1 > -1 &&  !split[i-1].matches("[-+*/^()]") &&  !split[i].matches("[-+*/^()]")){
                return false;
            }
            else if (!hasValidOperatorSequence(i, split)) {
                return false;
            }
            else if (split[i].matches("[+-]?\\d*") && !isValidValue(split[i])) {
                return false;
            }
            else if (split[i].matches("[a-zA-Z]*") && (!isValidKey(split[i]) || !isValidVariable(split[i]))) {
                return false;
            }
        }

        if (!hasEqualParen(opening, closing)) {
            return false;
        }

        return true;
    }

    boolean hasValidOperatorSequence(int i, String[] split) {
        if (i + 1 < split.length && split[i].matches("[*/^]") && split[i + 1].matches("[*/^]{1}")) {
            System.out.println("Invalid expression");
            return false;
        }
        return true;
    }

    boolean hasMissingOperator(String input){
        for (int i = 0; i < input.length() ; i++) {
            char c = input.charAt(i);
            if (i > 0 && i + 1 < input.length()) {
                char pre = input.charAt(i - 1);
                char post = input.charAt(i + 1);

                if (Character.isWhitespace(c)){
                    if (Character.isLetterOrDigit(pre) && Character.isLetterOrDigit(post)){
                        return true;
                    }
                }
            }
        }

        return false;
    }

    boolean hasEqualParen(int opening, int closing) {
        if (opening != closing) {
            System.out.println("Invalid expression");
        }
        return opening == closing;
    }

    boolean hasDanglingSign(String input){
        char firstChar = input.charAt(0);
        char lastChar = input.charAt(input.length()-1);
        if (firstChar != '(' && firstChar != '+' && firstChar != '-' && !Character.isLetterOrDigit(firstChar)) {
            return true;
        }

        if (lastChar != ')' && !Character.isLetterOrDigit(lastChar)) {
            return true;
        }

        return false;
    }

    static boolean canSimplifySign(String inputSign) {

        if (inputSign.charAt(0) != '-' && inputSign.charAt(0) != '+') {
            return false;
        }

        for (int i = 0; i < inputSign.length(); i++) {
            if (inputSign.charAt(i) != '-' && inputSign.charAt(i) != '+') {
                System.out.println("Invalid expression");
                return false;
            }
        }

        return true;
    }

    public static BigInteger getValue(String token) {

        if (memoryMap.containsKey(token)) {
            return memoryMap.get(token);
        }

        return new BigInteger(token);
    }
}


class Calculator {

    String[] split;

    List<String> postfixList = new ArrayList<>();

    static Map<String, Integer> pemdas = new HashMap<>();

    boolean hasDivideBy0 = false;

    static {
        pemdas.put(")", 4);
        pemdas.put("(", 4);
        pemdas.put("^", 3);
        pemdas.put("*", 2);
        pemdas.put("/", 2);
        pemdas.put("+", 1);
        pemdas.put("-", 1);
    }

    /**
     * Add operands (numbers and variables) to the result (postfix notation) as they arrive.
     * If the stack is empty or contains a left parenthesis on top, push the incoming operator on the stack.
     * If the incoming operator has higher precedence than the top of the stack, push it on the stack.
     * If the precedence of the incoming operator is lower than or equal to that of the top of the stack, pop the stack and add operators to the result until you see an operator that has smaller precedence or a left parenthesis on the top of the stack; then add the incoming operator to the stack.
     * If the incoming element is a left parenthesis, push it on the stack.
     * If the incoming element is a right parenthesis, pop the stack and add operators to the result until you see a left parenthesis. Discard the pair of parentheses.
     * At the end of the expression, pop the stack and add all operators to the result.
     */
    void makePostfix(String input) {
        Deque<String> signStack = new ArrayDeque<>();
        split = input.split(" ");

        String sign = "";

        for (int i = 0; i < split.length; i++) {
            if (!split[i].matches("[()^*/+-]*")) {
                postfixList.add(split[i]);
            } else {
                sign = split[i];
                if (signStack.isEmpty() || signStack.getLast().equals("(") ||
                        (!sign.equals(")") && pemdas.get(sign) > pemdas.get((signStack.getLast())))) {
                    signStack.offerLast(sign);
                } else if (pemdas.get(sign) <= pemdas.get((signStack.getLast()))) {
                    while (!signStack.isEmpty() &&
                            (!signStack.getLast().equals("(") &&
                                    pemdas.get(signStack.getLast()) >= pemdas.get(sign))) {
                        String poppedSign = signStack.pollLast();
                        postfixList.add(poppedSign);
                    }
                    signStack.offerLast(sign);
                } else if (sign.equals(")")) {
                    while (!signStack.isEmpty() && !signStack.getLast().equals("(")) {
                        String poppedSign = signStack.pollLast();
                        postfixList.add(poppedSign);
                    }
                    signStack.pollLast();
                }
            }
        }

        while (!signStack.isEmpty()) {
            String poppedSign = signStack.pollLast();
            postfixList.add(poppedSign);
        }

    }

    /**
     * If the incoming element is a number, push it into the stack (the whole number, not a single digit!).
     * If the incoming element is the name of a variable, push its value into the stack.
     * If the incoming element is an operator, then pop twice to get two numbers and perform the operation; push the result on the stack.
     * When the expression ends, the number on the top of the stack is a final result.
     */
    BigInteger calculate() {
        Deque<BigInteger> calcStack = new ArrayDeque<>();

        BigInteger result = BigInteger.ZERO;
        for (int i = 0; i < postfixList.size(); i++) {
            String curr = postfixList.get(i);
            if (calcStack.size() >= 2 && pemdas.containsKey(curr)) {
                BigInteger num1 = calcStack.pollLast();
                BigInteger num2 = calcStack.pollLast();

                String sign = curr;
                switch (sign) {
                    case "^" -> {
                        assert num2 != null;
                        result = num2.pow(num1.intValue());
                    }
                    case "*" -> {
                        assert num2 != null;
                        result = num2.multiply(num1);
                    }
                    case "/" -> {
                        try {
                            assert num2 != null;
                            result = num2.divide(num1);
                        }catch (ArithmeticException e){
                            hasDivideBy0 = true;
                            System.out.println("A number cannot be divided by zero.");
                        }
                    }
                    case "+" -> {
                        assert num2 != null;
                        result = num2.add(num1);
                    }
                    case "-" -> {
                        assert num2 != null;
                        result = num2.subtract(num1);
                    }
                }
                calcStack.offerLast(result);
            } else {
                if(curr.equals("+") || curr.equals("-")){
                    curr = curr + result;
                }
                calcStack.offerLast(OperandHandler.getValue(curr));
            }
        }

        return calcStack.isEmpty() ? BigInteger.ZERO : calcStack.peekLast();
    }

    static String simplifySign(String inputSign) {

        char outputSign = inputSign.charAt(0);

        for (int i = 1; i < inputSign.length(); i++) {
            if (outputSign == '-' && inputSign.charAt(i) == '-') {
                outputSign = '+';
            } else if (outputSign == '+' && inputSign.charAt(i) == '-') {
                outputSign = '-';
            }
        }

        return String.valueOf(outputSign);
    }
}