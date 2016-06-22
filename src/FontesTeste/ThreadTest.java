package FontesTeste;

import java.util.List;

public class ThreadTest {

	static int i = 0;
	public static void main(String[] args) {
		new Thread(t1).start();
		new Thread(t2).start();
	}	

	private static void countMe(String name){
		i++;
		System.out.println("Current Counter is: " + i + ", updated by: " + name);
	}

	private static Runnable t1 = new Runnable() {
		public void run() {
			try{
				for(int i=0; i<5; i++){
					countMe("t1");
				}
			} catch (Exception e){}

		}
	};

	private static Runnable t2 = new Runnable() {
		public void run() {
			try{
				for(int i=0; i<5; i++){
					countMe("t2");
				}
			} catch (Exception e){}
		}
	};
	
//	public void calculaTotalRecebido(){
//		new Thread() {
//			
//			@Override
//			public void run() {
//				//Recebe aproximadamente 70mil registros. 
//				List<Long> numeros = 
//				Long soma = 0l;
//				
//				for(Long n: numeros){
//					soma = soma + n;
//				}
//				
//				System.out.println(soma);
//				
//			}
//		}.start();
//
//	}
}

