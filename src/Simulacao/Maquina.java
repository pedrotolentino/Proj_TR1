package Simulacao;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;
import java.util.Vector;

import Protocolos.ProtStopAndWait;

public class Maquina implements Runnable{
	Socket  conn;
	int     numPacotes;
	int     tipoProtocolo;
	boolean ehEmissor;
	public static final int FIM_TRANSMISSAO  = -1;
	public static final int STOP_AND_WAIT    =  1;
	public static final int GO_BACK_N        =  2;
	public static final int SELECTIVE_REPEAT =  3;	
	public static final int NUM_BITS_INFO    =  8;
	public static final int NUM_BITS_PACOTE  = 11;
	public static final int CANAL_PRONTO     = 10;
	public static final int TRANSMISSAO      = 20;
	
	public Maquina(String ip, int porta, int qtdPacotes, int protocolo){
		try {
			this.numPacotes    = qtdPacotes;
			this.tipoProtocolo = protocolo;
			this.conn = new Socket(ip, porta);
		} catch (IOException e) {
			System.out.println("Erro ao realizar conexao da maquina!");
			e.printStackTrace();
		}
	}
	
	public void run(){
		try {
			System.out.println("Maquina "+conn.getInetAddress()+" conectada com o servidor na porta "+conn.getPort());
			ObjectOutputStream saidaCanal  = new ObjectOutputStream(conn.getOutputStream());
			ObjectInputStream entradaCanal = new ObjectInputStream(conn.getInputStream());
			 
			ehEmissor = (boolean)entradaCanal.readObject();
			
			saidaCanal.writeObject(ehEmissor?"Emissora": "Receptora");
			
			if((Integer)entradaCanal.readObject() == CANAL_PRONTO && ehEmissor){
				System.out.println("Emissor conectado e transferindo");
				funcionalidadeEmissor(entradaCanal, saidaCanal);
			}else{
				System.out.println("Receptor conectado e transferindo");
				funcionalidadeReceptor(entradaCanal, saidaCanal);
			}
			
		} catch (IOException e) {
			System.out.println("Erro na maquina!");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Classe nao encontrada!");
			e.printStackTrace();
		}
		
	}
	
	private void funcionalidadeEmissor(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException{
		for(int i = 0; i < numPacotes; i++){
			Vector<Object> v = new Vector<>();
			ProtStopAndWait sw = new ProtStopAndWait();
			out.writeObject(TRANSMISSAO);
			v.addElement(gerarInformacao());
			sw.enviarPacote(in, out, v);
		}
		out.writeObject(FIM_TRANSMISSAO);
	}
	
	private void funcionalidadeReceptor(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException{
		while((Integer)in.readObject() != FIM_TRANSMISSAO){
//			System.out.println(" Rec: "+in.readObject());
			System.out.print(" Rec: ");
			Vector pacote = (Vector) in.readObject();
			for(int i = 0; i < pacote.size(); i++){
				int[] pct = (int[]) pacote.get(i);
				for(int j = 0; j < 8; j++){
					System.out.print(pct[j]+" ");;
				}
			}
			out.writeObject("ACK");
		}
	}
	
	public int[] gerarInformacao(){
		int[] info = new int[NUM_BITS_INFO];
		Random r = new Random();
		
		for(int i = 0; i < NUM_BITS_INFO; i++){
			info[i] = r.nextBoolean() == true ? 1: 0;
		}
		
		return info;
	}
}