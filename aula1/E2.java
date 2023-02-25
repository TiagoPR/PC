class Counter{
    private int valor = 0;  // tem de estar encapsulado para haver concorrência

    public int getValor(){
        return this.valor;
    }

    public void incrementa(){
        this.valor += 1;
    }
}

class Incrementer extends Thread{
    final int I;
    final Counter c;

    Incrementer(int I ,Counter c){
        this.I = I;
        this.c = c;
    }
    public void run(){
        for (int i = 0; i < I; i++) {
            c.incrementa();
            //c.valor += 1;
        }
    }
}


class E2 {
    public static void main(String[] args) throws InterruptedException{
        final int N = Integer.parseInt(args[0]);
        final int I = Integer.parseInt(args[1]);

        Counter c = new Counter();

        Thread[] a = new Thread[N];

        for (int j = 0; j < N; j++) {
            a[j] = new Incrementer(I, c);
        }
        
        // Possível alternativa com função anónima
        // for (int j = 0; j < N; j++) {
        //     a[j] = new Thread(() -> {
        //         for (int j2 = 0; j2 < I; j2++) {
        //             c.incrementa();
        //         }
        //     });
        // }

        for (int j = 0; j < N; j++) {
            a[j].start();
        }

        for (int j = 0; j < N; j++) {
            a[j].join();
        }

        System.out.println(c.getValor());

    }
}
