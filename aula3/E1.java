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
    //private Lock l = new ReentrantLock();
    private ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private int nextID = 0;

    public int createAccount(int bal){ // temos q dar lock ao banco
        Account ac = new Account();
        ac.deposit(bal);
        //l.lock();
        rwl.writeLock().lock();;
        try{
            int id = nextID;
            nextID += 1;
            acs.put(id, ac);
            return id;      
        } finally {  // unlock tem de estar antes do return!!! ou fazemos este try pq evita runtimes exceptions
            //l.unlock();
            rwl.writeLock().unlock();
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
        if (id < 0 || id >= nextID)
            throw new InvalidAccount();
        Account c = acs.get(id);
        c.l.lock();
        try{
            c.deposit(val);
        } finally {
            c.l.unlock();
        }
    }
    public void withdraw(int id, int val) throws InvalidAccount, NotEnoughFunds{
        if (id < 0 || id >= nextID)
            throw new InvalidAccount();
            Account c = acs.get(id);
            c.l.lock();
            try{
                c.withdraw(val);
            } finally {
                c.l.unlock();
            }
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
        accounts = accounts.clone();
        Arrays.sort(accounts);
        int ret = 0;
        Account[] contas = new Account[accounts.length];
        l.lock();
        try{
            for (int i = 0; i < accounts.length; i++) {
                contas[i] = acs.get(accounts[i]);
                if (contas[i] == null)
                    throw new InvalidAccount();
            }
            for (int i = 0; i< accounts.length; i++)
                contas[i].l.lock();
        } finally {
            l.unlock();
        }
        for (Account c : contas) {
            ret += c.balance;
            c.l.unlock();
        }    
        return ret;
    }

    public void transfer(int from, int to, int val) throws InvalidAccount, NotEnoughFunds {
        if (from < 0 || from >= this.nextID || to < 0 || to >= this.nextID)
            throw new InvalidAccount();
        Account acfrom,acto;    
        //l.lock();
        rwl.readLock().lock();
        try{
            acfrom = acs.get(from);
            acto = acs.get(to);      // tem de estar em order de maneira a evitar deadlock
            if (acfrom == null || acto == null){
                throw new InvalidAccount();
            }
            if (from < to){
                acfrom.l.lock();
                acto.l.lock();
            } else {
                acto.l.lock();
                acfrom.l.lock();
            }
        } finally {
            //l.unlock();
            rwl.readLock().unlock();
        }
        try {
            try {
                acfrom.withdraw(val);
            } finally {
                acfrom.l.unlock();
            }
            acto.deposit(val);
        } finally {
            acto.l.unlock();
        }
    }
}

class E1 {
    
}
