import java.util.Scanner;

record BankInfo(String name,String branch) {
}
public class MiniBank {
    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);

        BankInfo bankinfo = new BankInfo("Bank of Baroda","Delhi");

        System.out.println("____________________________________");
        System.out.println("Bank Name : "+bankinfo.name());
        System.out.println("Branch : "+bankinfo.branch());
        System.out.println("____________________________________");

        boolean flag=true;

        while(flag){
            System.out.println("\n1. Open Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Exit");
            System.out.print("Enter your choice (1-5): ");

            int choice=sc.nextInt();

            switch (choice) {
                case 1 -> System.out.println("Open Account — to be implemented in a later lab.");
                case 2 -> System.out.println("Deposit — to be implemented in a later lab.");
                case 3 -> System.out.println("Withdraw — to be implemented in a later lab.");
                case 4 -> System.out.println("Transfer — to be implemented in a later lab.");
                case 5 -> {
                    System.out.println("Thank you for using MiniBank. Goodbye!");
                    flag = false;
                }
            }
        }
        sc.close();
    }

}
