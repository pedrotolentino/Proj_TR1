package Simulacao;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;
import java.util.Vector;

import Protocolos.ProtGoBackN;
import Protocolos.ProtStopAndWait;
import Verificacao.CRC;

public class Maquina implements Runnable{
	Socket  conn;
	int     numPacotes;
	int     tipoProtocolo;
	boolean ehEmissor;
	static final int FIM_TRANSMISSAO  = -1;
	static final int STOP_AND_WAIT    =  1;
	static final int GO_BACK_N        =  2;
	static final int SELECTIVE_REPEAT =  3;	
	static final int NUM_BITS_INFO    =  8;
	static final int NUM_BITS_PACOTE  = 11;
	static final int TAMANHO_JANELA   =  5;
	static final int CANAL_PRONTO     = 10;
	static final int TRANSMISSAO      = 20;
	
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
		ObjectInputStream entradaCanal = null;
		ObjectOutputStream saidaCanal = null;
		try {
			System.out.println("Maquina "+conn.getInetAddress()+" conectada com o servidor na porta "+conn.getPort());
			saidaCanal  = new ObjectOutputStream(conn.getOutputStream());
			entradaCanal = new ObjectInputStream(conn.getInputStream());
			 
			ehEmissor = (boolean)entradaCanal.readObject();
			
			saidaCanal.writeObject(ehEmissor?"Emissora": "Receptora");
			
			if((Integer)entradaCanal.readObject() == CANAL_PRONTO && ehEmissor){
				System.out.println("Emissor conectado e transferindo");
				Thread.sleep(300);
				funcionalidadeEmissor(entradaCanal, saidaCanal);
			}else{
				System.out.println("Receptor conectado e transferindo");
				Thread.sleep(300);
				funcionalidadeReceptor(entradaCanal, saidaCanal);
			}
			
		} catch (IOException e) {
			System.out.println("Erro na maquina: "+e.getMessage());
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Classe nao encontrada: "+e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("ERRO: "+e.getMessage());
			e.printStackTrace();
		}finally{
			try{
				entradaCanal.close();
				saidaCanal.close();
			}catch(Exception e){
				System.out.println("Erro ao finalizar as streams");
			}
		}
		
	}
	
	private void funcionalidadeEmissor(ObjectInputStream in, ObjectOutputStream out) throws Exception{
		CRC crc = new CRC();
		
		switch(tipoProtocolo){
		case STOP_AND_WAIT:
			for(int i = 0; i < numPacotes; i++){
				Vector<Object> v = new Vector<>();
				ProtStopAndWait sw = new ProtStopAndWait();
				
				out.writeObject(TRANSMISSAO);
				v.addElement(crc.encriptar(gerarInformacao()));
				sw.enviarPacote(in, out, v);
			}
			out.writeObject(FIM_TRANSMISSAO);
			break;
		case GO_BACK_N:
			ProtGoBackN bcn = new ProtGoBackN();
			Vector<Object> v = new Vector<>();
			int i;
			for(i = 0; i < numPacotes; i++){
				
				v.addElement(crc.encriptar(gerarInformacao()));
				
				if((i + 1)%TAMANHO_JANELA == 0){
					out.writeObject(TRANSMISSAO);
					bcn.enviarPacote(in, out, v);
					v = new Vector<>();
				}
			}
			if((i + 1)%TAMANHO_JANELA != 0){
				out.writeObject(TRANSMISSAO);
				bcn.enviarPacote(in, out, v);
			}
			out.writeObject(FIM_TRANSMISSAO);
			break;
		case SELECTIVE_REPEAT:
			break;
		default:
			throw new Exception("TIPO DE PROTOCOLO INVÁLIDO!");
		}
	}
	
	private void funcionalidadeReceptor(ObjectInputStream in, ObjectOutputStream out) throws Exception{
		CRC crc = new CRC();
		while((Integer)in.readObject() != FIM_TRANSMISSAO){
			System.out.print("\nRec:             ");
			
			Vector pacote = (Vector) in.readObject();
			int[] retorno = new int[TAMANHO_JANELA];
			
			for(int i = 0; i < pacote.size(); i++){
				if(crc.desencriptar((int[]) pacote.get(i))){
					retorno[i] = 1;
				}else{
					retorno[i] = 0;
				}
				System.out.print(i+1+"o Pacote: ");
				int[] pct = (int[]) pacote.get(i);
				for(int j = 0; j < NUM_BITS_PACOTE; j++){
					System.out.print(pct[j]+" ");;
				}
			}
			out.writeObject(retorno);
		}
	}
	
	private int[] gerarInformacao(){
		int[] info = new int[NUM_BITS_INFO];
		Random r = new Random();
		
		for(int i = 0; i < NUM_BITS_INFO; i++){
			info[i] = r.nextBoolean() == true ? 1: 0;
		}
		
		return info;
	}
}