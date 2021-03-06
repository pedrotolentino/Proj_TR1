package Simulacao;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Vector;

public class Canal implements Runnable{
	ServerSocket canalEntrada;
	ServerSocket canalSaida;
	
	public Canal(int portaEntrada, int portaSaida, int qtdPacotes){
		try {
			this.canalEntrada = new ServerSocket(portaEntrada);
			this.canalSaida   = new ServerSocket(portaSaida);
		} catch (IOException e) {
			System.out.println("ERRO ao instanciar o Canal");
			e.printStackTrace();
		}
	}

	public void run() {
		Vector<int []> pacote = null;
		ObjectInputStream entradaMaqEmi = null;
		ObjectOutputStream saidaMaqEmi  = null;
		ObjectInputStream entradaMaqRec = null;
		ObjectOutputStream saidaMaqRec  = null; 
		try {
			//Esperando a conexao das maquinas
			Socket maqEmissora = canalEntrada.accept();
			Socket maqReceptora = canalSaida.accept();
			
			//Realizando a conexao da maquina emissora
			maqEmissora.sendUrgentData(1);
			entradaMaqEmi = new ObjectInputStream(maqEmissora.getInputStream());
			saidaMaqEmi  = new ObjectOutputStream(maqEmissora.getOutputStream());
			saidaMaqEmi.reset();
			saidaMaqEmi.writeObject(true);
			entradaMaqEmi.readObject();
			
			//Realizando a conexao da maquina receptora
			entradaMaqRec = new ObjectInputStream(maqReceptora.getInputStream());
			saidaMaqRec  = new ObjectOutputStream(maqReceptora.getOutputStream());
			saidaMaqRec.reset();
			saidaMaqRec.writeObject(false);
			entradaMaqRec.readObject();
			
			saidaMaqRec.reset();
			saidaMaqRec.writeObject(Constantes.CANAL_PRONTO);
			saidaMaqEmi.reset();
			saidaMaqEmi.writeObject(Constantes.CANAL_PRONTO);
			
			while((Integer)entradaMaqEmi.readObject() != Constantes.FIM_TRANSMISSAO){
				pacote  = (Vector) entradaMaqEmi.readObject();
				for(int i = 0; i < pacote.size(); i++){
					pacote.setElementAt(interferenciaRuido((int[]) pacote.get(i)), i);
				}
				if(!isPctTransferidoComSucesso()){
					Thread.sleep(Constantes.TEMPO_TIME_OUT);
					int[] tOut = {Constantes.TIME_OUT};
 					saidaMaqEmi.reset();
					saidaMaqEmi.writeObject(tOut);
					continue;
				}
				saidaMaqRec.reset();
				saidaMaqRec.writeObject(Constantes.TRANSMISSAO);
				saidaMaqRec.reset();
				saidaMaqRec.writeObject(pacote);
				
				int[] respRec = (int[]) entradaMaqRec.readObject();
				
				if(!isPctTransferidoComSucesso()){
					Thread.sleep(Constantes.TEMPO_TIME_OUT);
					int[] tOut = {Constantes.TIME_OUT};
 					saidaMaqEmi.reset();
					saidaMaqEmi.writeObject(tOut);
					continue;
				}else{
					saidaMaqEmi.reset();
					saidaMaqEmi.writeObject(respRec);
				}
			}
			saidaMaqRec.writeObject(Constantes.FIM_TRANSMISSAO);
		} catch (IOException e) {
			System.out.println("Erro dentro do canal!");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Classe nao encontrada no Canal!");
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.out.println("Thread interrompida no timeout!");
			e.printStackTrace();
		}finally{
			try{
				entradaMaqEmi.close();
				saidaMaqEmi.close();
				entradaMaqRec.close();
				saidaMaqRec.close();
			}catch(Exception e){
				System.out.println("Erro ao finalizar as streams");
			}
		}
	}
	
	private boolean isPctTransferidoComSucesso(){
		Random r = new Random();
		
		//Caso o número aleatório gerado esteja dentro da faixa de probabilidade de perda
		//o pacote não é enviado para o receptor
		if(r.nextInt(101) > Constantes.PROB_PERDA){
			return true;
		}else{
			return false;
		}
	}
	
	private int[] interferenciaRuido(int[] pacote){
		Random r = new Random();
		
		for(int i = 0; i < pacote.length; i++){
			//Caso o número aleatório gerado esteja dentro da faixa de probabilidade de ruído
			//o pacote terá o bit em questão invertido
			if(r.nextInt(101) < Constantes.TAXA_RUIDO){
				pacote[i] = pacote[i] == 0? 1: 0;
			}
		}
		return pacote;
	}
}