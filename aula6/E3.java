class Barreira{
    private int contador = 0;
    private int limite;
    private int sairam = 0;
    private int e = 0; // alternativa b)

    Barreira (int N){
        this.limite = N;
    }
    public synchronized void await() throws InterruptedException{
        // a)
        /* 
        contador++;
        if (contador < limite) {
            while( contador < limite) // estar em ciclo caso haja uma "libertação" do wait
                wait();
        }
        else{
            notifyAll();
        } 
        */

        // b)

        /* 

        // while(contador == limite) // prevencao de thread adiantada
        // ou

        while (sairam != 0){
            wait();
        }


        contador++;
        if (contador < limite) {
            while( contador < limite)
                wait();
        }
        else{
            notifyAll();
        }

        sairam += 1;

        if (sairam == limite){
            contador = 0;
            sairam = 0;
            notifyAll(); // libertar a thread adiantada
        }
        */

        // b) ALTERNATIVA (mudança de época)
        
        contador++;
        if (contador < limite) {
            final int atual = e;
            while(e == atual)
                wait();
        }
        else{
            notifyAll();
            contador = 0;
            e += 1;
        } 

    }
}

class E3{
    public static void main(String[] args) {
        Barreira b = new Barreira(10);

        for (int i = 0; i < 10; i++) {
            int j = i;
            new Thread(() -> {
                try {
                    while(true){
                        Thread.sleep(j * 1000);
                        System.out.println("Antes");
                        b.await();
                        System.out.println("Depois");
                }
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }).start();
        }
    }
}
