class Printer extends Thread{
    final int I;
    final int n;

    Printer(int I,int n){
        this.I = I;
        this.n = n;
    }

    public void run(){
        for (int i = 1; i <= I; i++) {
            System.out.println("Thread" + n + " escreve " + i);
        }
    }
}

class E1 {
    public static void main(String[] args) throws InterruptedException{
        final int N = Integer.parseInt(args[0]);
        final int I = Integer.parseInt(args[1]);
        
        Thread[] prints = new Thread[N];

        for (int j = 0; j < N; j++) {
            // new Printer(I, j).start(); mas não dá para esperar
            prints[j] = new Printer(I, j+1);
        }

        for (int j = 0; j < N; j++) {
            prints[j].start();
        }

        for (int j = 0; j < N; j++) {
            prints[j].join();
        }

    }
}
