import java.util.*;
import java.util.concurrent.locks.*;

class Warehouse{

    Lock l = new ReentrantLock();
    
    private class Product{
        int quant = 0;
        Condition c = l.newCondition();
    }


    private Map<String,Product> items = new HashMap<>();

    private Product get(String name){
        Product p = items.get(name);
        if (p == null)
            p = new Product();
            items.put(name,p);
        return p;
    }

    public void supply(String item, int quantity){
        l.lock();
        try {
            Product p = items.get(item);
            p.quant += quantity;
            p.c.signalAll();
        } finally {
            l.unlock();
        }
    }

    private Product missing(Product[] its){
        for(Product i : its){
            if (i.quant == 0){
                return i;
            }
        }
        return null;
    }


    public void consume(String[] itens) throws InterruptedException{
        l.lock();
        try {
            for(String item : itens){
                Product p = items.get(item);
                while(p.quant == 0){
                    p.c.await(); // ao entrar no await liberta o lock e ao sair readquire
                }
                p.quant--;
            }
        } finally {
            l.unlock();
        }

        // ou 

        Product[] its = new Product[itens.length];
        for(int i = 0; i< itens.length; i++)
            its[i] = get(itens[i]);

        while(true){
            Product i = missing(its);
            if (i == null)
                break;
            i.c.await();
        }

        for(Product i : its)
            i.quant -= 1;

        // ou

        Product[] its = new Product[itens.length];
        for(int i = 0; i< itens.length; i++)
            its[i] = get(itens[i]);

        for(int i = 0; i< its.length;){
            if(its[i].quant == 0){
                its[i].c.await();
                i = 0;
            } else {
                i += 1;
            }
        }

        for(Product i : its)
            i.quant -= 1;
    }


}