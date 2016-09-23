import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.Font;

public class Principale implements ActionListener{

	private JFrame FinestraPrincipale;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Principale window = new Principale();
					window.FinestraPrincipale.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		

	}

	public Principale() throws IOException{

		/**************************
		 * Inizializzo i controlli
		 **************************/

		//Finestra principale
		FinestraPrincipale = new JFrame();

		CambiaStileGUI();

		FinestraPrincipale.setTitle("PostiRandom");
		FinestraPrincipale.setBounds(100, 100, 289, 440);
		FinestraPrincipale.setLocationRelativeTo(null);
		FinestraPrincipale.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		FinestraPrincipale.setResizable(false);
		FinestraPrincipale.getContentPane().setLayout(null);

		//Pulsante che randomizza
		JButton PulsanteRandomizza = new JButton("Randomizza!");
		PulsanteRandomizza.setToolTipText("Clicca per randomizzare i nomi del file");
		PulsanteRandomizza.setBounds(10, 351, 263, 52);
		FinestraPrincipale.getContentPane().add(PulsanteRandomizza);

		//Pulsante che apre il file NomeAlunniClasse
		JButton PulsanteApriFile = new JButton("Apri File Classe");
		PulsanteApriFile.setToolTipText("Apri il file contenente i nomi da randomizzare");
		PulsanteApriFile.setBounds(10, 323, 140, 23);
		FinestraPrincipale.getContentPane().add(PulsanteApriFile);

		//Pulsante che salva la disposizione
		JButton PulsanteSalvaFile = new JButton("Salva i Posti");
		PulsanteSalvaFile.setBounds(10, 298, 140, 23);
		PulsanteSalvaFile.setEnabled(false);
		FinestraPrincipale.getContentPane().add(PulsanteSalvaFile);

		//Etichetta che stampa la classe
		JLabel EtichettaClasse = new JLabel("Errore!");
		EtichettaClasse.setToolTipText("La tua classe");
		EtichettaClasse.setFont(new Font("Tahoma", Font.PLAIN, 13));
		EtichettaClasse.setBounds(172, 301, 65, 14);
		FinestraPrincipale.getContentPane().add(EtichettaClasse);

		//Etichetta che restituisce il numero degli alunni
		JLabel EtichettaNumeroAlunni = new JLabel("Errore!");
		EtichettaNumeroAlunni.setFont(new Font("Tahoma", Font.PLAIN, 13));
		EtichettaNumeroAlunni.setToolTipText("Il numero degli alunni (righe del file)");
		EtichettaNumeroAlunni.setBounds(172, 326, 86, 14);
		FinestraPrincipale.getContentPane().add(EtichettaNumeroAlunni);

		//Area che stampa il testo randomizzato
		JTextArea OutputTesto = new JTextArea();
		OutputTesto.setFont(new Font("Tahoma", Font.PLAIN, 13));
		OutputTesto.setToolTipText("Qui compariranno i posti in ordine random");
		OutputTesto.setEditable(false);
		OutputTesto.setBounds(10, 11, 252, 269);
		OutputTesto.setText("Benvenuto/a! \r\nQui compariranno i nomi in ordine random...\r\n\r\n");
		FinestraPrincipale.getContentPane().add(OutputTesto);

		//Scrollbar per OutputTesto
		JScrollPane Scrolla = new JScrollPane(OutputTesto);
		Scrolla.setBounds(10, 11, 263, 281);
		FinestraPrincipale.getContentPane().add(Scrolla);



		/*****************************
		 * Inizializzazione variabili
		 *****************************/

		File NomeAlunniClasse = new File("NomeAlunniClasse.psr");
		File NomeClasse = new File("NomeClasse.psr");


		/*****************************
		 * Inizializzazione all'avvio
		 *****************************/

		//Controllo se esiste il file
		ControllaEsistenzaFile(NomeAlunniClasse);
		ControllaEsistenzaFile(NomeClasse);

		//Controllo la pienezza di entrambi i file e setto il numero alunni e la classe
		OttieniClasse(NomeClasse, EtichettaClasse);
		ControllaPienezzaFile(NomeAlunniClasse, EtichettaNumeroAlunni);

		//Nascondo i file appena creati
		NascondiFile(NomeAlunniClasse);
		NascondiFile(NomeClasse);


		/*****************************
		 * Azioni dei Pulsanti 
		 *****************************/

		PulsanteRandomizza.addActionListener( new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				ControllaPienezzaFile(NomeAlunniClasse, EtichettaNumeroAlunni);

				Randomizza(NomeAlunniClasse, OutputTesto);

				//Blocco il pulsante (per bloccare i furbetti...)
				PulsanteRandomizza.setEnabled(false);

				//Sblocco il pulsante per salvare la disposizione
				PulsanteSalvaFile.setEnabled(true);

				//Setto il ToolTipText del pulsante
				PulsanteRandomizza.setToolTipText("Generato una volta! Non puoi cambiarlo... :D");
			}
		});

		PulsanteApriFile.addActionListener( new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{	
				Notepad(NomeAlunniClasse);
			}
		});

		PulsanteSalvaFile.addActionListener( new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				SalvaFilePosti(OutputTesto);
			}
		});
	}

	/*****************************
	 * Metodi vari
	 *****************************/

	//Metodo che controlla l'esistenza del file
	public static void ControllaEsistenzaFile(File FileDaLeggere){
		if (FileDaLeggere.exists()){
			//Non fa niente
		}
		else{
			//Prova a creare il file
			try {
				FileDaLeggere.createNewFile();
			} catch (IOException Errore) {
				JOptionPane.showMessageDialog(null, "Impossibile creare il file... Errore: \n" + Errore);
			}
		}
	}

	//Metodo che controlla la pienezza del file, altrimenti restituisce un errore
	public static void ControllaPienezzaFile(File FileDaLeggere, JLabel EtichettaNumeroRighe){
		while(true){
			//Numero delle righe del file
			int NumeroRighe = OttieniNumeroRighe(FileDaLeggere);

			if (NumeroRighe == 0){
				Notepad(FileDaLeggere);

				//Ottengo se preme OK o Cancella
				int OkCancella = JOptionPane.showOptionDialog(null, "Scrivi nel file un nome (e cognome) per riga, poi chiudi per salvare il file. \nPremi OK per riprovare, Cancella per chiudere il programma", "Attenzione", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null, null, null);

				//In base a ciò che ha premuto..
				switch(OkCancella){
				case(JOptionPane.OK_OPTION):
					break;
				case(JOptionPane.CANCEL_OPTION):
					//Altrimenti esco
					System.exit(0);
				}
				break;
			}	
			else{
				//Il file contiene minimo una riga
				break;
			}
		}

		//Se preme OK setto nel label il numero degli alunni
		EtichettaNumeroRighe.setText("Alunni: " + OttieniNumeroRighe(FileDaLeggere));
	}

	//Metodo che ottiene il nome della classe dal file NomeClasse
	public static void OttieniClasse(File FileDaLeggere, JLabel EtichettaClasse){
		//Inizializzo i controlli
		FileInputStream LettoreIniziale;
		try {
			LettoreIniziale = new FileInputStream(FileDaLeggere);
			BufferedReader LettoreFinale = new BufferedReader(new InputStreamReader(LettoreIniziale));

			while(true){
				//Leggo la prima riga del file
				String Classe = LettoreFinale.readLine();

				if(Classe != null){
					//Se è presente qualcosa...
					EtichettaClasse.setText("Classe: " + Classe);
					LettoreFinale.close();
					break;
				}
				else{
					//Se non è presente niente...

					//Ottengo la classe da un inputbox
					Classe = JOptionPane.showInputDialog(null, "Scrivi la tua classe. \nPremi OK per riprovare, Cancella per chiudere il programma", "Attenzione", JOptionPane.OK_OPTION);

					//Scrivo nel file la classe
					FileOutputStream ScrittoreIniziale = new FileOutputStream(FileDaLeggere);
					PrintStream ScrittoreFinale = new PrintStream(ScrittoreIniziale);
					ScrittoreFinale.println(Classe);
					ScrittoreFinale.close();

					//Ri-inizia il ciclo
				}
			}
		} catch (IOException Errore) {
			JOptionPane.showMessageDialog(null, "Impossibile interagire con il file " + FileDaLeggere + ". Errore: \n" + Errore);
		}
	}

	//Metodo che restituisce il numero degli alunni
	public static int OttieniNumeroRighe(File FileDaLeggere){
		//Creo e inizializzo la variabile che contiene il numero degli alunni
		int NumeroAlunni = 0;

		try {
			//Inizializzo i controlli
			FileInputStream LettoreIniziale = new FileInputStream(FileDaLeggere);
			BufferedReader LettoreFinale = new BufferedReader(new InputStreamReader(LettoreIniziale));

			//Creo e inizializzo la variabile che controlla se la linea è piena o vuota
			String LineaPienaVuota;

			while(true){
				LineaPienaVuota = LettoreFinale.readLine();
				if (LineaPienaVuota != null){
					NumeroAlunni++;
				}
				else{
					LettoreFinale.close();
					break;
				}
			}
		}
		catch (IOException Errore) {
			JOptionPane.showMessageDialog(null, "Impossibile interagire con il file " + FileDaLeggere + ". Errore: \n" + Errore);
		}
		return NumeroAlunni;
	} 

	//Metodo che aggiunge elementi all'arraylist per ogni riga del file
	public static void AggiungiAlunni(ArrayList<String> ListaNomi, File FileDaLeggere){
		try {
			//Inizializzo i controlli
			FileInputStream LettoreIniziale = new FileInputStream(FileDaLeggere);
			BufferedReader LettoreFinale = new BufferedReader(new InputStreamReader(LettoreIniziale));

			while(true){
				String NomeAlunno = LettoreFinale.readLine();
				if (NomeAlunno != null){
					ListaNomi.add(NomeAlunno);
				}
				else{
					LettoreFinale.close();
					break;
				}
			}
		}
		catch (IOException Errore) {
			JOptionPane.showMessageDialog(null, "Impossibile interagire con il file " + FileDaLeggere + ". Errore: \n" + Errore);
		}
	} 

	//Metodo che randomizza i nomi
	public static void Randomizza(File FileDaLeggere, JTextArea OutputTesto){

		//Creo la Lista
		ArrayList<String> ListaNomi = new ArrayList<String>();

		//Aggiungo tanti elementi quante le righe del file
		AggiungiAlunni(ListaNomi, FileDaLeggere);

		//Creo la lunghezza iniziale
		int LunghezzaIniziale = ListaNomi.size();

		for(int NumeroPosto = 1; NumeroPosto < (LunghezzaIniziale + 1); NumeroPosto++){
			//Inizializzo il Generatore Random
			Random Generatore = new Random();

			//Genera un numero random
			int NumeroRandom = Generatore.nextInt(ListaNomi.size());

			//Stampa il posto e il nome alunno
			String TestoIniziale = OutputTesto.getText().toString();
			OutputTesto.setText(TestoIniziale + "Il posto numero " + NumeroPosto + " e': " + ListaNomi.get(NumeroRandom).toString() + "\r\n");

			//Rimuovi l'elemento dalla lista
			ListaNomi.remove(NumeroRandom);
		}
	}

	//Metodo che interagisce con l'utente per salvare i posti
	public void SalvaFilePosti(JTextArea OutputTesto){
		//Ottengo il nome del file
		String NomeFileDisposizione = JOptionPane.showInputDialog(FinestraPrincipale, "Con che nome vuoi salvare i posti?");

		//Se il nome è diverso da niente
		if (NomeFileDisposizione != null){
			File FileDisposizione = new File(NomeFileDisposizione + ".txt");

			//Se il file non esiste
			if(FileDisposizione.exists() == false){
				try {
					//Creo il file su cui andrò a scrivere
					FileDisposizione.createNewFile();

					//Inizializzo lo scrittore
					PrintWriter Scrittore = new PrintWriter(FileDisposizione, "UTF-8");

					//Ottengo il testo dalla TextArea
					String TestoRandomizzato = OutputTesto.getText().toString();

					//Scrivo sul file
					Scrittore.write(TestoRandomizzato);

					//Chiudo lo scrittore
					Scrittore.close();

					//Apro il file
					Desktop.getDesktop().open(FileDisposizione);
				} catch (IOException Errore) {
					JOptionPane.showMessageDialog(null, "Impossibile creare il file " + FileDisposizione + ". Errore: \n" + Errore);
				}
			}

			//Se il file esiste già
			else{
				JOptionPane.showMessageDialog(null,  "Il file esiste già..." , "Attenzione" , JOptionPane.OK_OPTION);
			}

			//Se il nome è uguale a niente (se l'utente ha premuto annulla)
		}	
		else{
			JOptionPane.showMessageDialog(null,  "File non creato..." , "Attenzione" , JOptionPane.OK_OPTION);	
		}
	}

	//Metodo che preleva le righe dal file alunni e le porta nel notepad
	public static void LeggiFile(File FileDaLeggere, JTextArea AreaTesto){
		try {
			//Inizializzo i controlli
			FileInputStream LettoreIniziale = new FileInputStream(FileDaLeggere);
			BufferedReader LettoreFinale = new BufferedReader(new InputStreamReader(LettoreIniziale));

			while(true){
				String NomeAlunno = LettoreFinale.readLine();
				if (NomeAlunno != null){
					AreaTesto.append(NomeAlunno + "\r\n");
				}
				else{
					LettoreFinale.close();
					break;
				}
			}
		}
		catch (IOException Errore) {
			JOptionPane.showMessageDialog(null, "Impossibile interagire con il file " + FileDaLeggere + ". Errore: \n" + Errore);
		}
	} 

	//Metodo che apre il notepad personalizzato sul file
	public static void Notepad(File FileDaLeggere){
		JFrame Notepad = new JFrame();

		Notepad.setTitle(FileDaLeggere.toString());
		Notepad.setBounds(100, 100, 299, 453);
		Notepad.setLocationRelativeTo(null);
		Notepad.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		Notepad.setResizable(false);
		Notepad.getContentPane().setLayout(null);

		JTextArea AreaTesto = new JTextArea();
		AreaTesto.setFont(new Font("Tahoma", Font.PLAIN, 12));
		AreaTesto.setEditable(true);

		JScrollPane ScrollaNotepad = new JScrollPane(AreaTesto);
		ScrollaNotepad.setBounds(10, 10, 273, 400);

		Notepad.getContentPane().add(ScrollaNotepad);

		Notepad.setVisible(true);

		LeggiFile(FileDaLeggere, AreaTesto);

		Notepad.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {

				try {

					//Mostro la ConfirmDialog di uscita e salvataggio
					int Salvare = JOptionPane.showConfirmDialog(Notepad, "Vuoi salvare le modifiche?", "Salvare il file?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

					switch(Salvare){
					case(JOptionPane.YES_OPTION):

						//Elimino e ricreo il file (così vuoto)
						FileDaLeggere.delete();
					FileDaLeggere.createNewFile();

					//Inizializzo lo scrittore
					PrintStream Scrittore = new PrintStream(FileDaLeggere);

					//Scrivo nel file il contenuto della TextArea
					Scrittore.print(AreaTesto.getText().toString());

					//Chiudo lo scrittore
					Scrittore.close();

					//Chiudo la finestra e azzero il contenuto della TextArea
					AreaTesto.setText("");
					Notepad.setVisible(false);
					break;

					case(JOptionPane.NO_OPTION):

						//Chiudo il notepad
						Notepad.setVisible(false);
					break;

					default:

						//Non fa niente
					}
				} catch (IOException Errore) {
					JOptionPane.showMessageDialog(null, "Impossibile interagire con il file " + FileDaLeggere + ". Errore: \n" + Errore);
				}
			}
		});
	}

	//Metodo che nasconde un file
	public void NascondiFile(File FileDaNascondere){
		try {
			Runtime.getRuntime().exec("attrib +H " + FileDaNascondere);
		} catch (IOException Errore) {
			JOptionPane.showMessageDialog(null, "Impossibile interagire con il file " + FileDaNascondere + ". Errore: \n" + Errore);
		}
	}

	//Metodo che serve a fare funzionare i pulsanti
	@Override
	public void actionPerformed(ActionEvent e) {
		//Non so cosa mettere :D
	}	

	//Metodo che rileva il sistema operativo e cambia la GUI per renderla simile
	public void CambiaStileGUI(){
		try {
			if ((System.getProperty("os.name").toLowerCase()).contains("win")){
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			}

			else {
				if ((System.getProperty("os.name").toLowerCase()).contains("nix")){
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
				}
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException Errore) {
			JOptionPane.showMessageDialog(null, "Impossibile determinare il sistema operativo per la GUI, errore: \n" + Errore);
		}
	}
}
