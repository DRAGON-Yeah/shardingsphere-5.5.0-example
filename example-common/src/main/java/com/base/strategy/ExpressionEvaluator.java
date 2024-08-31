package com.base.strategy;

import java.util.Stack;

/**
 * @title: ExpressionEvaluator
 * @description: 字符串计算
 * @author: arron
 * @date: 2024/8/31 11:53
 */
public class ExpressionEvaluator {

    public static int evaluateExpression(String expression) {
        Stack<Integer> numbers = new Stack<>();
        Stack<Character> operators = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);

            // 如果是数字，连续读取所有数字并转换为整数
            if (Character.isDigit(ch)) {
                StringBuilder sb = new StringBuilder();
                while (i < expression.length() && Character.isDigit(expression.charAt(i))) {
                    sb.append(expression.charAt(i++));
                }
                numbers.push(Integer.parseInt(sb.toString()));
                i--; // 回退一位，因为循环会自动增加 i
            } else if ("*/%+-".indexOf(ch) >= 0) { // 如果是运算符
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(ch)) {
                    compute(numbers, operators);
                }
                operators.push(ch);
            }
        }

        // 计算剩余的运算符
        while (!operators.isEmpty()) {
            compute(numbers, operators);
        }

        // 最终结果在栈顶
        return numbers.pop();
    }

    private static void compute(Stack<Integer> numbers, Stack<Character> operators) {
        int b = numbers.pop();
        int a = numbers.pop();
        char op = operators.pop();

        switch (op) {
            case '+':
                numbers.push(a + b);
                break;
            case '-':
                numbers.push(a - b);
                break;
            case '*':
                numbers.push(a * b);
                break;
            case '/':
                numbers.push(a / b);
                break;
            case '%':
                numbers.push(a % b);
                break;
            default:
                throw new IllegalArgumentException("Unsupported operator: " + op);
        }
    }

    private static int precedence(char operator) {
        switch (operator) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
            case '%':
                return 2;
            default:
                return -1;
        }
    }
}
