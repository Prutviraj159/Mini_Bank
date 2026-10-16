# MiniBank - Object-Oriented Banking System

> **Course:** Object Oriented Programming (CEUE203)  
> **Student Roll No:** 25IT045  
> **Environment:** Java 17+ (Tested with JDK 26)  
> **Repository:** `Mini_Bank`  
> **Current Milestone:** Up to Practical 11 (State Persistence, Serialization & NIO Audit Reporting)

---

## 📌 Project Overview
**MiniBank** is a console-based, multi-threaded banking application developed iteratively across Practicals 1 to 14 (Part B). It showcases modern Java software design principles including:
- **Encapsulation & Immutability:** Records, private fields, controlled state modification.
- **Inheritance & Polymorphism:** Abstract `Account` base class with `SavingsAccount`, `CurrentAccount`, and `FixedDepositAccount`.
- **Interface-Driven Design:** `Transactable`, `InterestBearing`, and functional interface `WithdrawRule`.
- **Runtime Metadata & Reflection:** Custom annotations (`@Id`, `@Positive`, `@MaxLength`, `@Audit`) processed at runtime via the Java Reflection API.
- **Fault-Tolerant Error Handling:** Checked exception hierarchy (`BankException`, `InsufficientFundsException`, `InvalidAmountException`, `AccountNotFoundException`).
- **Concurrency & Thread Safety:** Synchronized methods, thread-safe collections (`ConcurrentHashMap`), thread pool execution (`ExecutorService`), and deadlock-free locking order.
- **Persistence & NIO File Operations:** Binary serialization (`ObjectOutputStream`/`ObjectInputStream`), append-only transaction logging, and NIO `DirectoryStream` audit reporting.
- **Collections & Generics:** `HashMap`, `ArrayList`, custom sorting via `Comparator` / `Comparable`, and a generic reflection-based `Repository<T>`.
- **Layered Architecture:** Decoupled Model, Utility, Service, and Presentation layers.

---

## 🗂️ Project Structure

```
Mini_Bank/
├── src/
│   ├── model/
│   │   ├── annotation/
│   │   │   ├── Id.java                  # Practical 7 & 13: Marker annotation for entity key
│   │   │   ├── Positive.java            # Practical 7: Validation annotation for positive amounts
│   │   │   ├── MaxLength.java           # Practical 7: Validation annotation for string lengths
│   │   │   └── Audit.java               # Practical 14: Method audit annotation
│   │   ├── Account.java                 # Practical 2, 3, 5-12: Abstract base account
│   │   ├── SavingsAccount.java          # Practical 5: 4% interest & min balance rule
│   │   ├── CurrentAccount.java          # Practical 5: 0% interest & overdraft limit
│   │   ├── FixedDepositAccount.java     # Practical 5: 7% interest & locked withdrawal
│   │   ├── Customer.java                # Practical 2, 3: Customer with nested Address & Cloneable
│   │   ├── BankInfo.java                # Practical 1: Read-only header record
│   │   ├── MenuOption.java              # Practical 1 & 14: Menu enumeration
│   │   ├── TransactionType.java         # Practical 4: Enum (DEPOSIT, WITHDRAW, TRANSFER)
│   │   ├── Command.java                 # Practical 4: Parsed command record
│   │   ├── Transactable.java            # Practical 6: Transaction capability interface
│   │   ├── InterestBearing.java         # Practical 6: Default interest calculation interface
│   │   ├── Premium.java                 # Practical 6: Marker interface
│   │   └── WithdrawRule.java            # Practical 6 & 7: Functional interface for withdrawal rules
│   ├── exception/
│   │   ├── BankException.java           # Practical 8: Base checked exception
│   │   ├── InsufficientFundsException.java # Practical 8: Carries shortfall amount
│   │   ├── AccountNotFoundException.java   # Practical 8: Missing account error
│   │   └── InvalidAmountException.java     # Practical 8: Negative/zero amount error
│   ├── util/
│   │   ├── Validator.java               # Practical 4: Compiled Pattern regex validation
│   │   ├── CommandParser.java           # Practical 4: String splitting and command parsing
│   │   ├── StatementFormatter.java      # Practical 4: Multi-line statement via StringBuilder
│   │   ├── AnnotationValidator.java     # Practical 7: Reflection validator for custom annotations
│   │   ├── BankSession.java             # Practical 8: AutoCloseable try-with-resources resource
│   │   └── AuditLogger.java             # Practical 14: Reflective method logger
│   ├── service/
│   │   ├── AccountWorker.java           # Practical 9: Runnable task for multithreaded deposits
│   │   ├── TransactionProcessor.java    # Practical 10 & 12: Thread pool & Producer-Consumer buffer
│   │   ├── StatePersister.java          # Practical 11: Binary serialization save/load
│   │   ├── TransactionLog.java          # Practical 11: Append-only transaction logger
│   │   ├── ReportGenerator.java         # Practical 11: NIO DirectoryStream audit summary
│   │   ├── AccountStore.java            # Practical 12: Map storage & Comparator sorting
│   │   ├── Repository.java              # Practical 13: Generic @Id reflection-based repository
│   │   └── BankService.java             # Practical 14: Capstone business logic coordinator
│   ├── test/
│   │   └── MiniBankTest.java            # Practical 14: Comprehensive test suite
│   └── MiniBank.java                    # Practical 1 & 14: Interactive CLI entry point
├── bin/                                 # Compiled bytecode class files
├── data/                                # Runtime persistence directory
│   ├── accounts.dat                     # Serialized accounts
│   ├── logs/transactions.log            # Transaction audit log
│   └── reports/daily_report.txt         # End-of-day summary report
├── minibank.jar                         # Standalone executable JAR
└── README.md
```

---

## 📋 Syllabus Practical Mapping (Practicals 1 to 14 Part B)

| Practical | Topic & Requirements | Implementation in Code |
| :--- | :--- | :--- |
| **Pr 1** | Tools setup, Menu shell, `BankInfo` record, `MenuOption` enum, Scanner & switch expression | `model/BankInfo.java`, `model/MenuOption.java`, `MiniBank.java` |
| **Pr 2** | Encapsulation, `Customer`, `Account`, static counters (`CUST101`, `AC0001`), deposit/withdraw | `model/Customer.java`, `model/Account.java` |
| **Pr 3** | Object overrides (`toString`, `equals`, `hashCode`), static nested `Address`, `Cloneable` | `model/Account.java`, `model/Customer.java` |
| **Pr 4** | Regex `Validator` (mobile, email, PAN, IFSC), `Command` record, `CommandParser`, `StatementFormatter` | `util/Validator.java`, `model/Command.java`, `util/CommandParser.java`, `util/StatementFormatter.java` |
| **Pr 5** | Abstract class `Account`, polymorphism: `SavingsAccount` (4%), `CurrentAccount` (OD), `FixedDepositAccount` (7%) | `model/Account.java`, `model/SavingsAccount.java`, `model/CurrentAccount.java`, `model/FixedDepositAccount.java` |
| **Pr 6** | `Transactable` & `InterestBearing` interfaces, `WithdrawRule` functional interface, `Premium` marker interface, packages | `model/Transactable.java`, `model/InterestBearing.java`, `model/WithdrawRule.java`, `model/Premium.java` |
| **Pr 7** | Standard annotations (`@Override`, `@FunctionalInterface`), custom annotations (`@Id`, `@Positive`, `@MaxLength`), reflection validator | `model/annotation/*`, `util/AnnotationValidator.java` |
| **Pr 8** | Custom exception hierarchy (`BankException`, `InsufficientFundsException` with shortfall), try-with-resources with `AutoCloseable` | `exception/*`, `util/BankSession.java`, `model/Account.java` |
| **Pr 9** | Multithreading with `Runnable`, `join()`, race conditions, synchronization for thread-safe balance updates | `service/AccountWorker.java`, `model/Account.java`, Menu Option 9 |
| **Pr 10** | `TransactionProcessor` (`ExecutorService` pool of 4), Producer-Consumer buffer (`wait()` / `notifyAll()`), deadlock-free lock ordering | `service/TransactionProcessor.java`, `model/Account.java` (`transfer`) |
| **Pr 11** | Serialization (`StatePersister`), append-only logging (`TransactionLog`), NIO directory stream report (`ReportGenerator`) | `service/StatePersister.java`, `service/TransactionLog.java`, `service/ReportGenerator.java` |
| **Pr 12** | Collections framework (`AccountStore`), `sortedByBalance()` via `Comparator`, `Comparable<Account>`, `ConcurrentHashMap` | `service/AccountStore.java`, `model/Account.java`, `service/TransactionProcessor.java` |
| **Pr 13** | Generic `Repository<T>`, key discovery via `@Id` reflection, `@SafeVarargs saveAll()`, bounded method `<N extends Number> average()` | `service/Repository.java`, Menu Option 10 |
| **Pr 14** | Capstone integration (`BankService`), full interactive menu wiring, automated persistence, test suite, and runnable JAR | `service/BankService.java`, `test/MiniBankTest.java`, `minibank.jar` |

---

## 🚀 How to Build and Run

### 1. Compile All Sources
```bash
javac -d bin -sourcepath src src/model/annotation/*.java src/exception/*.java src/model/*.java src/util/*.java src/service/*.java src/test/*.java src/MiniBank.java
```

### 2. Run the Automated Test Suite
```bash
java -cp bin test.MiniBankTest
```
Expected output:
```text
=================================================
          RUNNING MINIBANK TEST SUITE            
=================================================
  [PASS] testDepositIncreasesBalance
  [PASS] testWithdrawalOverLimitThrowsException
  [PASS] testConcurrentDepositsKeepBalanceCorrect
  [PASS] testStatePersistenceAndReload
  [PASS] testGenericRepositoryWithReflection
  [PASS] testInterestCalculation
-------------------------------------------------
Test Results: 6 Passed, 0 Failed.
=================================================
```

### 3. Package into Executable JAR
```bash
jar --create --file minibank.jar --main-class MiniBank -C bin .
```

### 4. Run MiniBank Application
```bash
java -jar minibank.jar
```
Or run directly from bytecode:
```bash
java -cp bin MiniBank
```

---

## 🎯 Interactive Menu Options
1. **Open Account:** Create a Savings, Current, or Fixed Deposit account with customer validation.
2. **Deposit Money:** Add funds to an account (or type `cmd` for CLI parsing).
3. **Withdraw Money:** Withdraw funds; enforces minimum balance and overdraft limits.
4. **Transfer Funds:** Transfer funds safely between accounts with deadlock prevention.
5. **Account Statement:** Print formatted multi-line statement using `StringBuilder`.
6. **List Accounts:** View accounts with option to sort by balance using `Comparator`.
7. **Process Batch Transactions:** Concurrently execute transaction batches on `ExecutorService`.
8. **Generate End-of-Day Audit Report:** Use NIO `DirectoryStream` to calculate totals and generate a report file.
9. **Run Concurrency Demo:** Demonstrate thread safety with 10 threads running 1,000 deposits each.
10. **Run Generic Repository Demo:** Test generic reflection repository and bounded `<N extends Number>` methods.
11. **Save & Exit:** Persist all accounts to `data/accounts.dat` and close sessions safely.

---

## 💡 Viva & Evaluation Highlights

- **Encapsulation:** Private variables with getters enforce business validation inside `deposit()` and `withdraw()`, preventing illegal external mutations.
- **Dynamic Method Dispatch:** When calling `interestRate()` or `canWithdraw()` on an `Account` reference, Java executes the subclass implementation (`SavingsAccount`, `CurrentAccount`, or `FixedDepositAccount`) at runtime.
- **Deadlock Prevention:** The `transfer()` method avoids circular wait by ordering lock acquisition based on the lexicographical comparison of account numbers (`a1.compareTo(a2)`).
- **Reflection & Generics:** The generic `Repository<T>` dynamically extracts the field annotated with `@Id` at runtime to index objects inside a `ConcurrentHashMap` without hardcoded type coupling.
- **Persistence & NIO:** The system uses `ObjectOutputStream` for state persistence and Java NIO `DirectoryStream` with `Files.readAttributes` for file inspection and reporting.
