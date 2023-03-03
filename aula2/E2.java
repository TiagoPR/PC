import java.util.Random;

class InvalidAccount extends Exception{}
class NotEnoughFunds extends Exception{}

class Bank {
    private static class Account{
        int balance = 0;
        synchronized void deposit(int val) { balance += val; }
        synchronized void withdraw(int val) throws NotEnoughFunds {
            if (balance < val) throw new NotEnoughFunds();
            balance -= val; 
        }
    }

    Account[] acs;

    Bank(int N) {
        acs = new Account[N];
        for (int i = 0; i < acs.length; i++) {
            acs[i] = new Account();
        }
    }
    public synchronized void deposit(int id, int val) throws InvalidAccount{
        if (id < 0 || id >= acs.length)
            throw new InvalidAccount();
        acs[id].deposit(val);
    }
    public synchronized void withdraw(int id, int val) throws InvalidAccount, NotEnoughFunds{
        if (id < 0 || id >= acs.length)
            throw new InvalidAccount();
        acs[id].withdraw(val);
    }

    public synchronized int balance(int id) throws InvalidAccount{
        if (id < 0 || id >= acs.length)
            throw new InvalidAccount();
        return acs[id].balance;
    }

    int totalBalance(){
        int ret = 0;
        for (int i = 0; i < acs.length; i++) {
            ret += acs[i].balance;
        }
        return ret;
    }

    public synchronized int totalBalance(int accounts[]) throws InvalidAccount{
        int ret = 0;
        for (int i = 0; i < accounts.length; i++) {
            ret += acs[accounts[i]].balance;
        }
        return ret;
    }

    public synchronized void transfer(int from, int to, int val) throws InvalidAccount, NotEnoughFunds {
        withdraw(from, val);
        deposit(to, val);
    }
}

class E2{
    public static void main(String[] args) {
        final int C = Integer.parseInt(args[0]);
        final int V = Integer.parseInt(args[1]);
        Bank b = new Bank(C);

        for (int i = 0; i < C; i++) {
            try {
                b.deposit(i, V);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        System.out.println(b.totalBalance());

        int[] ids = new int[C];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = i;
        }
        
        new Thread(() -> {
            Random r = new Random();
            try{
                while(true){
                    int from = r.nextInt(C);
                    int to = r.nextInt(C);
                    b.transfer(from, to, 100);
                }
            }
            catch (Exception e){
                System.out.println("fim de thread transferencia");
            }
        }).start();

        new Thread(() -> {
            try {
                while(true){
                    int total = b.totalBalance(ids);
                    
                    if(total != C * V) {
                        System.out.println("valor errado");
                    }
                }
            } catch (Exception e) {
                System.out.println("fim de thread print total");
            }
        }).start();;
    }
}
