import java.applet.Applet;
import java.awt.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

public class Main extends Applet implements Runnable, KeyListener {
	private final int speed = 10;
	private final int death_x = 200;
	private final int spawn_time = 50;
	//private int[] score = new int[2];
	private int score = 0;
	private int combo = 0;
	private String msg = "";
	private int rest_time = 30;
	private int time_count = 0;
	
	private Thread gameThread;
	private Taiko taiko;
	private LinkedList<Taiko> taiko_task;
	private Iterator<Taiko> taiko_iter;
	
	private int taiko_count;
	private boolean taiko_wait;
	private int wait_time;
		
	public void init(){
		setFocusable(true);
        setBackground(Color.black);
        setForeground(Color.white);
        
        taiko_count = 0;
        taiko_wait = false;
        taiko_task = new LinkedList<Taiko>();
        taiko_iter = taiko_task.iterator();
        wait_time = 500;
               
        addKeyListener(this);
	}
	
	public void start(){
		gameThread = new Thread(this);
        gameThread.start();
	}
	
	public void stop(){
		gameThread = null;
	}
	
	public void run(){
		while(gameThread == Thread.currentThread()){
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            //終了判定
            if(rest_time > 0){
            	moveTaiko();
            	countDown();
            }
            repaint();
        }
	}
	
	public void countDown(){
		time_count++;
		if(time_count >= 50){
			rest_time--;
			time_count = 0;
		}
	}
	
	public void paint(Graphics g){
		g.drawImage(getImage(getDocumentBase(), "./image/frame0.png"), 0, 50, this);
		g.drawImage(getImage(getDocumentBase(), "./image/frame1.png"), 500, 50, this);
		g.drawImage(getImage(getDocumentBase(), "./image/frame1.png"), 1000, 50, this);
		g.drawImage(getImage(getDocumentBase(), "./image/frame2.jpg"), 0, 470, 1300, 600, this);
		
		//的を描画
		g.drawImage(getImage(getDocumentBase(), "./image/hit.png"), death_x, 50, this);
		
		//存在している太鼓を全て描画
		taiko_iter = taiko_task.iterator();
		while(taiko_iter.hasNext()){
			Taiko t = taiko_iter.next();
			g.drawImage(t.taiko_img, t.taiko_x, t.taiko_y, this);
		}
		
		//スコアの描画
		g.setFont(new Font("SansSerif", Font.BOLD, 60));
		g.drawString(String.valueOf(score), 1000, 100);
		
		//コンボの描画
		g.setFont(new Font("SansSerif", Font.BOLD, 80));
		g.setColor(Color.red);
		g.drawString(String.valueOf(combo), 140, 310);
		g.setColor(Color.white);
		
		//メッセージの描画
		g.setFont(new Font("SansSerif", Font.BOLD, 30));
		g.drawString(msg, 400, 200);
		
		//カウンドダウンの描画
		g.setFont(new Font("SansSerif", Font.BOLD, 60));
		g.drawString(String.valueOf(rest_time), 600, 100);
		
		g.dispose();
	}
	
	public void moveTaiko(){
		//太鼓をランダム間隔で発生
		if(!taiko_wait){
			createTaiko();
			taiko_wait = true;
			Random rnd = new Random();
			wait_time = rnd.nextInt(spawn_time);
		}
		
		if(taiko_count >= wait_time){
			taiko_wait = false;
			taiko_count = 0;
		}
		
		if(taiko_wait){
			taiko_count ++;
		}
		
		//存在している全ての太鼓を移動
		taiko_iter = taiko_task.iterator();
		while(taiko_iter.hasNext()){
			Taiko t = taiko_iter.next();
			if(t.taiko_x < death_x){
				taiko_iter.remove();
			}else{
				t.taiko_x -= speed;
			}
		}
	}
	
	public void createTaiko(){
		// 太鼓をインスタンス化して追加
		taiko = new Taiko();
		taiko.taiko_x = 1500;
		taiko.taiko_y = 50;
	 	if(Math.random() > 0.5){
	 		taiko.taiko_type = "red";
			taiko.taiko_img = getImage(getDocumentBase(), "./image/taiko_r.png");
		}else{
			taiko.taiko_type ="blue";
			taiko.taiko_img = getImage(getDocumentBase(), "./image/taiko_b.png");
		}
		taiko_task.add(taiko);
	}
	
	public void keyPressed(KeyEvent e){}

    public void keyReleased(KeyEvent e){
        switch(e.getKeyCode()){
        case KeyEvent.VK_ENTER:
        	onCollision("red");
            break;
        case KeyEvent.VK_SPACE:
        	onCollision("blue");
            break;
        }
    }
    
    public void keyTyped(KeyEvent e){}
    
    public void onCollision(String type){
    	taiko_iter = taiko_task.iterator();
    	Taiko t = taiko_iter.next();
    	//System.out.print(t.taiko_x);
    	if(type == t.taiko_type){
    		if(t.taiko_x > death_x+100 && t.taiko_x <= death_x+100){
    			msg = "Normal";
    			score += 1000;
    			combo++;
    		}else if(t.taiko_x > death_x+50 && t.taiko_x <= death_x+100){
    			msg = "Good";
    			score += 1500;
    			combo++;
    		}else if(t.taiko_x >= death_x && t.taiko_x <= death_x+50){
    			msg = "Super";
    			score += 2000;
    			combo++;
    		}else{
    			msg = "Miss";
    			combo = 0;
    		}
    	}else{
    		msg = "Miss";
    		combo = 0;
    	}
    }

}
