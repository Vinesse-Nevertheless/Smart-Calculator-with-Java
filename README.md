# Smart Calculator (Java)

A robust, command-line mathematical expression evaluator capable of handling infinite-precision arithmetic, variable assignment, and complex operator precedence.

## 🚀 Features
- **Infinite Precision:** Utilizes Java's `BigInteger` class to handle numbers of any length without overflow or loss of precision.
- **Variable Support:** Store and retrieve values using custom variable names (e.g., `a = 5`, `b = a * 2`).
- **Full PEMDAS Support:** Implements the **Shunting-Yard Algorithm** to convert infix expressions (standard math) into Postfix notation (Reverse Polish Notation) for accurate order of operations.
- **Advanced Sanitization:** Uses complex **Regex Lookarounds** to parse and format user input, handling irregular spacing and multiple unary operators (e.g., `5 --- 3` correctly resolves to `5 - 3`).
- **Robust Error Handling:** Distinct feedback for invalid identifiers, unknown variables, unbalanced parentheses, and division by zero.

## 🛠️ Technical Challenges & Growth
This project served as a significant milestone in my development, specifically in the following areas:

### 🏗️ Architecture: Building a Compiler Front-end
One of the core challenges was structuring the program to "understand" and "translate" user input. I modeled the logic after a **Compiler Front-end** across four distinct phases:
* **Lexing (Scanning):** Using **Regex Lookarounds** to tokenize "messy" strings (e.g., `a= (5+3)--2`) into a clean list of individual operands and operators.
* **Dispatching:** Creating a central router (`processInput`) to determine if a set of tokens represents a variable assignment or a mathematical expression.
* **Parsing (Syntax Analysis):** Implementing the **Shunting-Yard Algorithm** to verify expression grammar and convert infix notation into an executable Postfix structure.
* **Semantic Analysis:** Ensuring the "meaning" of the code is valid by verifying that variables exist in the `memoryMap` before attempting evaluation.

### 🧠 Algorithm Implementation
My first exposure to the **Shunting-Yard Algorithm**. Navigating the logic of operator stacks and precedence (PEMDAS) was a masterclass in complex state management.

### 🔍 Advanced Regex
I moved beyond basic pattern matching into **Lookarounds**. This allowed for sophisticated string tokenization, ensuring that operators and operands are correctly identified regardless of user formatting.

### 🛡️ System Validation
Significant effort was placed on the validation pipeline. I focused on ensuring the "right error for the right circumstance," which improved the user experience and made the backend logic much more resilient.

## 📦 Requirements
- Java SDK 17 or higher

## 🔧 How to Use
1. Clone the repository.
2. Compile the `Main.java` file.
3. Run the program.
4. Input expressions like:
   ```text
   > a = 123456789012345678901234567890
   > b = 2
   > (a * b) + 10 / (5 - 3)
   `
   
## 📜 Commands
```/help ``` Displays the manual.

```/exit ``` Safely terminates the program.


## License
This project is licensed under the [CC BY-NC 4.0](https://creativecommons.org/licenses/by-nc/4.0/) License - see the LICENSE file for details.

![Java](https://img.shields.io/badge/language-Java-orange)
![License](https://img.shields.io/badge/license-CC%20BY--NC%204.0-blue)
![AI-No-Training](https://img.shields.io/badge/AI-No--Training-red)

