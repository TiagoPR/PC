import java.util.concurrent.Semaphore;

class Barreira{
    private Semaphore counter_control = new Semaphore(1); // bloquear o contador
    private Semaphore await_control = new Semaphore(0);; // controlar o await
    private int contador = 0;
    private int limite;
    Barreira (int N){
        this.limite = N;
    }
    void await() throws InterruptedException{
        counter_control.acquire();
        contador++;
        counter_control.release();
        if(contador == limite){
            // return de todas as threads
            for (int i = 0; i < limite; i++) {
                await_control.release();
            }
            counter_control.acquire();
            contador = 0;
            counter_control.release();
        }
        else{
            await_control.acquire();
        }
        System.out.println("Chegamos ao final");
    }
}

class Waiter extends Thread{
    private Barreira b;

    Waiter(Barreira b){
        this.b = b;
    }

    public void run(){
        try {
            System.out.println("Vou fazer await");
            b.await();
        } catch (InterruptedException e) {
            // TODO: handle exception
            System.out.println("Exception");
        }
    }
}

class E3{
    public static void main(String[] args) {
        Barreira b = new Barreira(10);

        for (int i = 0; i < 20; i++) {
            new Waiter(b).start();
        }

        /* Alternativa
        for (int i = 0; i < 20; i++) {
            new Thread(() -> {
                try {
                    Thread.sleep(100);
                    System.out.println("Antes");
                    b.await();
                    System.out.println("Depois");
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }).start();
        }
        */
    }
}