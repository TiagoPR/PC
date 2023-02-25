public class Thre extends Thread{
    public void run() {
        System.out.println("Hello from a thread!");
    }

    public static void main(String args[]) {
        (new Thre()).start();
    }
}
