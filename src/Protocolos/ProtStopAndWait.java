package Protocolos;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import Simulacao.Protocolo;

public class ProtStopAndWait implements Protocolo{
	public void enviarPacote(ObjectInputStream in, ObjectOutputStream out, Vector pacote) throws IOException, ClassNotFoundException {
		out.writeObject(pacote);
		System.out.print("Emi -> ");
		System.out.println(in.readObject());
	}
	
}
