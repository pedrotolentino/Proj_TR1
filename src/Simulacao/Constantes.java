package Simulacao;

public class Constantes {
	//Constantes usadas para retorno de dados
	public static final int NACK           = 0;
    public static final int ACK            = 1;
    public static final int NAO_USADO      = 2;
	public static final int TIME_OUT       = 3;
	
	//Constantes usadas para defini��o do canal
	public static final int FIM_TRANSMISSAO = -1;
	public static final int CANAL_PRONTO   = 10;
	public static final int TRANSMISSAO    = 20;
	public static final int PROB_PERDA     = 10;
	public static final int TAXA_RUIDO     = 2;
	public static final int TEMPO_TIME_OUT = 500;
	
	//Constantes que definem o tipo de protocolo a ser utilizado
   	public static final int STOP_AND_WAIT    =  1;
    public static final int GO_BACK_N        =  2;
    public static final int SELECTIVE_REPEAT =  3;
    
    //Constantes relacionadas com os pacotes
    public static final int NUM_BITS_INFO    =  8;
    public static final int NUM_BITS_PACOTE  = 11;
    public static final int TAMANHO_JANELA   =  5;
}