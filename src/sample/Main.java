package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import java.io.*;
import java.net.Socket;

public class Main extends Application
{
    // constantes
    public static final String ADRESSE = "scolaide.ca";
    public static final int PORT = 51000;

    // variables membres
    private Stage mStage;
    private Button mBoutonControle;
    private Button mBoutonAvant;
    private Button mBoutonVitesse;
    private Button mBoutonGauche;
    private Button mBoutonStop;
    private Button mBoutonDroite;
    private Button mBoutonConnexion;
    private Button mBoutonArriere;
    private Button mBoutonRotation;
    private GestionnaireBouton mGestionnaire = new GestionnaireBouton();
    private boolean mControle = false;
    private boolean mConnecte = false;
    private Socket mSocket;
    private BufferedReader mReader;
    private PrintWriter mWriter;
    private String mNomRobot;
    private int mVitesse = 0;
    private int mRotation = 0;

    private Label mLabelVitesse = new Label();
    private Label mLabelRotation = new Label();
    //
    // Chargement des images et création des boutons
    //
    private void creerBoutons()
    {
            mBoutonControle = new Button("", new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("Control-2-icon.png"))));
            mBoutonControle.setPrefSize(100, 100);
            mBoutonAvant = new Button("", new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("Fleche-haut-bleue-icon.png"))));
            mBoutonAvant.setPrefSize(100, 100);
            mBoutonGauche = new Button("", new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("Fleche-gauche-bleue-icon.png"))));
            mBoutonGauche.setPrefSize(100, 100);
            mBoutonStop = new Button("", new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("Actions-process-stop-icon.png"))));
            mBoutonStop.setPrefSize(100, 100);
            mBoutonDroite = new Button("", new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("Fleche-droite-bleue-icon.png"))));
            mBoutonDroite.setPrefSize(100, 100);
            mBoutonConnexion = new Button("", new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("connect-icon.png"))));
            mBoutonConnexion.setPrefSize(100, 100);
            mBoutonArriere = new Button("", new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("Fleche-bas-bleue-icon.png"))));
            mBoutonArriere.setPrefSize(100, 100);
    }

    //
    // Création de l'interface utilisateur graphique
    //
    @Override
    public void start(Stage stage)
    {
        mStage = stage;

        // création du gestionnaire de mise en page
        GridPane pane = new GridPane();

        // création des boutons
        creerBoutons();

            mLabelVitesse.setText(" Vitesse:\n" + mVitesse);
            mLabelVitesse.setFont(new Font("Arial", 20));
            mLabelRotation.setText(" Rotation:\n" + mRotation);
            mLabelRotation.setFont(new Font("Arial", 20));

        // ajout des boutons
        pane.add(mBoutonControle, 0, 0, 1, 1);
        pane.add(mBoutonAvant, 1, 0, 1, 1);
        pane.add(mLabelVitesse, 2, 0, 1, 1);
        pane.add(mBoutonGauche, 0, 1, 1, 1);
        pane.add(mBoutonStop, 1, 1, 1, 1);
        pane.add(mBoutonDroite, 2, 1, 1, 1);
        pane.add(mBoutonConnexion, 0, 2, 1, 1);
        pane.add(mBoutonArriere, 1, 2, 1, 1);
        pane.add(mLabelRotation, 2, 2, 1, 1);

        // assignation des gestionnaires de boutons
        mBoutonControle.setOnAction(mGestionnaire);
        mBoutonAvant.setOnAction(mGestionnaire);
        mBoutonGauche.setOnAction(mGestionnaire);
        mBoutonStop.setOnAction(mGestionnaire);
        mBoutonDroite.setOnAction(mGestionnaire);
        mBoutonConnexion.setOnAction(mGestionnaire);
        mBoutonArriere.setOnAction(mGestionnaire);

        // création et affichage de la scène
        Scene scene = new Scene(pane, 300, 300);
        mStage.setScene(scene);
        mStage.setTitle("Client Procove");
        mBoutonConnexion.requestFocus();
        mStage.show();
    }

    //
    // Point d'entrée de l'application JavaFX
    //
    public static void main(String[] args)
    {
        Application.launch(args);
    }

    //
    // Connexion au serveur PROCOVE
    //
    private void connecter()
    {
        try {
            mSocket = new Socket(ADRESSE, PORT);
            mReader = new BufferedReader(
                    new InputStreamReader(mSocket.getInputStream()));
            mWriter = new PrintWriter(
                    new OutputStreamWriter(mSocket.getOutputStream()),true);
            mConnecte = true;

            mNomRobot = envoyerRequete("SPC").substring(3);
            //mNomRobot = "Samuel Lanthier";
            ///////////////////////////////////////////////////////////////////
            // IMPORTANT :                                                   //
            //                                                               //
            // TOUTE MODIFICATION À UN COMPOSANT DE L'INTERFACE UTILISATEUR  //
            // DOIT ÊTRE EFFECTUÉE PAR LE THREAD PRINCIPAL VIA CETTE MÉTHODE //
            //                                                               //
            ///////////////////////////////////////////////////////////////////
            //Platform.runLater(() -> mStage.setTitle("Coonect� � " + mNomRobot));
            mStage.setTitle("Connecter a" + mNomRobot);

        }
        catch (IOException ioe)
        {
            Platform.runLater(() -> mStage.setTitle("�chec de la connexion"));
        }
    }
    //
    // Déconnexion du serveur
    //
    private void deconnecter()
    {
        try
        {
            String reponse = envoyerRequete("FIN");
            if (reponse.equals("ACK"))
            {
                mSocket.close();
                mReader.close();
                mWriter.close();
                mConnecte = false;
                mControle = false;
                mStage.setTitle("Deconnecter");
            }
        }
        catch (IOException ioee)
        {

        }

    }
    //
    // Envoie d'une requête
    //
    public String envoyerRequete(String requete)
    {
        String reponse = "";
        try
        {
            mWriter.println(requete);
            reponse = mReader.readLine();
            System.out.println(requete + "--> " + reponse);
            return reponse;
        }
        catch (IOException e)
        {

        }
        // à compléter...
        // pour débogage
        //System.out.println(requete + "--> " + reponse);
        return reponse;
    }

    //
    // Gestion des clics sur les boutons
    //
    class GestionnaireBouton implements EventHandler
    {
        @Override
        public void handle(Event event)
        {
            Button b = (Button)event.getTarget();

            if (b == mBoutonControle)
            {
                    if (mControle == false && mConnecte == true)
                    {
                        if (envoyerRequete("CTR").equals("ACK"))
                        {
                            mControle = true;
                            System.out.println("Vous avez le controle");
                            mStage.setTitle("En controle");
                        }
                    }
                    else if (mControle == true && mConnecte == true)
                    {
                        if (envoyerRequete("rel").equals("ACK"))
                        {
                            mControle = false;
                            mStage.setTitle("Relachement du controle");
                        }
                    }
                    else
                    {
                        mStage.setTitle("Connecter vous");
                    }
            }
            else if (b == mBoutonAvant)
            {
                if (mControle)
                {
                    if (mVitesse < 100)
                    {
                        String reponse = envoyerRequete("MOT " + (mVitesse + 10));
                        if (reponse.equals("ACK"))
                        {
                            mVitesse += 10;
                            mLabelVitesse.setText("Vitesse:\n" + mVitesse);
                        }
                    }
                }
                else
                {
                    mStage.setTitle("Pas en controle");
                }
                if (!mConnecte)
                {
                    mStage.setTitle("Pas connecter");
                }
            }
            else if (b == mBoutonArriere)
            {
                if (mControle)
                {
                    if (mVitesse > -100)
                    {
                        String reponse = envoyerRequete("MOT " + (mVitesse - 10));
                        if (reponse.equals("ACK"))
                        {
                            mVitesse -= 10;
                            mLabelVitesse.setText("Vitesse:\n" + mVitesse);
                        }
                    }
                }
                else
                {
                    mStage.setTitle("Pas en controle");
                }
                if (!mConnecte)
                {
                    mStage.setTitle("Pas connecter");
                }
            }
            else if (b == mBoutonGauche)
            {
                if (mControle)
                {
                    if (mRotation > -100)
                    {
                        String reponse = envoyerRequete("ROT " + (mRotation - 10));
                        if (reponse.equals("ACK"))
                        {
                            mRotation -= 10;
                            mLabelRotation.setText("Vitesse:\n" + mRotation);
                        }
                    }
                }
                else
                {
                    mStage.setTitle("Pas en controle");
                }
                if (!mConnecte)
                {
                    mStage.setTitle("Pas connecter");
                }
            }
            else if (b == mBoutonDroite)
            {
                if (mControle)
                {
                    if (mRotation < 100)
                    {
                        String reponse = envoyerRequete("ROT " + (mRotation + 10));
                        if (reponse.equals("ACK"))
                        {
                            mRotation += 10;
                            mLabelRotation.setText("Vitesse:\n" + mRotation);
                        }
                    }
                }
                else
                {
                    mStage.setTitle("Pas en controle");
                }
                if (!mConnecte)
                {
                    mStage.setTitle("Pas connecter");
                }
            }
            else if (b == mBoutonStop)
            {
                if (mControle)
                {
                    mRotation = 0;
                    mVitesse = 0;
                    String reponse = envoyerRequete("ROT " + (mRotation));
                    String reponse2 = envoyerRequete("MOT " + (mVitesse));
                    if (reponse.equals("ACK") && reponse2.equals("ACK"))
                    {
                        mLabelRotation.setText("Vitesse:\n" + mRotation);
                        mLabelVitesse.setText("Vitesse:\n" + mVitesse);
                    }
                }
                else
                {
                    mStage.setTitle("Pas en controle");
                }
                if (!mConnecte)
                {
                    mStage.setTitle("Pas connecter");
                }
            }
            else if (b == mBoutonConnexion)
            {
                if (mConnecte == true)
                {
                    deconnecter();
                }
                else
                {
                    connecter();
                }

            }
            // à compléter...
        }
    }
}
