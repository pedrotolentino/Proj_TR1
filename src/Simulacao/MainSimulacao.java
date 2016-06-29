package Simulacao;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainSimulacao {
	
	private static Maquina comp1 = null;
	private static Maquina comp2 = null;
	
	private static Collection<String> logs;
	
	private static final int PORTA_ENTRADA = 1234;
	private static final int PORTA_SAIDA   = 1235;
	private static final int QTD_PACOTES   = 1000;
	
	public static void executarSimulacao(Runnable meio){
		for (int i = 1; i <= 3; i++){
			ExecutorService executor = Executors.newFixedThreadPool(3);
			
			executor.execute(meio);
			
			comp1 = new Maquina("127.0.0.1", PORTA_ENTRADA, QTD_PACOTES, i);
			comp2 = new Maquina("127.0.0.2", PORTA_SAIDA, QTD_PACOTES, i);
			
			executor.execute(comp1);
			executor.execute(comp2);
						
			executor.shutdown();
			while(!executor.isTerminated()){
			}
		}		
	}
	
	public static void main(String[] args){
		Canal cabo = new Canal(PORTA_ENTRADA, PORTA_SAIDA, QTD_PACOTES);
		
		executarSimulacao(cabo);
		
		System.out.println("## O PROCESSO FOI EXECUTADO COM SUCESSO, ENVIANDO "+QTD_PACOTES+" PACOTES");
		
	}
}

