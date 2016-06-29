package Simulacao;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;
import java.util.Vector;

import Protocolos.ProtGoBackN;
import Protocolos.ProtSelectiveRepeat;
import Protocolos.ProtStopAndWait;
import Verificacao.CRC;

public class Maquina implements Runnable{
	Socket  conn;
	int     numPacotes;
	int     tipoProtocolo;
	boolean ehEmissor;
	
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
			
			if((Integer)entradaCanal.readObject() == Constantes.CANAL_PRONTO && ehEmissor){
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
		case Constantes.STOP_AND_WAIT:
			for(int i = 0; i < numPacotes; i++){
				Vector<int []> v = new Vector<>();
				ProtStopAndWait sw = new ProtStopAndWait();
				
				v.addElement(crc.encriptar(gerarInformacao()));
				sw.enviarPacote(in, out, v);
			}
			out.writeObject(Constantes.FIM_TRANSMISSAO);
			break;
		case Constantes.GO_BACK_N:
			ProtGoBackN bcn = new ProtGoBackN();
			Vector<int []> v = new Vector<>();
			int i;
			for(i = 0; i < numPacotes; i++){
				
				v.addElement(crc.encriptar(gerarInformacao()));
				
				if((i + 1)%Constantes.TAMANHO_JANELA == 0){
					bcn.enviarPacote(in, out, v);
					v = new Vector<>();
				}
			}
			if((i + 1)%Constantes.TAMANHO_JANELA != 0 && !v.isEmpty()){
				bcn.enviarPacote(in, out, v);
			}
			out.reset();
			out.writeObject(Constantes.FIM_TRANSMISSAO);
			break;
		case Constantes.SELECTIVE_REPEAT:
			ProtSelectiveRepeat sr = new ProtSelectiveRepeat();
			Vector<int []> ve = new Vector<>();
			int j;
			for(j = 0; j < numPacotes; j++){
				
				ve.addElement(crc.encriptar(gerarInformacao()));
				
				if((j + 1)%Constantes.TAMANHO_JANELA == 0){
					sr.enviarPacote(in, out, ve);
					ve = new Vector<>();
				}
			}
			if((j + 1)%Constantes.TAMANHO_JANELA != 0){
				sr.enviarPacote(in, out, ve);
			}
			out.reset();
			out.writeObject(Constantes.FIM_TRANSMISSAO);
			break;
		default:
			throw new Exception("TIPO DE PROTOCOLO INVÁLIDO!");
		}
	}
	
	private void funcionalidadeReceptor(ObjectInputStream in, ObjectOutputStream out) throws Exception{
		CRC crc = new CRC();
		while((Integer)in.readObject() != Constantes.FIM_TRANSMISSAO){
			System.out.print("\nRec:             ");
			
			Vector pacote = (Vector) in.readObject();
			int[] retorno = new int[Constantes.TAMANHO_JANELA];
			int i;
			
			for(i = 0; i < pacote.size(); i++){
				if(crc.desencriptar((int[]) pacote.get(i))){
					retorno[i] = Constantes.ACK;
				}else{
					retorno[i] = Constantes.NACK;
				}
				System.out.print(i+1+"o Pacote: ");
				int[] pct = (int[]) pacote.get(i);
				for(int j = 0; j < Constantes.NUM_BITS_PACOTE; j++){
					System.out.print(pct[j]+" ");;
				}
			}
			
			for(int k = i; k < Constantes.TAMANHO_JANELA; k++){
				retorno[k] = Constantes.NAO_USADO;
			}
			
			out.reset();
			out.writeObject(retorno);
		}
	}
	
	private int[] gerarInformacao(){
		int[] info = new int[Constantes.NUM_BITS_INFO];
		Random r = new Random();
		
		for(int i = 0; i < Constantes.NUM_BITS_INFO; i++){
			info[i] = r.nextBoolean() == true ? 1: 0;
		}
		
		return info;
	}
}