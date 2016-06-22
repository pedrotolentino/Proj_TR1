package FontesTeste;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClasseTesteMain {
	private static final int PORTA_ENTRADA = 1234;
	private static final int PORTA_SAIDA   = 1235;
	private static final int QTD_PACOTES   = 10;
	
	public static void testeExecutarProtocolo(Runnable meio){
		ExecutorService executor = Executors.newFixedThreadPool(3);
		
		executor.execute(meio);
		
		Maquina comp1 = new Maquina("127.0.0.1", PORTA_ENTRADA, QTD_PACOTES);
		Maquina comp2 = new Maquina("127.0.0.2", PORTA_SAIDA, QTD_PACOTES);
		
		executor.execute(comp1);
		executor.execute(comp2);
		
		executor.shutdown();
		while(!executor.isTerminated()){
		}
		
		System.out.println("Fim de execução do Protocolo de teste");
		
	}
	
	public static void main(String[] args){
		Canal cabo = new Canal(PORTA_ENTRADA, PORTA_SAIDA, QTD_PACOTES);
		
		testeExecutarProtocolo(cabo);
	}
	
	
}
