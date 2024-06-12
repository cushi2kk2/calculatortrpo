import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;

public class Calculator extends JFrame implements ActionListener {
    private JTextField display;

    private String currentInput = "";
    private Stack<Double> values = new Stack<>();
    private Stack<Character> operations = new Stack<>();

    public Calculator() {
        setTitle("Calculator");
        setSize(400, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        display = new JTextField();
        display.setEditable(false);
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setFont(new Font("Arial", Font.BOLD, 32));
        add(display, BorderLayout.NORTH);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 4, 10, 10));

        String[] buttonLabels = {
                "(", ")", "C", "/",
                "7", "8", "9", "*",
                "4", "5", "6", "-",
                "1", "2", "3", "+",
                "0", ".", "=", "^",
                "sqrt", "sin", "cos", "±"
        };

        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.setFont(new Font("Arial", Font.BOLD, 24));
            button.addActionListener(this);
            panel.add(button);
        }

        add(panel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        // Clear display
        if (command.charAt(0) == 'C') {
            currentInput = "";
            display.setText(currentInput);
        }
        // Calculate result
        else if (command.charAt(0) == '=') {
            calculate();
        }
        // Square root operation
        else if (command.equals("sqrt")) {
            if (!currentInput.isEmpty()) {
                double value = Math.sqrt(Double.parseDouble(currentInput));
                currentInput = String.valueOf(value);
                display.setText(currentInput);
            }
        }
        // Sine operation
        else if (command.equals("sin")) {
            if (!currentInput.isEmpty()) {
                double value = Math.sin(Math.toRadians(Double.parseDouble(currentInput)));
                currentInput = String.valueOf(value);
                display.setText(currentInput);
            }
        }
        // Cosine operation
        else if (command.equals("cos")) {
            if (!currentInput.isEmpty()) {
                double value = Math.cos(Math.toRadians(Double.parseDouble(currentInput)));
                currentInput = String.valueOf(value);
                display.setText(currentInput);
            }
        }
        // Unary minus operation
        else if (command.equals("±")) {
            if (!currentInput.isEmpty()) {
                double value = -Double.parseDouble(currentInput);
                currentInput = String.valueOf(value);
                display.setText(currentInput);
            }
        }
        // Handle operators and parentheses
        else if ("+-*/^()".indexOf(command.charAt(0)) >= 0) {
            currentInput += " " + command + " ";
            display.setText(currentInput);
        }
        // Handle numbers and decimal point
        else {
            currentInput += command;
            display.setText(currentInput);
        }
    }

    // Method to calculate the result of the expression
    private void calculate() {
        try {
            String[] tokens = currentInput.split(" ");
            Stack<Double> values = new Stack<>();
            Stack<Character> operations = new Stack<>();

            // Process each token
            for (int i = 0; i < tokens.length; i++) {
                String token = tokens[i];
                if (token.isEmpty()) continue;
                if (isOperator(token.charAt(0))) {
                    // Apply operations with higher or equal precedence
                    while (!operations.isEmpty() && hasPrecedence(token.charAt(0), operations.peek())) {
                        values.push(applyOp(operations.pop(), values.pop(), values.pop()));
                    }
                    operations.push(token.charAt(0));
                } else if (token.charAt(0) == '(') {
                    operations.push('(');
                } else if (token.charAt(0) == ')') {
                    while (operations.peek() != '(') {
                        values.push(applyOp(operations.pop(), values.pop(), values.pop()));
                    }
                    operations.pop();
                } else {
                    values.push(Double.parseDouble(token));
                }
            }

            // Apply remaining operations
            while (!operations.isEmpty()) {
                values.push(applyOp(operations.pop(), values.pop(), values.pop()));
            }

            currentInput = String.valueOf(values.pop());
            display.setText(currentInput);
        } catch (Exception e) {
            display.setText("Error");
        }
    }

    // Method to check if a character is an operator
    private boolean isOperator(char op) {
        return op == '+' || op == '-' || op == '*' || op == '/' || op == '^';
    }

    // Method to determine operator precedence
    private boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')')
            return false;
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-'))
            return false;
        if (op1 == '^' && (op2 != '^'))
            return false;
        return true;
    }

    // Method to apply an operation to two operands
    private double applyOp(char op, double b, double a) {
        switch (op) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/': if (b == 0) throw new ArithmeticException("Cannot divide by zero");
                return a / b;
            case '^': return Math.pow(a, b);
        }
        return 0;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Calculator calculator = new Calculator();
            calculator.setVisible(true);
        });
    }
}
