package FontesTeste;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClasseTesteMain {
	public static void testeExecutarProtocolo(Runnable meio){
		ExecutorService executor = Executors.newFixedThreadPool(3);
		
		executor.execute(meio);
	}
	
	public static void main(String[] args){
		Canal cabo = new Canal();
		
		testeExecutarProtocolo(cabo);
	}
	
	
}
