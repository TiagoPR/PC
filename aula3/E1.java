import java.util.HashMap;
import java.util.concurrent.locks.*;
import java.util.*;

class InvalidAccount extends Exception{}
class NotEnoughFunds extends Exception{}

// Guiao 3

class Bank {

    private static class Account{
        Lock l = new ReentrantLock();
        private int balance = 0;
        void deposit(int val) { balance += val; }
        void withdraw(int val) throws NotEnoughFunds {
            if (balance < val) throw new NotEnoughFunds();
            balance -= val; 
        }
    }

    private Map<Integer,Account> acs = new HashMap<>();
    private Lock l = new ReentrantLock();
    private int nextID = 0;

    public int createAccount(int bal){ // temos q dar lock ao banco
        Account ac = new Account();
        ac.deposit(bal);
        l.lock();
        try{
            int id = nextID;
            nextID += 1;
            acs.put(id, ac);
            return id;      
        } finally {  // unlock tem de estar antes do return!!! ou fazemos este try pq evita runtimes exceptions
            l.unlock();
        }
    }

    public int closeAccount(int id) throws InvalidAccount{
        Account c;
        l.lock();
        try{
            c = acs.get(id);
            if (c == null){
                throw new InvalidAccount();
            }
            acs.remove(id);
            c.l.lock();
        }
        finally {
            l.unlock();
        }
        try{
            int bal = c.balance; //ainda tenho a referencia do objeto aqui q depois é coletado pelo garbage collector qnd nao tiver referencias
            return bal;
        } finally {
            c.l.unlock();
        }
    }

    public void deposit(int id, int val) throws InvalidAccount{
        if (id < 0 || id >= acs.length)
            throw new InvalidAccount();
        acs[id].deposit(val);
    }
    public void withdraw(int id, int val) throws InvalidAccount, NotEnoughFunds{
        if (id < 0 || id >= acs.length)
            throw new InvalidAccount();
        acs[id].withdraw(val);
    }

    public int balance(int id) throws InvalidAccount{
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

    public int totalBalance(int accounts[]) throws InvalidAccount{
        int ret = 0;
        for (int i = 0; i < accounts.length; i++) {
            ret += acs[accounts[i]].balance;
        }
        return ret;
    }

    public void transfer(int from, int to, int val) throws InvalidAccount, NotEnoughFunds {
        if (from < 0 || from >= acs.length || to < 0 || to >= acs.length)
            throw new InvalidAccount();
        Account acfrom = acs[from];
        Account acto = acs[to];      // tem de estar em order de maneira a evitar deadlock
        Account c1,c2;

        if (from < to){
            c1 = acfrom;
            c2 = acto;
        } else {
            c2 = acfrom;
            c1 = acto;
        }

        acfrom.withdraw(val);
        acto.deposit(val);
    }
}

class E1 {
    
}
