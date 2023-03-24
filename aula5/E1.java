import java.util.concurrent.Semaphore;

class BoundedBuffer { // de inteiros
    private Semaphore mut = new Semaphore(1); // exclusao mutua
    private Semaphore items = new Semaphore(0); 
    private Semaphore slots; 
    private int[] fila;
    private int iput = 0;
    private int iget= 0;

    public BoundedBuffer(int N) {
        fila = new int[N];
        slots = new Semaphore(N);
    }
    public int get() throws InterruptedException {
        items.acquire();
        mut.acquire();
        int v = fila[iget];
        iget = (iget+1) % fila.length; // incrementar mas dentro do espaço
        mut.release();
        slots.release();
        return v;
    }
    void put(int x) throws InterruptedException { 
        slots.acquire();
        mut.acquire();
        fila[iput] = x;
        iput = (iput + 1) % fila.length;
        mut.release();
        items.release();
    }
}

class Produtor extends Thread{
    private BoundedBuffer bb;

    Produtor(BoundedBuffer bb){
        this.bb = bb;
    }

    public void run(){
        for (int i = 0; i < 10000; i++) {
            try {
                sleep(1000);
                System.out.println("Vou fazer put de: " + i);
                bb.put(i);
                System.out.println("Put de " + i + " retornou");
            } catch (InterruptedException e) {
                // TODO: handle exception
                System.out.println("Exception");
            }
        }
    }
}

class Consumidor extends Thread{
    private BoundedBuffer bb;

    Consumidor(BoundedBuffer bb){
        this.bb = bb;
    }

    public void run(){
        for (int i = 0; i < 10000; i++) {
            try {
                System.out.println("Vou fazer get");
                int v = bb.get();
                System.out.println("Get retornou " + v );
                sleep(2000);
            } catch (InterruptedException e) {
                // TODO: handle exception
                System.out.println("Exception");
            }
        }
    }
}

class E1 {
    public static void main(String[] args) throws InterruptedException{
        BoundedBuffer bb = new BoundedBuffer(10);

        new Produtor(bb).start();
        new Consumidor(bb).start();

    }
}
    
