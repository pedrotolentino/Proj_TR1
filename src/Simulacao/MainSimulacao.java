package Simulacao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainSimulacao {
	private static final int PORTA_ENTRADA = 1234;
	private static final int PORTA_SAIDA   = 1235;
	private static final int QTD_PACOTES   = 12;
	
	public static void executarSimulacao(Runnable meio){
		for (int i = 1; i <= 3; i++){
			ExecutorService executor = Executors.newFixedThreadPool(3);
			
			executor.execute(meio);
			
			Maquina comp1 = new Maquina("127.0.0.1", PORTA_ENTRADA, QTD_PACOTES, i);
			Maquina comp2 = new Maquina("127.0.0.2", PORTA_SAIDA, QTD_PACOTES, i);
			
			executor.execute(comp1);
			executor.execute(comp2);
			
			executor.shutdown();
			while(!executor.isTerminated()){
			}
		}
		
		System.out.println("Fim de execução do Protocolo de teste");
		
	}
	
	public static void main(String[] args){
		Canal cabo = new Canal(PORTA_ENTRADA, PORTA_SAIDA, QTD_PACOTES);
		
		executarSimulacao(cabo);
	}
}

