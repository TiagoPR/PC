class Aula extends Thread {
    public void run(){
        for (int i = 0; i < 10; i++) {
            System.out.println("Print na thread");
        }
    }
}

class MyRun implements Runnable {

    public void run() {
        try {
            //Thread.sleep(1000);
            System.out.println("Print na thread 2");    
        } catch (Exception e) {
            // TODO: handle exception
        }
        
        
    }
}

class Main {
    public static void main(String[] args) throws InterruptedException{
        Aula t = new Aula();
        // t.run(); <- nunca fazer
        t.start();

        // MyRun r = new MyRun();       // Em vez de implementar-mos desta maneira temos uma maneira mais rapida mostrada em baixo
        // Thread t2 = new Thread(r);
        // t2.start();

        // Thread t3 = new Thread(r);
        // t3.start();

        Thread t2 = new Thread(new MyRun());
        t2.start();

        System.out.println("Print no main");
        t.join(); // esperar que a thread termine
        t2.join();
        

        System.out.println("Print no fim main");
    }
}
