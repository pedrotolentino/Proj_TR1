package Simulacao;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.Vector;

import Protocolos.ProtGoBackN;
import Protocolos.ProtSelectiveRepeat;
import Protocolos.ProtStopAndWait;
import Verificacao.CRC;

public class Maquina implements Runnable{
	
	ProtStopAndWait sw;
	ProtGoBackN gbn;
	ProtSelectiveRepeat sr;
	
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
		int erros = 0; 
		int enviados = 0;
		double tProp = 0;
		switch(tipoProtocolo){
		case Constantes.STOP_AND_WAIT:
			long tempoStopAndWait = System.currentTimeMillis();
			sw = new ProtStopAndWait();
			for(int i = 0; i < numPacotes; i++){
				Vector<int []> v = new Vector<>();
				
				v.addElement(crc.encriptar(gerarInformacao()));
				sw.enviarPacote(in, out, v);
				erros =+ sw.pacoteErro;
				enviados =+ sw.pacotesEnviados;
			}
			tProp = sw.tProp;
			out.writeObject(Constantes.FIM_TRANSMISSAO);
			calcularEstatistica(Constantes.STOP_AND_WAIT, System.currentTimeMillis() - tempoStopAndWait, erros, enviados, tProp);
			break;
		case Constantes.GO_BACK_N:
			long tempoGoBackN = System.currentTimeMillis();
			gbn = new ProtGoBackN();
			Vector<int []> v = new Vector<>();
			int i;
			for(i = 0; i < numPacotes; i++){
				
				v.addElement(crc.encriptar(gerarInformacao()));
				
				if((i + 1)%Constantes.TAMANHO_JANELA == 0){
					gbn.enviarPacote(in, out, v);
					erros =+ gbn.pacoteErro;
					enviados =+ gbn.pacotesEnviados;
					v = new Vector<>();
				}
			}
			if((i)%Constantes.TAMANHO_JANELA != 0 && !v.isEmpty()){
				gbn.enviarPacote(in, out, v);
				erros =+ gbn.pacoteErro;
				enviados =+ gbn.pacotesEnviados;
			}
			tProp = gbn.tProp;
			out.reset();
			out.writeObject(Constantes.FIM_TRANSMISSAO);
			calcularEstatistica(Constantes.GO_BACK_N, System.currentTimeMillis() - tempoGoBackN, erros, enviados, tProp);
			break;
		case Constantes.SELECTIVE_REPEAT:
			long tempoSelectiveRepeat = System.currentTimeMillis();
			sr = new ProtSelectiveRepeat();
			Vector<int []> ve = new Vector<>();
			int j;
			for(j = 0; j < numPacotes; j++){
				
				ve.addElement(crc.encriptar(gerarInformacao()));
			}
			sr.enviarPacote(in, out, ve);
			erros = sr.pacoteErro;
			enviados = sr.pacotesEnviados;
			tProp = sr.tProp;
			out.reset();
			out.writeObject(Constantes.FIM_TRANSMISSAO);
			calcularEstatistica(Constantes.SELECTIVE_REPEAT, System.currentTimeMillis() - tempoSelectiveRepeat, erros, enviados, tProp);
			break;
		default:
			throw new Exception("TIPO DE PROTOCOLO INVÁLIDO!");
		}
	}
	
	private void calcularEstatistica(int tipoProtocolo, long tempo, int erros, int enviados, double tProp) throws Exception {
		double taxaBits = (Constantes.NUM_BITS_INFO*numPacotes)*1000/tempo;
		StringBuilder logProtocolo = new StringBuilder();
		switch(tipoProtocolo){
		case Constantes.STOP_AND_WAIT:
			logProtocolo.append("\n############### PROTOCOLO STOP AND WAIT ###############");
			break;
		case Constantes.GO_BACK_N:
			logProtocolo.append("\n############### PROTOCOLO GO BACK AND N ###############");
			break;
		case Constantes.SELECTIVE_REPEAT:
			logProtocolo.append("\n############### PROTOCOLO SELECTIVE REPEAT ############");
			break;
		default:
			throw new Exception("TIPO DE PROTOCOLO INVÁLIDO!");
		}
		logProtocolo.append("\n## TOTAL DE PACOTES ENVIADOS: "+enviados
				         + "\n## TOTAL DE PACOTES COM ERRO: "+erros
				         + "\n## TEMPO TOTAL DE ENVIO: "+ tempo+"ms"
				         + "\n## TAXA EM bits/s: "+taxaBits
				         + "\n## TEMPO DE PROCESSAMENTO: "+tProp+"ms"
						 //+ "\n## EFICIÊNCIA DA TRANSMISSÃO: "+(Constantes.NUM_BITS_INFO/tProp)/taxaBits
				         + "\n#######################################################");
		System.out.println(logProtocolo);
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