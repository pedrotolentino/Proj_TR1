package Simulacao;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

public interface Protocolo {
	public static final int NACK        = 0;
	public static final int ACK         = 1;
	public static final int TRANSMISSAO = 20;
	
	public void enviarPacote(ObjectInputStream in, ObjectOutputStream out, Vector pacote) throws IOException, ClassNotFoundException;
}
