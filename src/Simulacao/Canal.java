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
	static final int FIM_TRASMISSAO = -1;
	static final int CANAL_PRONTO   = 10;
	static final int TRANSMISSAO    = 20;
	static final int PROB_PERDA     = 50;
	static final int TAXA_RUIDO     = 1;
	
	public Canal(int portaEntrada, int portaSaida, int qtdPacotes){
		try {
			this.canalEntrada = new ServerSocket(portaEntrada);
			this.canalSaida   = new ServerSocket(portaSaida);
			System.out.println("Entrada do canal escutando na porta "+canalEntrada.getLocalPort());
			System.out.println("Saida do canal escutando na porta "+canalSaida.getLocalPort());
			
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
		System.out.println("Aguardando conexao dos clientes...");
		try {
			//Esperando a conexao das maquinas
			Socket maqEmissora = canalEntrada.accept();
			Socket maqReceptora = canalSaida.accept();
			
			//Realizando a conexao da maquina emissora
			maqEmissora.sendUrgentData(1);
			entradaMaqEmi = new ObjectInputStream(maqEmissora.getInputStream());
			saidaMaqEmi  = new ObjectOutputStream(maqEmissora.getOutputStream());
			saidaMaqEmi.writeObject(true);
			System.out.println("Maquina "+entradaMaqEmi.readObject()+" conectada com o canal ");
			
			//Realizando a conexao da maquina receptora
			entradaMaqRec = new ObjectInputStream(maqReceptora.getInputStream());
			saidaMaqRec  = new ObjectOutputStream(maqReceptora.getOutputStream());
			saidaMaqRec.writeObject(false);
			System.out.println("Maquina "+entradaMaqRec.readObject()+" conectada com o canal");
			
			saidaMaqRec.writeObject(CANAL_PRONTO);
			saidaMaqEmi.writeObject(CANAL_PRONTO);
			
			while((Integer)entradaMaqEmi.readObject() != FIM_TRASMISSAO){
				pacote  = (Vector) entradaMaqEmi.readObject();
				System.out.print(" Canal -> ");
				for(int i = 0; i < pacote.size(); i++){
					System.out.print(i+1+"o Pacote: ");
					int[] pct = (int[]) pacote.get(i);
					for(int j = 0; j < 11; j++){
						System.out.print(pct[j]+" ");;
					}
					pacote.setElementAt(interferenciaRuido((int[]) pacote.get(i)), i);
				}
				saidaMaqRec.reset();
				saidaMaqRec.writeObject(TRANSMISSAO);
				saidaMaqRec.reset();
				saidaMaqRec.writeObject(pacote);
				saidaMaqEmi.reset();
				saidaMaqEmi.writeObject(entradaMaqRec.readObject());
			}
			saidaMaqRec.writeObject(FIM_TRASMISSAO);
		} catch (IOException e) {
			System.out.println("Erro dentro do canal!");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Classe nao encontrada no Canal!");
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
		if(r.nextInt(101) > PROB_PERDA){
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
			if(r.nextInt(101) < TAXA_RUIDO){
				pacote[i] = pacote[i] == 0? 1: 0;
			}
		}
		return pacote;
	}
}